package dhsa.project.dataset;

import ca.uhn.fhir.util.BundleBuilder;
import dhsa.project.service.FhirWrapper;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DevicesLoader implements Loader {

    private final Iterable<CSVRecord> records;

    public DevicesLoader() {
        records = Helper.parse("devices");
        if (records == null) {
            Helper.logSevere("Failed to load devices");
        }
    }

    private Map<String, String> splitUDI(String udi) {
        String[] parts = udi.split("\\(([0-9]{2})\\)");
        return Map.of(
            "01", parts[1],
            "11", parts[2],
            "17", parts[3],
            "10", parts[4],
            "21", parts[5]
        );
    }

    @Override
    @SneakyThrows
    public void load() {
        int count = 0;
        List<Device> devBuffer = new ArrayList<>();
        List<DeviceRequest> reqBuffer = new ArrayList<>();

        for (CSVRecord rec : records) {
            Reference pat = Helper.resolveUID(Patient.class, rec.get("PATIENT"));
            Reference enc = Helper.resolveUID(Encounter.class, rec.get("ENCOUNTER"));

            Device dev = new Device();
            DeviceRequest dr = new DeviceRequest();

            dev.setPatient(pat);

            Map<String, String> udiParts = splitUDI(rec.get("UDI"));
            dev.addUdiCarrier()
                .setDeviceIdentifier(udiParts.get("01"))
                .setIssuer(udiParts.get("11"))
                .setJurisdiction(udiParts.get("17"))
                .setCarrierAIDC(udiParts.get("10").getBytes())
                .setCarrierHRF(udiParts.get("21"));

            dev.addIdentifier().setType(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://hl7.org/fhir/identifier-type")
                        .setCode("UDI")
                        .setDisplay("Universal Device Identifier")
                    )
                )
                .setSystem("urn:ietf:rfc:3986")
                .setValue(rec.get("UDI"));

            dev.setType(new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("CODE"))
                    .setDisplay(rec.get("DESCRIPTION"))
                )
            );

            dr.setSubject(pat);
            dr.setEncounter(enc);
            dr.setCode(new Reference(dev));

            if (Helper.hasProp(rec, "STOP"))
                dr.setOccurrence(new Period()
                    .setStart(Helper.parseDate(rec.get("START")))
                    .setEnd(Helper.parseDate(rec.get("STOP")))
                );
            else
                dr.setOccurrence(new Period()
                    .setStart(Helper.parseDate(rec.get("START")))
                );

            count++;
            devBuffer.add(dev);
            reqBuffer.add(dr);

            if (count % 100 == 0) {
                BundleBuilder bb = new BundleBuilder(FhirWrapper.getContext());
                devBuffer.forEach(bb::addTransactionCreateEntry);
                reqBuffer.forEach(bb::addTransactionCreateEntry);
                FhirWrapper.getClient().transaction().withBundle(bb.getBundle()).execute();

                if (count % 1000 == 0)
                    Helper.logInfo("Loaded %d devices", count);

                devBuffer.clear();
                reqBuffer.clear();
            }
        }

        Helper.logInfo("Loaded ALL devices");
    }
}
