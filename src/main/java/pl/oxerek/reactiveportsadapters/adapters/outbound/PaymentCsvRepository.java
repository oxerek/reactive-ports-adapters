package pl.oxerek.reactiveportsadapters.adapters.outbound;

import static java.util.stream.Collectors.toSet;
import static pl.oxerek.reactiveportsadapters.adapters.outbound.mapper.PaymentCsvMapper.INSTANCE;

import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PaymentCsvRepository extends PaymentCsvProcessor implements Repository<PaymentDto> {

    Scheduler scheduler = Schedulers.boundedElastic();

    public PaymentCsvRepository(String csvFileName) {
        super(csvFileName);
    }

    @Override
    public Mono<PaymentDto> findById(UUID id) {
        return Mono.just(id)
              .map(UUID::toString)
              .map(this::findRecord)
              .flatMap(paymentCsvRecord -> paymentCsvRecord.map(csvRecord -> Mono.just(INSTANCE.recordToDto(csvRecord))).orElseGet(Mono::empty))
              .subscribeOn(scheduler);
    }

    @Override
    public Flux<PaymentDto> findAll() {
        return Flux.just(findRecords(Set.of()).stream().map(INSTANCE::recordToDto).toArray(PaymentDto[]::new))
              .subscribeOn(scheduler);
    }

    @Override
    public Flux<PaymentDto> findAll(Set<UUID> ids) {
        return Flux.just(findRecords(ids.stream().map(UUID::toString).collect(toSet())).stream().map(INSTANCE::recordToDto).toArray(PaymentDto[]::new))
              .subscribeOn(scheduler);
    }

    @Override
    public Mono<PaymentDto> create(PaymentDto dto) {
        return Mono.just(dto)
              .map(paymentCsvRecord -> writeOrAppendRecord(INSTANCE.dtoToRecord(dto)))
              .map(savedPaymentCsvRecord -> PaymentDto.of(dto, savedPaymentCsvRecord.getIdAsUUID()))
              .subscribeOn(scheduler);
    }

    @Override
    public Mono<PaymentDto> update(PaymentDto dto) {
        return Mono.just(dto)
              .flatMap(paymentDto -> {
                  var updatedRecord = replaceRecord(INSTANCE.dtoToRecordWithId(paymentDto));
                  return updatedRecord != null ? Mono.just(INSTANCE.recordToDto(updatedRecord)) : Mono.empty();
              }).subscribeOn(scheduler);
    }

    @Override
    public Mono<PaymentDto> delete(UUID id) {
        return Mono.just(id)
              .flatMap(uuid -> {
                  var deletedRecord = removeRecord(id.toString());
                  return deletedRecord != null ? Mono.just(INSTANCE.recordToDto(deletedRecord)) : Mono.empty();
              }).subscribeOn(scheduler);
    }
}
