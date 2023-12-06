package dhsa.project.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class FhirWrapper {

    private final FhirContext ctx;

    private static FhirWrapper instance;

    private static final String baseUrl = "http://localhost:8080/fhir";

    private FhirWrapper() {
        ctx = FhirContext.forR4();
    }

    public static FhirContext getContext() {
        if (instance == null) {
            instance = new FhirWrapper();
        }
        return instance.ctx;
    }

    public static IGenericClient getClient() {
        getContext().getRestfulClientFactory().setSocketTimeout(60 * 1000);
        return getContext().newRestfulGenericClient(baseUrl);
    }
}
