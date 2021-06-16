package pl.oxerek.reactiveportsadapters.adapters.outbound.model;

import static java.util.UUID.fromString;

import java.util.UUID;
import lombok.Data;
import org.apache.commons.csv.CSVRecord;

@Data
public class PaymentCsvRecord {

    String id;

    String amount;

    String currency;

    String userId;

    String targetAccountNumber;

    public Object[] toWrite() {
        return new String[] { id, amount, currency, userId, targetAccountNumber };
    }

    public PaymentCsvRecord update(PaymentCsvRecord paymentRecord) {
        amount = paymentRecord.amount;
        currency = paymentRecord.currency;
        userId = paymentRecord.userId;
        targetAccountNumber = paymentRecord.targetAccountNumber;

        return this;
    }

    public static PaymentCsvRecord ofCsvRecord(CSVRecord paymentRecord) {
        var paymentCsvRecord = new PaymentCsvRecord();

        paymentCsvRecord.setId(paymentRecord.get("id"));
        paymentCsvRecord.setAmount(paymentRecord.get("amount"));
        paymentCsvRecord.setCurrency(paymentRecord.get("currency"));
        paymentCsvRecord.setUserId(paymentRecord.get("userId"));
        paymentCsvRecord.setTargetAccountNumber(paymentRecord.get("targetAccountNumber"));

        return paymentCsvRecord;
    }

    public UUID getIdAsUUID() {
        return fromString(id);
    }
}
