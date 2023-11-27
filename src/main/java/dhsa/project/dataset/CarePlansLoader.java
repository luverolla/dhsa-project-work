package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class CarePlansLoader extends ResourceLoader {

        public CarePlansLoader() {
            super("careplans");
        }

        @Override
        protected void loadResource(List<CSVRecord> records) {

        }
}
