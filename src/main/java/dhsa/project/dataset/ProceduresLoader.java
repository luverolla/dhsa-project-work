package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Resource;

public class ProceduresLoader extends ResourceLoader {

        public ProceduresLoader() {
                super("procedures");
        }

        @Override
        protected Resource createResource(CSVRecord record) {
            Procedure proc = new Procedure();

            if (hasProp(record, "DATE")) {
                try {
                    proc.getPerformedDateTimeType().setValueAsString(record.get("DATE"));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(record, "PATIENT")) {
                try {
                    proc.setSubject(getPatientRef(record.get("PATIENT")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(record, "ENCOUNTER")) {
                try {
                    proc.setEncounter(getEncounterRef(record.get("ENCOUNTER")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(record, "CODE")) {
                proc.setCode(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://snomed.info/sct")
                        .setCode(record.get("CODE"))
                        .setDisplay(record.get("DESCRIPTION"))
                    )
                );
            }

            if (hasProp(record, "REASONCODE")) {
                proc.addReasonCode(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://snomed.info/sct")
                        .setCode(record.get("REASONCODE"))
                        .setDisplay(record.get("REASONDESCRIPTION"))
                    )
                );
            }

            return proc;
        }
}
