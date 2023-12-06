package dhsa.project.dataset;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import dhsa.project.service.FhirWrapper;

public class DatasetLoader {

    private final ResourceLoader[] loaders;

    public DatasetLoader() {
        this(1);
    }

    public DatasetLoader(int numParts) {
        IGenericClient fhirClient = FhirWrapper.getClient();
        this.loaders = new ResourceLoader[] {
                //new PatientsLoader(),
                //new EncountersLoader(),
                //new ConditionsLoader(),
                //new ObservationsLoader(),
                //new AllergiesLoader(),
                //new CarePlansLoader(),
                //new ImmunizationsLoader(),
                //new MedicationsLoader(),
                new MedicationRequestsLoader(),
                //new ProceduresLoader()
        };

        for (ResourceLoader loader : loaders) {
            if (!(loader instanceof PatientsLoader || loader instanceof EncountersLoader))
                loader.set(fhirClient, numParts, 100_000);
            else
                loader.set(fhirClient, numParts);
        }
    }

    public void load() {
        for (ResourceLoader loader : loaders)
            loader.load();
    }
}
