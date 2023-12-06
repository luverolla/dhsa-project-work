package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

public class MedicationsLoader extends ResourceLoader {

    private List<String> medCodes;

    public MedicationsLoader() {
        super("medications");
        medCodes = new ArrayList<>();
    }

    @Override
    protected Resource createResource(CSVRecord rec) {
        if (!hasProp(rec, "CODE")) {
            return null;
        }

        if (medCodes.contains(rec.get("CODE"))) {
            return null;
        }

        Medication med = new Medication();

        med.setCode(new CodeableConcept()
            .addCoding(new Coding()
                .setSystem("http://snomed.info/sct")
                .setCode(rec.get("CODE"))
                .setDisplay(rec.get("DESCRIPTION"))
            )
        );

        medCodes.add(rec.get("CODE"));

        return med;
    }
}
