package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class MedicationsLoader extends ResourceLoader {

        public MedicationsLoader() {
            super("medications");
        }

        @Override
        protected void loadResource(List<CSVRecord> records) {

        }
}
