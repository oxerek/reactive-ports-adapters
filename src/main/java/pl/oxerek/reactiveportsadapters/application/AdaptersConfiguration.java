package pl.oxerek.reactiveportsadapters.application;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.KubernetesConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.oxerek.reactiveportsadapters.adapters.inbound.PaymentRestHandler;
import pl.oxerek.reactiveportsadapters.adapters.outbound.PaymentInMemoryRepository;
import pl.oxerek.reactiveportsadapters.adapters.outbound.model.PaymentInMemoryEntity;
import pl.oxerek.reactiveportsadapters.domain.ports.PaymentsFacade;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;

@Configuration
@EnableConfigurationProperties(AdaptersProperties.class)
class AdaptersConfiguration {

    @Bean
    AdaptersProperties adaptersProperties() {
        return new AdaptersProperties();
    }

    @Bean
    public PaymentRestHandler paymentRestHandler(PaymentsFacade paymentsFacade) {
        return new PaymentRestHandler(paymentsFacade);
    }

    @Bean
    public RouterFunction<ServerResponse> paymentRestRoutes(PaymentRestHandler paymentRestHandler) {
        return RouterFunctions.route()
              .POST("/payment", accept(APPLICATION_JSON), paymentRestHandler::createPayment)
              .GET("/payments", paymentRestHandler::getPayments)
              .build();
    }

    @Bean
    @ConditionalOnBean(name = "inMemoryStore")
    Repository<PaymentDto> inMemoryRepository(Map<UUID, PaymentInMemoryEntity> inMemoryStore) {
        return new PaymentInMemoryRepository(inMemoryStore);
    }

    @Bean
    @ConditionalOnProperty(prefix = "adapters", name = "storage", havingValue = "map")
    Map<UUID, PaymentInMemoryEntity> inMemoryStore() {
        return new ConcurrentHashMap<>();
    }

    @ConditionalOnProperty(prefix = "adapters", name = "storage", havingValue = "hazelcast")
    static class InMemoryHazelcastStoreConfiguration {

        @Bean
        public Map<UUID, PaymentInMemoryEntity> inMemoryStore(
              @Qualifier("hazelcastInstance") HazelcastInstance hazelcast,
              AdaptersProperties adaptersProperties
        ) {
            return hazelcast.getMap(adaptersProperties.getHazelcastCacheName());
        }

        @Bean
        public Config hazelcastConfiguration(AdaptersProperties adaptersProperties) {
            return new Config().setInstanceName(adaptersProperties.getHazelcastInstanceName())
                  .setNetworkConfig(new NetworkConfig().setJoin(new JoinConfig()
                        .setKubernetesConfig(new KubernetesConfig().setEnabled(adaptersProperties.isHazelcastKubernetes()))
                        .setMulticastConfig(new MulticastConfig().setEnabled(adaptersProperties.isHazelcastMulticast())))
                  ).addMapConfig(new MapConfig()
                        .setName(adaptersProperties.getHazelcastCacheName())
                        .setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionPolicy.NONE).setMaxSizePolicy(MaxSizePolicy.FREE_HEAP_SIZE))
                        .setTimeToLiveSeconds(adaptersProperties.getHazelcastTtl()));
        }

    }
}
