package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Resource;

public class ImmunizationsLoader extends ResourceLoader {

        public ImmunizationsLoader() {
                super("immunizations");
        }

        @Override
        protected Resource createResource(CSVRecord rec) {
            Immunization imm = new Immunization();

            if (hasProp(rec, "DATE")) {
                try {
                    imm.setRecorded(df.parse(rec.get("DATE")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(rec, "PATIENT")) {
                try {
                    imm.setPatient(getPatientRef(rec.get("PATIENT")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(rec, "ENCOUNTER")) {
                try {
                    imm.setEncounter(getEncounterRef(rec.get("ENCOUNTER")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(rec, "CODE")) {
                imm.setVaccineCode(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://hl7.org/fhir/sid/cvx")
                        .setCode(rec.get("CODE"))
                        .setDisplay(rec.get("DESCRIPTION"))
                    )
                );
            }

            return imm;
        }
}
