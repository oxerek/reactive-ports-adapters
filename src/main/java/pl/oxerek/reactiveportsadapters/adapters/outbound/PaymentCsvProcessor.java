package pl.oxerek.reactiveportsadapters.adapters.outbound;

import static io.vavr.control.Try.of;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.notExists;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.apache.commons.csv.CSVFormat.DEFAULT;
import static org.springframework.util.CollectionUtils.isEmpty;

import io.vavr.control.Try;
import io.vavr.control.Try.WithResources1;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import pl.oxerek.reactiveportsadapters.adapters.outbound.model.PaymentCsvRecord;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PaymentCsvProcessor {

    WithResources1<CSVPrinter> csvWriter;
    WithResources1<CSVPrinter> csvAppender;
    WithResources1<CSVParser> csvReader;

    public PaymentCsvProcessor(String csvFileName) {
        var csvPath = get(csvFileName);

        if (notExists(csvPath)) {
            of(() -> createFile(csvPath));
        }

        this.csvWriter = Try.withResources(() -> new CSVPrinter(newBufferedWriter(csvPath, CREATE, TRUNCATE_EXISTING), DEFAULT
              .withHeader("id", "amount", "currency", "userId", "targetAccountNumber")));

        this.csvAppender = Try.withResources(() -> new CSVPrinter(newBufferedWriter(csvPath, APPEND), DEFAULT
              .withFirstRecordAsHeader()));

        this.csvReader = Try.withResources(() -> new CSVParser(newBufferedReader(csvPath), DEFAULT
              .withFirstRecordAsHeader()));
    }

    protected PaymentCsvRecord writeOrAppendRecord(PaymentCsvRecord paymentCsvRecord) {
        if (recordExists()) {
            return csvAppender.of(csvPrinter -> printRecord(csvPrinter, paymentCsvRecord)).get();
        }
        return csvWriter.of(csvPrinter -> printRecord(csvPrinter, paymentCsvRecord)).get();
    }

    protected PaymentCsvRecord replaceRecord(PaymentCsvRecord paymentCsvRecord) {
        var records = findRecords(Set.of()).stream()
              .map(payment -> payment.getId().equals(paymentCsvRecord.getId()) ? payment.update(paymentCsvRecord) : payment)
              .collect(Collectors.toList());

        csvWriter.of(csvPrinter -> printRecords(csvPrinter, records)).get();

        return records.stream().anyMatch(paymentCsvRecord::equals) ? paymentCsvRecord : null;
    }

    protected PaymentCsvRecord removeRecord(String id) {
        var records = findRecords(Set.of());

        var toRemove = records.stream()
              .filter(paymentCsvRecord -> paymentCsvRecord.getId().equals(id))
              .findFirst();

        toRemove.ifPresent(records::remove);

        csvWriter.of(csvPrinter -> printRecords(csvPrinter, records)).get();

        return toRemove.orElse(null);
    }

    private List<PaymentCsvRecord> printRecords(CSVPrinter csvPrinter, List<PaymentCsvRecord> records) throws IOException {
        for (PaymentCsvRecord paymentCsvRecord : records) {
            csvPrinter.printRecord(paymentCsvRecord.toWrite());
        }
        return records;
    }

    protected PaymentCsvRecord printRecord(CSVPrinter csvPrinter, PaymentCsvRecord paymentCsvRecord) throws IOException {
        paymentCsvRecord.setId(UUID.randomUUID().toString());
        csvPrinter.printRecord(paymentCsvRecord.toWrite());
        return paymentCsvRecord;
    }

    protected boolean recordExists() {
        return csvReader.of(reader -> reader.iterator().hasNext()).get();
    }

    protected Optional<PaymentCsvRecord> findRecord(String id) {
        return csvReader.of(records -> records.getRecords().stream()
              .filter(paymentRecord -> id.equals(paymentRecord.get("id")))
              .findFirst()
              .map(PaymentCsvRecord::ofCsvRecord)).get();
    }

    protected List<PaymentCsvRecord> findRecords(Set<String> ids) {
        return csvReader.of(records -> records.getRecords().stream()
              .filter(paymentRecord -> isEmpty(ids) || ids.stream().anyMatch(id -> id.equals(paymentRecord.get("id"))))
              .map(PaymentCsvRecord::ofCsvRecord)
              .collect(Collectors.toList())).get();
    }
}
