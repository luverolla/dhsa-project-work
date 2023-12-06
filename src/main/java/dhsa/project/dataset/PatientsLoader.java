package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;

import java.text.ParseException;

public class PatientsLoader extends ResourceLoader {
    public PatientsLoader() {
        super("patients");
    }

    @Override
    protected Resource createResource(CSVRecord rec) {
        Patient patient = new Patient();

        patient.addExtension()
            .setUrl("http://hl7.org/fhir/StructureDefinition/patient-birthPlace")
            .setValue(new Address().setText(rec.get("BIRTHPLACE")));

        patient.addAddress().setText(rec.get("ADDRESS"));

        HumanName name = new HumanName();
        if (hasProp(rec, "PREFIX"))
            name.addPrefix(rec.get("PREFIX"));
        if (hasProp(rec, "FIRST"))
            name.addGiven(rec.get("FIRST"));
        if (hasProp(rec, "LAST"))
            name.setFamily(rec.get("LAST"));
        if (hasProp(rec, "SUFFIX"))
            name.addSuffix(rec.get("SUFFIX"));
        patient.addName(name);

        if (hasProp(rec, "BIRTHDATE"))
            try {
                patient.setBirthDate(df.parse(rec.get("BIRTHDATE")));
            } catch (ParseException e) {
                return null;
            }

        if (hasProp(rec, "DEATHDATE")) {
            try {
                patient.setDeceased(new DateTimeType(df.parse(rec.get("DEATHDATE"))));
            } catch (ParseException e) {
                return null;
            }
        }
        else {
            patient.setDeceased(new BooleanType(false));
        }

        if (hasProp(rec, "ID"))
            patient.addIdentifier()
                .setType(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                        .setCode("ANON")
                        .setDisplay("Anonymous ID")
                    )
                )
                .setSystem("urn:ietf:rfc:3986")
                .setValue(rec.get("ID"));

        if (hasProp(rec, "SSN")) {
            patient.addIdentifier()
                .setType(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                        .setCode("SS")
                        .setDisplay("Social Security Number")
                    )
                )
                .setSystem("http://hl7.org/fhir/sid/us-ssn")
                .setValue(rec.get("SSN"));
        }

        if (hasProp(rec, "PASSPORT")) {
            patient.addIdentifier()
                .setType(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                        .setCode("PPN")
                        .setDisplay("Passport Number")
                    )
                )
                .setSystem("http://standardhealthrecord.org/fhir/StructureDefinition/passportNumber")
                .setValue(rec.get("PASSPORT"));
        }

        if (hasProp(rec, "DRIVERS")) {
            patient.addIdentifier()
                .setType(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                        .setCode("DL")
                        .setDisplay("Driver's License Number")
                    )
                )
                .setSystem("urn:oid:2.16.840.1.113883.4.3.25")
                .setValue(rec.get("DRIVERS"));
        }

        patient.setGender(
            rec.get("GENDER").equals("M") ? AdministrativeGender.MALE : AdministrativeGender.FEMALE
        );

        patient.setMaritalStatus(new CodeableConcept()
            .addCoding(new Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus")
                .setCode(rec.get("MARITAL"))
            )
        );

        return patient;
    }
}
