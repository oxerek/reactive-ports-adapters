package pl.oxerek.reactiveportsadapters.application

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@TestPropertySource("classpath:test.properties")
@SpringBootTest(classes = ApplicationInitializer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdaptersConfigurationSpec extends Specification {

    def "should load context correctly"() {

        expect:
        1 == 1
    }
}
