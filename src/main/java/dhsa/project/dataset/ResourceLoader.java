package dhsa.project.dataset;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleBuilder;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.slf4j.Logger;

import org.hl7.fhir.r4.model.*;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class ResourceLoader {
    protected int numParts;
    protected String subject;

    protected IGenericClient client;

    protected int limit;

    protected final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    protected final Logger lg = org.slf4j.LoggerFactory.getLogger(getClass());

    protected Reference getPatientRef(String uid) throws IndexOutOfBoundsException {
        Bundle results = client.search()
            .forResource(Patient.class)
            .and(Patient.IDENTIFIER.exactly().systemAndValues("urn:ietf:rfc:3986", uid))
            .returnBundle(Bundle.class)
            .execute();

        Patient p = (Patient) results.getEntry().get(0).getResource();
        return new Reference(p.getId());
    }

    protected Reference getEncounterRef(String uid) throws IndexOutOfBoundsException {
        Bundle results = client.search()
            .forResource(Encounter.class)
            .and(Encounter.IDENTIFIER.exactly().systemAndValues("urn:ietf:rfc:3986", uid))
            .returnBundle(Bundle.class)
            .execute();

        Encounter e = (Encounter) results.getEntry().get(0).getResource();
        return new Reference(e.getId());
    }

    protected Encounter getEncounterRes(String uid) throws IndexOutOfBoundsException {
        Bundle results = client.search()
            .forResource(Encounter.class)
            .and(Encounter.IDENTIFIER.exactly().systemAndValues("urn:ietf:rfc:3986", uid))
            .returnBundle(Bundle.class)
            .execute();

        return (Encounter) results.getEntry().get(0).getResource();
    }

    protected Reference getMedicationRef(String code) throws IndexOutOfBoundsException {
        Bundle results = client.search()
            .forResource(Medication.class)
            .and(Medication.CODE.exactly().systemAndCode("http://snomed.info/sct", code))
            .returnBundle(Bundle.class)
            .execute();

        Medication m = (Medication) results.getEntry().get(0).getResource();
        return new Reference(m.getId());
    }

    protected ResourceLoader(String subject) {
        this.subject = subject;
        this.numParts = 1;
    }

    protected abstract Resource createResource(CSVRecord record);

    protected boolean hasProp(CSVRecord record, String prop) {
        return (
            record.get(prop) != null &&
            !record.get(prop).isEmpty() &&
            !record.get(prop).equals("false")
        );
    }

    public void set(IGenericClient client, int numParts, int limit) {
        this.client = client;
        this.numParts = numParts;
        this.limit = limit;
    }

    public void set(IGenericClient client, int numParts) {
        this.client = client;
        this.numParts = numParts;
        this.limit = 0;
    }

    public void load() {
        parse();
    }

    protected void saveBundle(Iterable<Resource> resources) {
        BundleBuilder bb = new BundleBuilder(client.getFhirContext());
        for (Resource r : resources) {
            if (r != null)
                bb.addTransactionCreateEntry(r);
        }
        client.transaction().withBundle(bb.getBundle()).execute();
    }

    private void processRecords(Iterable<CSVRecord> records) {
        int taken = 0, total = 0;
        List<Resource> buffer = new ArrayList<>();
        for (CSVRecord rec : records) {
            if (limit > 0 && taken >= limit)
                break;
            if (rec.isConsistent()) {
                Resource res = createResource(rec);
                if (res == null)
                    continue;
                buffer.add(res);
                taken++;
                if (taken % 1000 == 0) {
                    lg.info("loaded %d resources".formatted(taken));
                }
                if (taken % 100 == 0) {
                    saveBundle(buffer);
                    buffer.clear();
                }
            }
            total++;
        }
        lg.info("loaded %d/%d resources".formatted(taken, total));
    }

    private void parse() {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        for (int i = 1; i <= numParts; i++) {
            try {
                String path = "dataset/output_" + i + "/csv/" + subject + ".csv";
                File part = new ClassPathResource(path).getFile();

                Reader r = new FileReader(part);
                Iterable<CSVRecord> records = csvFormat.parse(r);

                processRecords(records);
            }
            catch(IOException e) {
                lg.error(e.getMessage());
            }
        }
    }
}
