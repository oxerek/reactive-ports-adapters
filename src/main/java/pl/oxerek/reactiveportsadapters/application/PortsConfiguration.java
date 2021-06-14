package pl.oxerek.reactiveportsadapters.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.oxerek.reactiveportsadapters.domain.ports.PaymentsFacade;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;

@Configuration
class PortsConfiguration {

    @Bean
    PaymentsFacade paymentsFacade(Repository<PaymentDto> repository) {
        return new PaymentsFacade(repository);
    }
}
