package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

public class CarePlansLoader extends ResourceLoader {

        public CarePlansLoader() {
                super("careplans");
        }

        @Override
        protected Resource createResource(CSVRecord rec) {
            CarePlan cp = new CarePlan();

            Period period = new Period();
            if (hasProp(rec, "START")) {
                try {
                    period.setStart(df.parse(rec.get("START")));
                } catch (Exception e) {
                    return null;
                }
            }
            if (hasProp(rec, "STOP")) {
                try {
                    period.setEnd(df.parse(rec.get("STOP")));
                } catch (Exception e) {
                    return null;
                }
            }
            cp.setPeriod(period);

            if (hasProp(rec, "PATIENT")) {
                try {
                    cp.setSubject(getPatientRef(rec.get("PATIENT")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(rec, "ENCOUNTER")) {
                try {
                    cp.setEncounter(getEncounterRef(rec.get("ENCOUNTER")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(rec, "CODE")) {
                cp.addCategory(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://snomed.info/sct")
                        .setCode(rec.get("CODE"))
                        .setDisplay(rec.get("DESCRIPTION"))
                    )
                );
            }

            if (hasProp(rec, "REASONCODE")) {
                cp.addActivity()
                    .getDetail()
                    .addReasonCode(new CodeableConcept()
                        .addCoding(new Coding()
                            .setSystem("http://snomed.info/sct")
                            .setCode(rec.get("REASONCODE"))
                            .setDisplay(rec.get("REASONDESCRIPTION"))
                        )
                    );
            }

            if (hasProp(rec, "ID")) {
                cp.addIdentifier()
                    .setType(new CodeableConcept()
                        .addCoding(new Coding()
                            .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                            .setCode("ANON")
                            .setDisplay("Anonymous ID")
                        )
                    )
                    .setValue(rec.get("ID"))
                    .setSystem("urn:ietf:rfc:3986");
            }

            return cp;
        }
}
