package pl.oxerek.reactiveportsadapters;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.oxerek.reactiveportsadapters.application.ApplicationInitializer;

@SpringBootTest(classes = ApplicationInitializer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "classpath:test.properties", properties = {"adapters.storage=hazelcast"})
class ContextLoadTest {

    @Test
    void contextLoads() {}

}
