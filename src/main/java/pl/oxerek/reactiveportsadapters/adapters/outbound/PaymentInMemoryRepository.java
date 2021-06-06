package pl.oxerek.reactiveportsadapters.adapters.outbound;

import static pl.oxerek.reactiveportsadapters.adapters.outbound.mapper.PaymentInMemoryMapper.INSTANCE;
import static pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto.of;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.adapters.outbound.model.PaymentInMemoryEntity;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PaymentInMemoryRepository implements Repository<PaymentDto> {

    Map<UUID, PaymentInMemoryEntity> store;

    @Override
    public Mono<PaymentDto> findById(UUID id) {
        return Mono.justOrEmpty(INSTANCE.entityToDto(store.get(id)));
    }

    @Override
    public Flux<PaymentDto> findAll() {
        return Flux.just(store.values().stream()
              .map(INSTANCE::entityToDto)
              .toArray(PaymentDto[]::new));
    }

    @Override
    public Flux<PaymentDto> findAll(Set<UUID> ids) {
        return Flux.just(store.values().stream()
              .map(INSTANCE::entityToDto)
              .filter(paymentDto -> ids.contains(paymentDto.id().orElseThrow()))
              .toArray(PaymentDto[]::new));
    }

    @Override
    public Mono<PaymentDto> create(PaymentDto dto) {
        return Mono.just(UUID.randomUUID())
              .map(uuid -> store.computeIfAbsent(uuid, id -> INSTANCE.dtoToEntity(of(dto, id))))
              .map(INSTANCE::entityToDto);
    }

    @Override
    public Mono<PaymentDto> update(PaymentDto dto) {
        return Mono.justOrEmpty(store.computeIfPresent(dto.id().orElseThrow(), (uuid, paymentInMemoryEntity) -> INSTANCE.dtoToEntity(of(dto, uuid))))
              .map(INSTANCE::entityToDto);
/*
        return Mono.justOrEmpty(store.put(dto.id().orElseThrow(), INSTANCE.dtoToEntity(dto)))
              .map(INSTANCE::entityToDto);*/
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return Mono.justOrEmpty(store.remove(id)).then();
    }
}
