package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public abstract class BaseLoader implements Loader {

    protected final String subject;
    protected final DatasetService datasetService;
    protected List<CSVRecord> records;

    protected BaseLoader(DatasetService datasetService, String subject) {
        this.subject = subject;
        this.datasetService = datasetService;
        this.records = datasetService.parse(subject);
        if (records == null)
            throw new RuntimeException("Failed to load " + subject);
    }

    @Override
    public abstract void load();
}
