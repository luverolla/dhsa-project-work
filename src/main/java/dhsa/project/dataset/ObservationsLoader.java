package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class ObservationsLoader extends ResourceLoader {

        public ObservationsLoader() {
            super("observations");
        }

        @Override
        protected void loadResource(List<CSVRecord> records) {

        }
}
