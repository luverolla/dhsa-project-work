package dhsa.project.dataset;

import ca.uhn.fhir.util.BundleBuilder;
import dhsa.project.service.FhirWrapper;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

public class CarePlansLoader implements Loader {

    private final Iterable<CSVRecord> records;

    public CarePlansLoader() {
        records = Helper.parse("careplans");
        if (records == null) {
            Helper.logSevere("Failed to load careplans");
        }
    }

    @Override
    @SneakyThrows
    public void load() {
        int count = 0;
        List<CarePlan> buffer = new ArrayList<>();

        for (CSVRecord rec : records) {
            Reference pat = Helper.resolveUID(Patient.class, rec.get("PATIENT"));
            Reference enc = Helper.resolveUID(Encounter.class, rec.get("ENCOUNTER"));

            if (pat == null || enc == null)
                continue;

            CarePlan cp = new CarePlan();
            cp.setId(rec.get("Id"));

            Period period = new Period();
            period.setStart(Helper.parseDate(rec.get("START")));
            if (Helper.hasProp(rec, "STOP"))
                period.setEnd(Helper.parseDate(rec.get("STOP")));
            cp.setPeriod(period);

            cp.setSubject(pat);
            cp.setEncounter(enc);

            cp.addCategory(new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("CODE"))
                    .setDisplay(rec.get("DESCRIPTION"))
                )
            );

            cp.addActivity()
                .getDetail()
                .addReasonCode(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://snomed.info/sct")
                        .setCode(rec.get("REASONCODE"))
                        .setDisplay(rec.get("REASONDESCRIPTION"))
                    )
                );

            cp.addIdentifier()
                .setType(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                        .setCode("ANON")
                        .setDisplay("Anonymous ID")
                    )
                )
                .setValue(rec.get("Id"))
                .setSystem("urn:ietf:rfc:3986");

            count++;
            buffer.add(cp);

            if (count % 100 == 0) {
                BundleBuilder bb = new BundleBuilder(FhirWrapper.getContext());
                buffer.forEach(bb::addTransactionUpdateEntry);
                FhirWrapper.getClient().transaction().withBundle(bb.getBundle()).execute();

                if (count % 1000 == 0)
                    Helper.logInfo("Loaded %d careplans", count);

                buffer.clear();
            }
        }

        Helper.logInfo("Loaded ALL careplans");
    }
}
