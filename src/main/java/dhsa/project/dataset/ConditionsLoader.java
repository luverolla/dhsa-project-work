package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

public class ConditionsLoader extends ResourceLoader {

        public ConditionsLoader() {
                super("conditions");
        }

        @Override
        protected Resource createResource(CSVRecord rec) {
            Condition cond = new Condition();

            if (hasProp(rec, "START")) {
                try {
                    cond.setOnset(DateTimeType.parseV3(rec.get("START")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(rec, "STOP")) {
                try {
                    cond.setAbatement(DateTimeType.parseV3(rec.get("STOP")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(rec, "PATIENT")) {
                try {
                    cond.setSubject(getPatientRef(rec.get("PATIENT")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(rec, "ENCOUNTER")) {
                try {
                    cond.setEncounter(getEncounterRef(rec.get("ENCOUNTER")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(rec, "CODE")) {
                cond.setCode(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://snomed.info/sct")
                        .setCode(rec.get("CODE"))
                        .setDisplay(rec.get("DESCRIPTION"))
                    )
                );
            }

            return cond;
        }
}
