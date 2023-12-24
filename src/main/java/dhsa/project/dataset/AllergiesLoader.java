package dhsa.project.dataset;

import ca.uhn.fhir.util.BundleBuilder;
import dhsa.project.service.FhirWrapper;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

public class AllergiesLoader implements Loader {

    private final Iterable<CSVRecord> records;

    public AllergiesLoader() {
        records = Helper.parse("allergies");
        if (records == null) {
            Helper.logSevere("Failed to load allergies");
        }
    }

    @Override
    @SneakyThrows
    public void load() {
        int count = 0;
        List<AllergyIntolerance> buffer = new ArrayList<>();

        for (CSVRecord rec : records) {
            AllergyIntolerance alin = new AllergyIntolerance();
            alin.setOnset(DateTimeType.parseV3(rec.get("START")));

            if (Helper.hasProp(rec, "STOP"))
                alin.setLastOccurrence(Helper.parseDate(rec.get("STOP")));

            alin.setPatient(Helper.resolveUID(Patient.class, rec.get("PATIENT")));
            alin.setEncounter(Helper.resolveUID(Encounter.class, rec.get("ENCOUNTER")));
            alin.setCode(new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("CODE"))
                    .setDisplay(rec.get("DESCRIPTION"))
                )
            );

            count++;
            buffer.add(alin);

            if (count % 100 == 0) {
                BundleBuilder bb = new BundleBuilder(FhirWrapper.getContext());
                buffer.forEach(bb::addTransactionCreateEntry);
                FhirWrapper.getClient().transaction().withBundle(bb.getBundle()).execute();

                if (count % 1000 == 0)
                    Helper.logInfo("Loaded %d allergies", count);

                buffer.clear();
            }
        }

        Helper.logInfo("Loaded ALL allergies");
    }
}
