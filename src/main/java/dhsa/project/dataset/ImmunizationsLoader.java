package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class ImmunizationsLoader extends ResourceLoader {

        public ImmunizationsLoader() {
            super("immunizations");
        }

        @Override
        protected void loadResource(List<CSVRecord> records) {

        }
}
