package pl.oxerek.reactiveportsadapters.adapters.outbound.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.oxerek.reactiveportsadapters.adapters.outbound.model.PaymentCsvRecord;
import pl.oxerek.reactiveportsadapters.application.AdaptersConfiguration.PaymentMappersConfig;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;

@Mapper(config = PaymentMappersConfig.class)
public interface PaymentCsvMapper {

    PaymentCsvMapper INSTANCE = Mappers.getMapper(PaymentCsvMapper.class);

    @Mapping(target = "id", ignore = true)
    PaymentCsvRecord dtoToRecord(PaymentDto paymentDto);

    PaymentCsvRecord dtoToRecordWithId(PaymentDto paymentDto);

    PaymentDto recordToDto(PaymentCsvRecord paymentCsvRecord);
}
