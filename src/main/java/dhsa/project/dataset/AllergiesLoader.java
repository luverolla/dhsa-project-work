package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

public class AllergiesLoader extends ResourceLoader {

    public AllergiesLoader() {
        super("allergies");
    }

    @Override
    protected Resource createResource(CSVRecord rec) {
        AllergyIntolerance alin = new AllergyIntolerance();

        if (hasProp(rec, "START")) {
            try {
                alin.setOnset(DateTimeType.parseV3(rec.get("START")));
            } catch (Exception e) {
                return null;
            }
        }

        if (hasProp(rec, "STOP")) {
            try {
                alin.setLastOccurrence(df.parse(rec.get("STOP")));
            } catch (Exception e) {
                return null;
            }
        }

        if (hasProp(rec, "PATIENT")) {
            try {
                alin.setPatient(getPatientRef(rec.get("PATIENT")));
            } catch (Exception e) {
                return null;
            }
        }

        if (hasProp(rec, "ENCOUNTER")) {
            try {
                alin.setEncounter(getEncounterRef(rec.get("ENCOUNTER")));
            }
            catch (Exception e) {
                return null;
            }
        }

        if (hasProp(rec, "CODE")) {
            alin.setCode(new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("CODE"))
                    .setDisplay(rec.get("DESCRIPTION"))
                )
            );
        }

        return alin;
    }
}
