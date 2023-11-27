package dhsa.project.dataset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;

public abstract class ResourceLoader {
    protected int numParts;
    protected String subject;

    protected ResourceLoader(String subject) {
        this.subject = subject;
        this.numParts = 12;
    }

    protected abstract void loadResource(List<CSVRecord> records);

    public void set(int numParts) {
        this.numParts = numParts;
    }

    public void load() {
        loadResource(parse());
    }

    private List<CSVRecord> parse() {
        List<CSVRecord> result = new ArrayList<>();

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        for (int i = 1; i <= numParts; i++) {
            try {
                String path = "dataset/output_" + i + "/csv/" + subject + ".csv";
                File part = new ClassPathResource(path).getFile();

                Reader r = new FileReader(part);
                Iterable<CSVRecord> records = csvFormat.parse(r);

                for (CSVRecord rec : records)
                    result.add(rec);
            }
            catch(IOException e) {
                Logger.getGlobal().severe(e.getMessage());
            }
        }

        return result;
    }
}
