package pl.oxerek.reactiveportsadapters.adapters.inbound.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.oxerek.reactiveportsadapters.adapters.inbound.model.PaymentRestRequest;
import pl.oxerek.reactiveportsadapters.adapters.inbound.model.PaymentRestResponse;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;

@Mapper
public interface PaymentRestMapper {

    PaymentRestMapper INSTANCE = Mappers.getMapper(PaymentRestMapper.class);

    @Mapping(target = "id", ignore = true)
    PaymentDto requestToDto(PaymentRestRequest paymentRestRequest);

    PaymentRestResponse dtoToResponse(PaymentDto paymentDto);
}
