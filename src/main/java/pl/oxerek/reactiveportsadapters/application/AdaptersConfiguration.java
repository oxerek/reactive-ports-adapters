package pl.oxerek.reactiveportsadapters.application;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import com.google.protobuf.StringValue;
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
import lombok.NoArgsConstructor;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.oxerek.reactiveportsadapters.adapters.inbound.PaymentGrpcService;
import pl.oxerek.reactiveportsadapters.adapters.inbound.PaymentRestHandler;
import pl.oxerek.reactiveportsadapters.adapters.outbound.PaymentCsvRepository;
import pl.oxerek.reactiveportsadapters.adapters.outbound.PaymentInMemoryRepository;
import pl.oxerek.reactiveportsadapters.adapters.outbound.model.PaymentInMemoryEntity;
import pl.oxerek.reactiveportsadapters.application.AdaptersConfiguration.PaymentMappersConfig.StringUUIDMapper;
import pl.oxerek.reactiveportsadapters.application.AdaptersConfiguration.PaymentMappersConfig.StringValueStringMapper;
import pl.oxerek.reactiveportsadapters.application.AdaptersConfiguration.PaymentMappersConfig.StringValueUUIDMapper;
import pl.oxerek.reactiveportsadapters.domain.ports.PaymentsFacade;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;

@Configuration
@EnableConfigurationProperties(AdaptersProperties.class)
public class AdaptersConfiguration {

    private static final String GET_PUT_PATCH_DELETE_PATTERN = "/payment/{id}";
    private static final String GET_MANY_BY_IDS_PATTERN = "/payments/{ids}";
    private static final String GET_MANY_PATTERN = "/payments";
    private static final String POST_PATTERN = "/payment";

    @Bean
    AdaptersProperties adaptersProperties() {
        return new AdaptersProperties();
    }

    @Bean
    PaymentRestHandler paymentRestHandler(PaymentsFacade paymentsFacade) {
        return new PaymentRestHandler(paymentsFacade);
    }

    @Bean
    RouterFunction<ServerResponse> paymentRestRoutes(PaymentRestHandler paymentRestHandler) {
        return RouterFunctions.route()
              .POST(POST_PATTERN, accept(APPLICATION_JSON), paymentRestHandler::createPayment)
              .PUT(GET_PUT_PATCH_DELETE_PATTERN, accept(APPLICATION_JSON), paymentRestHandler::updatePayment)
              .PATCH(GET_PUT_PATCH_DELETE_PATTERN, accept(APPLICATION_JSON), paymentRestHandler::modifyPayment)
              .DELETE(GET_PUT_PATCH_DELETE_PATTERN, paymentRestHandler::deletePayment)
              .GET(GET_PUT_PATCH_DELETE_PATTERN, paymentRestHandler::getPayment)
              .GET(GET_MANY_PATTERN, paymentRestHandler::getPayments)
              .GET(GET_MANY_BY_IDS_PATTERN, paymentRestHandler::getPayments)
              .build();
    }

    @Bean
    PaymentGrpcService paymentGrpcService(PaymentsFacade paymentsFacade) {
        return new PaymentGrpcService(paymentsFacade);
    }

    @Bean
    @ConditionalOnBean(name = "paymentInMemoryStore")
    Repository<PaymentDto> paymentInMemoryRepository(Map<UUID, PaymentInMemoryEntity> inMemoryStore) {
        return new PaymentInMemoryRepository(inMemoryStore);
    }

    @Bean
    @ConditionalOnProperty(prefix = "adapters", name = "storage", havingValue = "csv")
    Repository<PaymentDto> paymentCsvRepository(AdaptersProperties adaptersProperties) {
        return new PaymentCsvRepository(adaptersProperties.getCsvFileName());
    }

    @Bean
    @ConditionalOnProperty(prefix = "adapters", name = "storage", havingValue = "map")
    Map<UUID, PaymentInMemoryEntity> paymentInMemoryStore() {
        return new ConcurrentHashMap<>();
    }

    @ConditionalOnProperty(prefix = "adapters", name = "storage", havingValue = "hazelcast")
    static class InMemoryHazelcastStoreConfiguration {

        @Bean
        Map<UUID, PaymentInMemoryEntity> paymentInMemoryStore(
              @Qualifier("hazelcastInstance") HazelcastInstance hazelcast,
              AdaptersProperties adaptersProperties
        ) {
            return hazelcast.getMap(adaptersProperties.getHazelcastCacheName());
        }

        @Bean
        Config hazelcastConfiguration(AdaptersProperties adaptersProperties) {
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

    @MapperConfig(
          unmappedTargetPolicy = ReportingPolicy.IGNORE,
          uses = { StringUUIDMapper.class, StringValueStringMapper.class, StringValueUUIDMapper.class }
    )
    public interface PaymentMappersConfig {

        @NoArgsConstructor
        class StringUUIDMapper {

            public UUID toUuid(String value) {
                return value.isEmpty() ? null : UUID.fromString(value);
            }

            public String toStringValue(UUID uuid) {
                return uuid.toString();
            }
        }

        @NoArgsConstructor
        class StringValueUUIDMapper {

            public UUID toUuid(StringValue stringValue) {
                var value = stringValue.getValue();

                return value.isEmpty() ? null : UUID.fromString(value);
            }

            public StringValue toStringValue(UUID uuid) {
                return StringValue.of(uuid.toString());
            }
        }

        @NoArgsConstructor
        class StringValueStringMapper {

            public String toString(StringValue stringValue) {
                var value = stringValue.getValue();

                return value.isEmpty() ? null : value;
            }

            public StringValue toStringValue(String string) {
                return string.isEmpty() ? StringValue.getDefaultInstance() : StringValue.of(string);
            }
        }
    }
}
