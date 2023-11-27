package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class EncountersLoader extends ResourceLoader {

        public EncountersLoader() {
            super("encounters");
        }

        @Override
        protected void loadResource(List<CSVRecord> records) {

        }
}
