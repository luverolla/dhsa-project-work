package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class AllergiesLoader extends ResourceLoader {

    public AllergiesLoader() {
        super("allergies");
    }

    @Override
    protected void loadResource(List<CSVRecord> records) {

    }
}
