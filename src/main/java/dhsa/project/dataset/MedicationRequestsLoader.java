package dhsa.project.dataset;

import ca.uhn.fhir.util.BundleBuilder;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

public class MedicationRequestsLoader extends ResourceLoader {

    protected MedicationRequestsLoader() {
        super("medications");
    }

    @Override
    protected Resource createResource(CSVRecord rec) {
        if (!hasProp(rec, "CODE")) {
            return null;
        }

        try {
            MedicationStatement mst = new MedicationStatement();

            mst.setSubject(getPatientRef(rec.get("PATIENT")));
            mst.setContext(getEncounterRef(rec.get("ENCOUNTER")));
            mst.setMedication(new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("CODE"))
                    .setDisplay(rec.get("DESCRIPTION"))
                )
            );

            if (hasProp(rec, "START")) {
                if (hasProp(rec, "STOP")) {
                    try {
                        mst.getEffectivePeriod().setStart(df.parse(rec.get("START")));
                        mst.getEffectivePeriod().setEnd(df.parse(rec.get("STOP")));
                        mst.setStatus(MedicationStatement.MedicationStatementStatus.COMPLETED);
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    try {
                        mst.getEffectivePeriod().setStart(df.parse(rec.get("START")));
                        mst.setStatus(MedicationStatement.MedicationStatementStatus.ACTIVE);
                    } catch (Exception e) {
                        return null;
                    }
                }
            }

            return mst;

        } catch (Exception e) {
            return null;
        }
    }
}
