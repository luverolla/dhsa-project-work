package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class PatientsLoader extends ResourceLoader {

        public PatientsLoader() {
            super("patients");
        }

        @Override
        protected void loadResource(List<CSVRecord> records) {

        }
}
