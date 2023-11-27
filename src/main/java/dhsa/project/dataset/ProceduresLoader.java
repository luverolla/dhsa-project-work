package dhsa.project.dataset;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class ProceduresLoader extends ResourceLoader {

        public ProceduresLoader() {
            super("procedures");
        }

        @Override
        protected void loadResource(List<CSVRecord> records) {

        }
}
