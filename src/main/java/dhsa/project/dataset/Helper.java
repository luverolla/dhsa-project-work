package dhsa.project.dataset;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import dhsa.project.service.FhirWrapper;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

import java.io.FileReader;
import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.logging.Logger;

class Helper {

    final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    final static Logger lg = Logger.getLogger(Helper.class.getName());
    final static IGenericClient client = FhirWrapper.getClient();
    final static String basePath = "src/main/resources/coherent-11-07-2022";
    final static String csvPath = basePath + "/csv";

    static void logInfo(String fmt, Object... args) {
        lg.info(String.format(fmt, args));
    }

    static void logWarning(String fmt, Object... args) {
        lg.warning(String.format(fmt, args));
    }

    static void logSevere(String fmt, Object... args) {
        lg.severe(String.format(fmt, args));
    }

    static boolean hasProp(CSVRecord record, String prop) {
        return (
            record.isMapped(prop) &&
                record.get(prop) != null &&
                !record.get(prop).isEmpty() &&
                !record.get(prop).equals("false")
        );
    }

    static Iterable<CSVRecord> parse(String subject) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        try {
            String filePath = csvPath + "/" + subject + ".csv";
            return csvFormat.parse(new FileReader(filePath));
        }
        catch(IOException e) {
            lg.severe(e.getMessage());
            return null;
        }
    }

    static Date parseDate(String date) throws ParseException {
       return df.parse(date);
    }

    static Reference resolveUID(Class<? extends Resource> type, String uid) {
        try {
            Resource res = client.read()
                .resource(type)
                .withId(uid)
                .execute();

            return new Reference(res.getId());
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
}
