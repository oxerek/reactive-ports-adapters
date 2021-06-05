package pl.oxerek.reactiveportsadapters.application;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "adapters")
public class AdaptersProperties {

    String hazelcastInstanceName;

    String hazelcastCacheName;

    int hazelcastTtl;

    boolean hazelcastMulticast;

    boolean hazelcastKubernetes;
}
