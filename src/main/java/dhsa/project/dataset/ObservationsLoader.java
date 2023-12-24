package dhsa.project.dataset;

import ca.uhn.fhir.util.BundleBuilder;
import dhsa.project.service.FhirWrapper;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

public class ObservationsLoader implements Loader {

    private final Iterable<CSVRecord> records;

    public ObservationsLoader() {
        records = Helper.parse("observations");
        if (records == null) {
            Helper.logSevere("Failed to load observations");
        }
    }

    @Override
    public void load() {
        int count = 0;
        List<Observation> buffer = new ArrayList<>();

        for (CSVRecord record : records) {

            if (count <= 100) {
                count++;
                continue;
            }

            Reference pat = Helper.resolveUID(Patient.class, record.get("PATIENT"));
            Reference enc = null;

            if (Helper.hasProp(record, "ENCOUNTER"))
                enc = Helper.resolveUID(Encounter.class, record.get("ENCOUNTER"));

            Observation obs = new Observation();
            obs.getEffectiveDateTimeType().setValueAsString(record.get("DATE"));
            obs.setSubject(pat);
            if (enc != null)
                obs.setEncounter(enc);

            obs.setCode(new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem("http://loinc.org")
                    .setCode(record.get("CODE"))
                    .setDisplay(record.get("DESCRIPTION"))
                )
            );

            String unitStr = record.get("UNITS");
            if (unitStr.equals("{nominal}")) {
                obs.setValue(new StringType(record.get("VALUE")));
            } else if (unitStr.equals("{count}")) {
                float value = Float.parseFloat(record.get("VALUE"));
                obs.setValue(new IntegerType((int) value));
            } else if (record.get("TYPE").equals("numeric")) {
                Quantity q = new Quantity();
                q.setValue(Double.parseDouble(record.get("VALUE")));
                q.setUnit(unitStr);
                obs.setValue(q);
            } else {
                obs.setValue(new StringType(record.get("VALUE")));
            }

            count++;
            buffer.add(obs);

            if (count % 100 == 0) {
                BundleBuilder bb = new BundleBuilder(FhirWrapper.getContext());
                buffer.forEach(bb::addTransactionCreateEntry);
                FhirWrapper.getClient().transaction().withBundle(bb.getBundle()).execute();

                if (count % 1000 == 0)
                    Helper.logInfo("Loaded %d observations", count);

                buffer.clear();
            }
        }

        Helper.logInfo("Loaded ALL observations");
    }
}
