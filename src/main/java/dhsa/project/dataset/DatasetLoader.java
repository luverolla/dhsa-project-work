package dhsa.project.dataset;

public class DatasetLoader implements Loader {
    private final Loader[] loaders;
    public DatasetLoader() {
        this.loaders = new Loader[] {
                new PatientsLoader(),
                new OrganizationsLoader(),
                new ProvidersLoader(),
                new PayersLoader(),
                new EncountersLoader(),
                new ConditionsLoader(),
                new ObservationsLoader(),
                new AllergiesLoader(),
                new CarePlansLoader(),
                new ImmunizationsLoader(),
                new MedicationStatementsLoader(),
                new DevicesLoader(),
                new ImagingStudiesLoader(),
                new DicomFilesLoader(),
                new ProceduresLoader()
        };
    }

    public void load() {
        for (Loader loader : loaders)
            loader.load();
    }
}
