package dhsa.project.dataset;

public class DatasetLoader {

    private final ResourceLoader[] loaders;

    public DatasetLoader() {
        this(12);
    }

    public DatasetLoader(int numParts) {
        this.loaders = new ResourceLoader[] {
                new AllergiesLoader(),
                new CarePlansLoader(),
                new ConditionsLoader(),
                new EncountersLoader(),
                new ImmunizationsLoader(),
                new MedicationsLoader(),
                new ObservationsLoader(),
                new PatientsLoader(),
                new ProceduresLoader()
        };

        for (ResourceLoader loader : loaders) {
            loader.set(numParts);
        }
    }

    public void load() {
        for (ResourceLoader loader : loaders)
            loader.load();
    }
}
