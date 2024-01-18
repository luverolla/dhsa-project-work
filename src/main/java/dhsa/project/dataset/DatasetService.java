package dhsa.project.dataset;

import dhsa.project.dicom.DicomService;
import lombok.Getter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class DatasetService {

    private final DateFormat onlyDateFmt = new SimpleDateFormat("yyyy-MM-dd");
    private final DateFormat datetimeFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private final Logger logger = Logger.getLogger("DatasetLoader");
    private final String basePath = "src/main/resources/coherent-11-07-2022";
    @Getter
    @Autowired
    private DicomService dicomService;

    public void logInfo(String fmt, Object... args) {
        logger.info(String.format(fmt, args));
    }

    public void logSevere(String fmt, Object... args) {
        logger.severe(String.format(fmt, args));
    }

    public boolean hasProp(CSVRecord record, String prop) {
        return (
            record.isMapped(prop) &&
                record.get(prop) != null &&
                !record.get(prop).isEmpty() &&
                !record.get(prop).equals("false")
        );
    }

    public List<CSVRecord> parse(String subject) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build();

        try {
            String filePath = basePath + "/csv/" + subject + ".csv";
            return csvFormat.parse(new FileReader(filePath)).getRecords();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return null;
        }
    }

    public Date parseDate(String date) throws ParseException {
        return onlyDateFmt.parse(date);
    }

    public Date parseDatetime(String datetime) throws ParseException {
        return datetimeFmt.parse(datetime);
    }

    public void loadDataset() throws IOException {
        Path path = Paths.get("ds_loaded_ok");
        //if (!Files.exists(path)) {
        new DatasetLoader(this).load();
        //Files.createFile(path);
        //}
    }
}
