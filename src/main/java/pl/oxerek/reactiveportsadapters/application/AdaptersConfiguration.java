package pl.oxerek.reactiveportsadapters.application;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.oxerek.reactiveportsadapters.adapters.outbound.PaymentInMemoryRepository;
import pl.oxerek.reactiveportsadapters.adapters.outbound.model.PaymentInMemoryEntity;
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
