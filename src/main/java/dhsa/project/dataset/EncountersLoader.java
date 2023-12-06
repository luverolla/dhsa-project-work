package dhsa.project.dataset;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

public class EncountersLoader extends ResourceLoader {

        public EncountersLoader() {
                super("encounters");
        }

        @Override
        protected Resource createResource(CSVRecord rec) {

            Encounter enc = new Encounter();

            if (hasProp(rec, "ID"))
                enc.addIdentifier()
                    .setType(new CodeableConcept()
                        .addCoding(new Coding()
                            .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                            .setCode("ANON")
                            .setDisplay("Anonymous ID")
                        )
                    )
                    .setSystem("urn:ietf:rfc:3986")
                    .setValue(rec.get("ID"));

            if (hasProp(rec, "DATE")) {
                try {
                    enc.getPeriod().setStart(df.parse(rec.get("DATE")));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (hasProp(rec, "PATIENT")) {
                try {
                    enc.setSubject(getPatientRef(rec.get("PATIENT")));
                } catch (Exception e) {
                    return null;
                }
            }

            if (hasProp(rec, "CODE")) {
                enc.addType().addCoding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("CODE"))
                    .setDisplay(rec.get("DESCRIPTION"));
            }

            if (hasProp(rec, "REASONCODE")) {
                enc.addReasonCode().addCoding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("REASONCODE"))
                    .setDisplay(rec.get("REASONDESCRIPTION"));
            }

            return enc;
        }
}
