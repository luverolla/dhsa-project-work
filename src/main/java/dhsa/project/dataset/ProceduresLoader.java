package dhsa.project.dataset;

import ca.uhn.fhir.util.BundleBuilder;
import dhsa.project.service.FhirWrapper;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

public class ProceduresLoader implements Loader {

    private final Iterable<CSVRecord> records;

    public ProceduresLoader() {
        records = Helper.parse("procedures");
        if (records == null) {
            Helper.logSevere("Failed to load procedures");
        }
    }

    @Override
    public void load() {
        int count = 0;
        List<Procedure> buffer = new ArrayList<>();

        for (CSVRecord record : records) {
            Reference pat = Helper.resolveUID(Patient.class, record.get("PATIENT"));
            Reference enc = Helper.resolveUID(Encounter.class, record.get("ENCOUNTER"));

            Procedure proc = new Procedure();
            proc.getPerformedDateTimeType().setValueAsString(record.get("DATE"));
            proc.setSubject(pat);
            proc.setEncounter(enc);

            proc.setCode(new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(record.get("CODE"))
                    .setDisplay(record.get("DESCRIPTION"))
                )
            );

            proc.addReasonCode(new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(record.get("REASONCODE"))
                    .setDisplay(record.get("REASONDESCRIPTION"))
                )
            );

            count++;
            buffer.add(proc);

            if (count % 100 == 0) {
                BundleBuilder bb = new BundleBuilder(FhirWrapper.getContext());
                buffer.forEach(bb::addTransactionCreateEntry);
                FhirWrapper.getClient().transaction().withBundle(bb.getBundle()).execute();

                if (count % 1000 == 0)
                    Helper.logInfo("Loaded %d procedures", count);

                buffer.clear();
            }
        }

        Helper.logInfo("Loaded ALL procedures");
    }
}
