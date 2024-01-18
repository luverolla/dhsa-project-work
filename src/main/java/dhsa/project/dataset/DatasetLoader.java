package dhsa.project.dataset;

public class DatasetLoader implements Loader {
    private final Loader[] loaders;

    public DatasetLoader(DatasetService datasetService) {
        this.loaders = new Loader[]{
            new PatientsLoader(datasetService),
            new OrganizationsLoader(datasetService),
            new ProvidersLoader(datasetService),
            new PayersLoader(datasetService),
            new EncountersLoader(datasetService),
            new ConditionsLoader(datasetService),
            new ObservationsLoader(datasetService),
            new AllergiesLoader(datasetService),
            new CarePlansLoader(datasetService),
            new ImmunizationsLoader(datasetService),
            new MedicationRequestsLoader(datasetService),
            new DevicesLoader(datasetService),
            new ImagingStudiesLoader(datasetService),
            new ProceduresLoader(datasetService)
        };
    }

    public void load() {
        for (Loader loader : loaders)
            loader.load();
    }
}
