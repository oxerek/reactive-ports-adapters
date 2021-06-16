package pl.oxerek.reactiveportsadapters.adapters.inbound;

import static java.util.Optional.of;
import static pl.oxerek.reactiveportsadapters.adapters.inbound.mapper.PaymentGrpcMapper.INSTANCE;
import static pl.oxerek.reactiveportsadapters.adapters.inbound.model.PaymentGrpcResponse.getDefaultInstance;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.StringUtils;
import pl.oxerek.reactiveportsadapters.adapters.inbound.model.PaymentGrpcRequest;
import pl.oxerek.reactiveportsadapters.adapters.inbound.model.PaymentGrpcResponse;
import pl.oxerek.reactiveportsadapters.adapters.inbound.model.PaymentWithIdGrpcRequest;
import pl.oxerek.reactiveportsadapters.application.AdaptersConfiguration.PaymentMappersConfig.StringValueUUIDMapper;
import pl.oxerek.reactiveportsadapters.domain.ports.PaymentsFacade;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@GrpcService
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PaymentGrpcService extends ReactorPaymentServiceGrpc.PaymentServiceImplBase {

    StringValueUUIDMapper stringValueUUIDMapper = new StringValueUUIDMapper();

    PaymentsFacade paymentsFacade;

    @Override
    public Mono<PaymentGrpcResponse> createPayment(Mono<PaymentGrpcRequest> request) {
        return request.map(INSTANCE::requestToDto)
              .flatMap(paymentsFacade::createOrUpdatePayment)
              .map(INSTANCE::dtoToResponse);
    }

    @Override
    public Mono<PaymentGrpcResponse> updatePayment(Mono<PaymentWithIdGrpcRequest> request) {
        return request.flatMap(paymentWithIdGrpcRequest -> idFromStringValue(paymentWithIdGrpcRequest.getId())
              .map(id -> PaymentDto.of(INSTANCE.requestToDto(paymentWithIdGrpcRequest.getPayment()), id)))
              .flatMap(paymentsFacade::createOrUpdatePayment)
              .map(INSTANCE::dtoToResponse)
              .switchIfEmpty(Mono.just(getDefaultInstance()));
    }

    @Override
    public Mono<PaymentGrpcResponse> modifyPayment(Mono<PaymentWithIdGrpcRequest> request) {
        return request.flatMap(paymentWithIdGrpcRequest -> idFromStringValue(paymentWithIdGrpcRequest.getId())
              .flatMap(id -> paymentsFacade.modifyPayment(INSTANCE.requestToDto(paymentWithIdGrpcRequest.getPayment()), id))
              .map(INSTANCE::dtoToResponse)
              .switchIfEmpty(Mono.just(getDefaultInstance())));
    }

    @Override
    public Mono<Empty> deletePayment(Mono<StringValue> request) {
        return request.flatMap(this::idFromStringValue)
              .flatMap(paymentsFacade::deletePayment)
              .then(Mono.just(Empty.getDefaultInstance()));
    }

    @Override
    public Mono<PaymentGrpcResponse> getPayment(Mono<StringValue> request) {
        return request.flatMap(this::idFromStringValue)
              .flatMap(paymentsFacade::getPayment)
              .map(INSTANCE::dtoToResponse)
              .switchIfEmpty(Mono.just(getDefaultInstance()));
    }

    @Override
    public Flux<PaymentGrpcResponse> getPayments(Mono<StringValue> request) {
        return request.flatMap(this::idsFromStringValue)
              .flatMapMany(paymentsFacade::getPayments)
              .map(INSTANCE::dtoToResponse)
              .switchIfEmpty(Flux.just(getDefaultInstance()));
    }

    private Mono<UUID> idFromStringValue(StringValue stringValue) {
        var id = stringValueUUIDMapper.toUuid(stringValue);
        return id != null ? Mono.just(id) : Mono.empty();
    }

    private Mono<Set<UUID>> idsFromStringValue(StringValue stringValue) {
        return Mono.just(of(stringValue.getValue())
              .filter(StringUtils::isNotBlank)
              .stream()
              .flatMap(value -> Arrays.stream(value.split(",")))
              .map(String::trim)
              .map(UUID::fromString)
              .collect(Collectors.toSet()));
    }
}
