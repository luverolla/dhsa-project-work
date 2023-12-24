package dhsa.project.dataset;

import ca.uhn.fhir.util.BundleBuilder;
import com.pixelmed.dicom.TagFromName;
import dhsa.project.data.DicomHandle;
import dhsa.project.service.FhirWrapper;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImagingStudiesLoader implements Loader {

    private final Iterable<CSVRecord> records;

    public ImagingStudiesLoader() {
        records = Helper.parse("imaging_studies");
        if (records == null) {
            Helper.logSevere("Failed to load imaging studies");
        }
    }

    private DicomHandle getDicomFile(String patientId) {
        Path root = Path.of(Helper.basePath + "/dicom");
        try (var stream = Files.walk(root)) {
            Path path = stream
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().contains(patientId))
                .findFirst().orElse(null);

            if (path == null)
                return null;

            return new DicomHandle(path.toFile());
        } catch (IOException e) {
            return null;
        }
    }

    @SneakyThrows
    @Override
    public void load() {
        int count = 0;
        List<ImagingStudy> buffer = new ArrayList<>();

        for (CSVRecord rec : records) {
            Reference pat = Helper.resolveUID(Patient.class, rec.get("PATIENT"));
            Reference enc = Helper.resolveUID(Encounter.class, rec.get("ENCOUNTER"));
            DicomHandle dicom = getDicomFile(rec.get("PATIENT"));

            if (dicom == null)
                continue;

            ImagingStudy is = new ImagingStudy();
            is.setId(rec.get("Id"));

            is.addIdentifier()
                .setType(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                        .setCode("ANON")
                        .setDisplay("Anonymous ID")
                    )
                )
                .setSystem("urn:ietf:rfc:3986")
                .setValue(rec.get("Id"));

            is.setStarted(Helper.parseDate(rec.get("DATE")));
            is.setSubject(pat);
            is.setEncounter(enc);

            is.addModality(new Coding()
                .setSystem("http://dicom.nema.org/resources/ontology/DCM")
                .setCode(rec.get("MODALITY_CODE"))
                .setDisplay(rec.get("MODALITY_DESCRIPTION"))
            );

            ImagingStudy.ImagingStudySeriesComponent series = is.addSeries();
            if (dicom.hasAttr(TagFromName.SeriesDate))
                series.setStarted(Helper.parseDate(dicom.getString(TagFromName.SeriesDate)));
            series.setUid(dicom.getString(TagFromName.SeriesInstanceUID));
            series.setNumber(dicom.getInt(TagFromName.SeriesNumber));
            series.setDescription(dicom.getString(TagFromName.SeriesDescription));
            series.setBodySite(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("BODYSITE_CODE"))
                    .setDisplay(rec.get("BODYSITE_DESCRIPTION"))
                );
            series.setLaterality(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(dicom.getString(TagFromName.Laterality))
                )
                .setNumberOfInstances(
                    dicom.hasAttr(TagFromName.NumberOfSeriesRelatedInstances) ?
                        dicom.getInt(TagFromName.NumberOfSeriesRelatedInstances) : 1
                )
                .setModality(new Coding()
                    .setSystem("http://dicom.nema.org/resources/ontology/DCM")
                    .setCode(rec.get("MODALITY_CODE"))
                    .setDisplay(rec.get("MODALITY_DESCRIPTION"))
                )
                .setBodySite(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("BODYSITE_CODE"))
                    .setDisplay(rec.get("BODYSITE_DESCRIPTION"))
                )
                .addInstance()
                .setUid(dicom.getString(TagFromName.SOPInstanceUID))
                .setNumber(dicom.getInt(TagFromName.InstanceNumber))
                .setSopClass(new Coding()
                    .setSystem("http://dicom.nema.org/resources/ontology/DCM")
                    .setCode(rec.get("SOP_CODE"))
                    .setDisplay(rec.get("SOP_DESCRIPTION"))
                );

            count++;
            buffer.add(is);

            if (count % 10 == 0) {
                BundleBuilder bb = new BundleBuilder(FhirWrapper.getContext());
                buffer.forEach(bb::addTransactionUpdateEntry);
                FhirWrapper.getClient().transaction().withBundle(bb.getBundle()).execute();

                if (count % 100 == 0)
                    Helper.logInfo("Loaded %d imaging studies", count);

                buffer.clear();
            }
        }

        Helper.logInfo("Loaded ALL imaging studies");
    }
}
