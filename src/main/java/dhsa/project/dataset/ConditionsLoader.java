package dhsa.project.dataset;

import ca.uhn.fhir.util.BundleBuilder;
import dhsa.project.service.FhirWrapper;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

public class ConditionsLoader implements Loader {

    private final Iterable<CSVRecord> records;

    public ConditionsLoader() {
        records = Helper.parse("conditions");
        if (records == null) {
            Helper.logSevere("Failed to load conditions");
        }
    }

    @Override
    public void load() {
        int count = 0;
        List<Condition> buffer = new ArrayList<>();

        for (CSVRecord rec : records) {
            Reference pat = Helper.resolveUID(Patient.class, rec.get("PATIENT"));
            Reference enc = Helper.resolveUID(Encounter.class, rec.get("ENCOUNTER"));

            Condition cond = new Condition();

            cond.setOnset(DateTimeType.parseV3(rec.get("START")));

            if (Helper.hasProp(rec, "STOP"))
                cond.setAbatement(DateTimeType.parseV3(rec.get("STOP")));

            cond.setSubject(pat);
            cond.setEncounter(enc);

            cond.setCode(new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("CODE"))
                    .setDisplay(rec.get("DESCRIPTION"))
                )
            );

            count++;
            buffer.add(cond);

            if (count % 100 == 0) {
                BundleBuilder bb = new BundleBuilder(FhirWrapper.getContext());
                buffer.forEach(bb::addTransactionCreateEntry);
                FhirWrapper.getClient().transaction().withBundle(bb.getBundle()).execute();

                if (count % 1000 == 0)
                    Helper.logInfo("Loaded %d conditions".formatted(buffer.size()));

                buffer.clear();
            }
        }

        Helper.logInfo("Loaded ALL conditions");
    }
}
