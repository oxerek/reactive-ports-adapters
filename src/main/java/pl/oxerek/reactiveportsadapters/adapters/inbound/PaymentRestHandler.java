package pl.oxerek.reactiveportsadapters.adapters.inbound;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static pl.oxerek.reactiveportsadapters.adapters.inbound.mapper.PaymentRestMapper.INSTANCE;

import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.oxerek.reactiveportsadapters.adapters.inbound.model.PaymentRestRequest;
import pl.oxerek.reactiveportsadapters.adapters.inbound.model.PaymentRestResponse;
import pl.oxerek.reactiveportsadapters.domain.ports.PaymentsFacade;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PaymentRestHandler {

    PaymentsFacade paymentsFacade;

    public Mono<ServerResponse> createPayment(ServerRequest request) {
        return request.bodyToMono(PaymentRestRequest.class)
              .flatMap(paymentRestRequest -> ServerResponse.status(CREATED)
                    .contentType(APPLICATION_JSON)
                    .body(paymentsFacade.createOrModifyPayment(INSTANCE.requestToDto(paymentRestRequest))
                          .map(INSTANCE::dtoToResponse), PaymentRestResponse.class));
    }

    public Mono<ServerResponse> getPayments(ServerRequest request) {
        return ServerResponse.ok()
              .contentType(APPLICATION_JSON)
              .body(paymentsFacade.getPayments(Set.of()).map(INSTANCE::dtoToResponse), PaymentRestResponse.class);
    }
}
