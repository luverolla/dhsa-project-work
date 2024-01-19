package dhsa.project.loader;

import ca.uhn.fhir.util.BundleBuilder;
import dhsa.project.fhir.FhirWrapper;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;

import java.util.ArrayList;
import java.util.List;

public class PatientsLoader extends BaseLoader {

    PatientsLoader(DatasetService datasetService) {
        super(datasetService, "patients");
    }

    @Override
    @SneakyThrows
    public void load() {
        int count = 0;
        List<Patient> buffer = new ArrayList<>();

        for (CSVRecord rec : records) {
            Patient patient = new Patient();
            patient.setId(rec.get("Id"));

            patient.addExtension()
                .setUrl("http://hl7.org/fhir/StructureDefinition/patient-birthPlace")
                .setValue(new Address().setText(rec.get("BIRTHPLACE")));

            if (datasetService.hasProp(rec, "MAIDEN"))
                patient.addExtension()
                    .setUrl("http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName")
                    .setValue(new StringType(rec.get("MAIDEN")));

            patient.addAddress()
                .addLine(rec.get("ADDRESS"))
                .setCity(rec.get("CITY"))
                .setState(rec.get("STATE"))
                .setPostalCode(rec.get("ZIP"))
                .setDistrict(rec.get("COUNTY"));

            patient.addExtension()
                .setUrl("http://hl7.org/fhir/StructureDefinition/geolocation")
                .setValue(new Address()
                    .addExtension()
                    .setUrl("latitude")
                    .setValue(new DecimalType(rec.get("LAT")))
                    .addExtension()
                    .setUrl("longitude")
                    .setValue(new DecimalType(rec.get("LON")))
                );

            HumanName name = new HumanName();
            name.addGiven(rec.get("FIRST"));
            name.setFamily(rec.get("LAST"));
            if (datasetService.hasProp(rec, "PREFIX"))
                name.addPrefix(rec.get("PREFIX"));
            if (datasetService.hasProp(rec, "SUFFIX"))
                name.addSuffix(rec.get("SUFFIX"));
            patient.addName(name);

            patient.setBirthDate(datasetService.parseDate(rec.get("BIRTHDATE")));
            if (datasetService.hasProp(rec, "DEATHDATE"))
                patient.setDeceased(new DateTimeType(datasetService.parseDate(rec.get("DEATHDATE"))));
            else
                patient.setDeceased(new BooleanType(false));

            patient.addIdentifier()
                .setType(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                        .setCode("ANON")
                        .setDisplay("Anonymous ID")
                    )
                )
                .setSystem("urn:ietf:rfc:3986")
                .setValue(rec.get("Id"));

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

            patient.setGender(
                rec.get("GENDER").equals("M") ? AdministrativeGender.MALE : AdministrativeGender.FEMALE
            );

            if (datasetService.hasProp(rec, "MARITAL"))
                patient.setMaritalStatus(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus")
                        .setCode(rec.get("MARITAL"))
                    )
                );

            count++;
            buffer.add(patient);
            if (count % 100 == 0 || count == records.size()) {
                BundleBuilder bb = new BundleBuilder(FhirWrapper.getContext());
                buffer.forEach(bb::addTransactionUpdateEntry);
                FhirWrapper.getClient().transaction().withBundle(bb.getBundle()).execute();

                if (count % 1000 == 0)
                    datasetService.logInfo("Loaded %d patients", count);

                buffer.clear();
            }
        }

        datasetService.logInfo("Loaded ALL patients");
    }
}
