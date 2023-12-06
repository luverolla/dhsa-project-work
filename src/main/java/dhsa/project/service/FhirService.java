package dhsa.project.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

@Service
public class FhirService {

    private final IGenericClient client = FhirWrapper.getClient();

    public Patient getPatientById(String id) {
        Bundle results = client.search()
            .forResource(Patient.class)
            .and(Patient.IDENTIFIER.exactly().systemAndIdentifier("http://hl7.org/fhir/sid/us-ssn", id))
            .returnBundle(Bundle.class)
            .execute();

        return (Patient) results.getEntry().get(0).getResource();
    }
}
