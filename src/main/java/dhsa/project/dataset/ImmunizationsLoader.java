package dhsa.project.dataset;

import ca.uhn.fhir.util.BundleBuilder;
import dhsa.project.service.FhirWrapper;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

public class ImmunizationsLoader implements Loader {

    private final Iterable<CSVRecord> records;

    public ImmunizationsLoader() {
        records = Helper.parse("immunizations");
        if (records == null) {
            Helper.logSevere("Failed to load immunizations");
        }
    }

    @Override
    @SneakyThrows
    public void load() {
        int count = 0;
        List<Immunization> buffer = new ArrayList<>();

        for (CSVRecord rec : records) {
            Reference pat = Helper.resolveUID(Patient.class, rec.get("PATIENT"));
            Reference enc = Helper.resolveUID(Encounter.class, rec.get("ENCOUNTER"));

            Immunization imm = new Immunization();

            imm.setRecorded(Helper.parseDate(rec.get("DATE")));
            imm.setPatient(pat);
            imm.setEncounter(enc);
            imm.setVaccineCode(new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem("http://hl7.org/fhir/sid/cvx")
                    .setCode(rec.get("CODE"))
                    .setDisplay(rec.get("DESCRIPTION"))
                )
            );

            count++;
            buffer.add(imm);

            if (count % 100 == 0) {
                BundleBuilder bb = new BundleBuilder(FhirWrapper.getContext());
                buffer.forEach(bb::addTransactionCreateEntry);
                FhirWrapper.getClient().transaction().withBundle(bb.getBundle()).execute();

                if (count % 1000 == 0)
                    Helper.logInfo("Loaded %d immunizations".formatted(buffer.size()));

                buffer.clear();
            }
        }

        Helper.logInfo("Loaded ALL immunizations");
    }
}
