package pl.oxerek.reactiveportsadapters.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "adapters")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdaptersProperties {

    String csvFileName;

    String hazelcastInstanceName;

    String hazelcastCacheName;

    int hazelcastTtl;

    boolean hazelcastMulticast;

    boolean hazelcastKubernetes;
}
