package dhsa.project.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FhirService {

    private final IGenericClient client = FhirWrapper.getClient();

    public List<Patient> listPatients(int page) {
        if (page < 1) {
            return List.of();
        }
        Bundle resultBundle = client.search().forResource(Patient.class)
            .count(100)
            .offset((page - 1) * 100)
            //.sort()
            //.descending(Patient.BIRTHDATE)
            .returnBundle(Bundle.class)
            .execute();

            return resultBundle.getEntry().stream()
                .map(Bundle.BundleEntryComponent::getResource)
                .map(Patient.class::cast)
                .toList();
    }

    public Patient getPatientById(String id) {
        Bundle results = client.search()
            .forResource(Patient.class)
            .and(Patient.IDENTIFIER.exactly().systemAndIdentifier("http://hl7.org/fhir/sid/us-ssn", id))
            .returnBundle(Bundle.class)
            .execute();

        return (Patient) results.getEntry().get(0).getResource();
    }
}
