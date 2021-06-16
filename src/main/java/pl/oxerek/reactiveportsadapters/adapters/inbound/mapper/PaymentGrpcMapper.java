package pl.oxerek.reactiveportsadapters.adapters.inbound.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.oxerek.reactiveportsadapters.adapters.inbound.model.PaymentGrpcRequest;
import pl.oxerek.reactiveportsadapters.adapters.inbound.model.PaymentGrpcResponse;
import pl.oxerek.reactiveportsadapters.application.AdaptersConfiguration.PaymentMappersConfig;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;

@Mapper(config = PaymentMappersConfig.class)
public interface PaymentGrpcMapper {

    PaymentGrpcMapper INSTANCE = Mappers.getMapper(PaymentGrpcMapper.class);

    @Mapping(target = "id", ignore = true)
    PaymentDto requestToDto(PaymentGrpcRequest paymentRestRequest);

    PaymentGrpcResponse dtoToResponse(PaymentDto paymentDto);
}
