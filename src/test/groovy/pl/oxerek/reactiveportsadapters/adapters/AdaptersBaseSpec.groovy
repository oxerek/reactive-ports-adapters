package pl.oxerek.reactiveportsadapters.adapters

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import pl.oxerek.reactiveportsadapters.application.ApplicationInitializer

@SpringBootTest(classes = ApplicationInitializer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
abstract class AdaptersBaseSpec {

}
