package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class ConditionsLoader extends ResourceLoader {

        public ConditionsLoader() {
            super("conditions");
        }

        @Override
        protected void loadResource(List<CSVRecord> records) {

        }
}
