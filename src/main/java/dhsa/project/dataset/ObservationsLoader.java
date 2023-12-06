package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

public class ObservationsLoader extends ResourceLoader {

        public ObservationsLoader() {
                super("observations");
        }

        @Override
        protected Resource createResource(CSVRecord record) {
            Observation obs = new Observation();

            if (hasProp(record, "DATE")) {
                try {
                    obs.getEffectiveDateTimeType().setValueAsString(record.get("DATE"));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(record, "PATIENT")) {
                try {
                    obs.setSubject(getPatientRef(record.get("PATIENT")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(record, "ENCOUNTER")) {
                try {
                    obs.setEncounter(getEncounterRef(record.get("ENCOUNTER")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(record, "CODE")) {
                obs.setCode(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://loinc.org")
                        .setCode(record.get("CODE"))
                        .setDisplay(record.get("DESCRIPTION"))
                    )
                );
            }

            if (hasProp(record, "VALUE")) {
                if (!hasProp(record, "UNITS"))
                    return null;

                String unitStr = record.get("UNITS");
                if (unitStr.equals("{nominal}")) {
                    obs.setValue(new StringType(record.get("VALUE")));
                }
                else if (unitStr.equals("{count}")) {
                    try {
                        float value = Float.parseFloat(record.get("VALUE"));
                        obs.setValue(new IntegerType((int)value));
                    } catch (Exception e) {
                        return null;
                    }
                }
                else {
                    try {
                        Quantity q = new Quantity();
                        q.setValue(Double.parseDouble(record.get("VALUE")));
                        q.setUnit(unitStr);
                        obs.setValue(q);
                    } catch (Exception e) {
                        return null;
                    }
                }
            }

            return obs;

        }
}
