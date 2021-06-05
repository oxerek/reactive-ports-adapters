package pl.oxerek.reactiveportsadapters.adapters.outbound.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pl.oxerek.reactiveportsadapters.adapters.outbound.model.PaymentInMemoryEntity;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;

@Mapper
public interface PaymentInMemoryMapper {

    PaymentInMemoryMapper INSTANCE = Mappers.getMapper(PaymentInMemoryMapper.class);

    PaymentInMemoryEntity dtoToEntity(PaymentDto paymentDto);

    PaymentDto entityToDto(PaymentInMemoryEntity paymentInMemoryEntity);
}
