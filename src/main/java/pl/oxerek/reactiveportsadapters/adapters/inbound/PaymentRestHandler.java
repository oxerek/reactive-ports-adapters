package pl.oxerek.reactiveportsadapters.adapters.inbound;

import static java.util.Optional.ofNullable;
import static java.util.UUID.fromString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static pl.oxerek.reactiveportsadapters.adapters.inbound.mapper.PaymentRestMapper.INSTANCE;
import static pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto.of;
import static reactor.core.publisher.Mono.just;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
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
              .flatMap(paymentRestRequest -> paymentsFacade.createOrUpdatePayment(INSTANCE.requestToDto(paymentRestRequest)))
                    .map(INSTANCE::dtoToResponse)
                    .flatMap(paymentRestResponse -> created(uriOfCreatedPayment(request, paymentRestResponse.getId()))
                          .contentType(APPLICATION_JSON)
                          .body(just(paymentRestResponse), PaymentRestResponse.class));
    }

    public Mono<ServerResponse> updatePayment(ServerRequest request) {
        return request.bodyToMono(PaymentRestRequest.class)
              .flatMap(paymentRestRequest -> paymentsFacade.createOrUpdatePayment(of(INSTANCE.requestToDto(paymentRestRequest), idFromPathVariable(request)))
                    .map(INSTANCE::dtoToResponse)
                    .flatMap(paymentRestResponse -> ok().contentType(APPLICATION_JSON).body(just(paymentRestResponse), PaymentRestResponse.class))
                    .switchIfEmpty(notFound().build()));
    }

    public Mono<ServerResponse> modifyPayment(ServerRequest request) {
        return request.bodyToMono(PaymentRestRequest.class)
              .flatMap(paymentRestRequest -> paymentsFacade.modifyPayment(INSTANCE.requestToDto(paymentRestRequest), idFromPathVariable(request))
                    .map(INSTANCE::dtoToResponse)
                    .flatMap(paymentRestResponse -> ok().contentType(APPLICATION_JSON).body(just(paymentRestResponse), PaymentRestResponse.class))
                    .switchIfEmpty(notFound().build()));
    }

    public Mono<ServerResponse> deletePayment(ServerRequest request) {
        return paymentsFacade.deletePayment(idFromPathVariable(request))
              .flatMap(paymentDto -> ok().build())
              .switchIfEmpty(notFound().build());
    }

    @SuppressWarnings("java:S5411")
    public Mono<ServerResponse> getPayments(ServerRequest request) {
        var payments = paymentsFacade.getPayments(idsFromPathVariable(request)).map(INSTANCE::dtoToResponse);

        return payments.hasElements()
              .flatMap(hasElements -> hasElements ? ok().contentType(APPLICATION_JSON).body(payments, PaymentRestResponse.class) : notFound().build());
    }

    public Mono<ServerResponse> getPayment(ServerRequest request) {
        return paymentsFacade.getPayment(idFromPathVariable(request)).map(INSTANCE::dtoToResponse)
              .flatMap(paymentRestResponse -> ok()
                    .contentType(APPLICATION_JSON)
                    .body(just(paymentRestResponse), PaymentRestResponse.class))
                    .switchIfEmpty(notFound().build());
    }

    private UUID idFromPathVariable(ServerRequest request) {
        return fromString(request.pathVariable("id"));
    }

    private Set<UUID> idsFromPathVariable(ServerRequest request) {
        return ofNullable(request.pathVariables().get("ids"))
              .filter(StringUtils::isNotBlank)
              .stream()
              .flatMap(pathVariable -> Arrays.stream(pathVariable.split(",")))
              .map(String::trim)
              .map(UUID::fromString)
              .collect(Collectors.toSet());
    }

    private URI uriOfCreatedPayment(ServerRequest request, UUID id) {
        return URI.create(request.exchange().getRequest().getURI() + "/" + id);
    }
}
