package dhsa.project.dicom;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import dhsa.project.fhir.FhirWrapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class DicomService {

    private String getFilenamePart(ImagingStudy img) {
        Patient patient = FhirWrapper.getClient().read().resource(Patient.class)
            .withId(img.getSubject().getReference()).execute();

        String givenName = patient.getNameFirstRep().getGivenAsSingleString(),
            surname = patient.getNameFirstRep().getFamily(),
            patientId = patient.getIdElement().getIdPart();

        return givenName + "_" + surname + "_" + patientId;
    }

    public File getFileObject(String filename) {
        Path root = Path.of("src/main/resources/coherent-11-07-2022/dicom");

        try (var stream = Files.walk(root)) {
            List<Path> paths = stream
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString()
                    .contains(filename)
                )
                .toList();

            if (paths.isEmpty())
                return null;

            for (Path path : paths) {
                DicomHandle dh = new DicomHandle(path.toFile());
                String studyUid = dh.getString(TagFromName.StudyInstanceUID);
                if (path.toString().contains(studyUid))
                    return path.toFile();
            }

            return null;
        } catch (IOException | DicomException e) {
            return null;
        }
    }

    public DicomHandle getDicomFile(String filename) {
        Path root = Path.of("src/main/resources/coherent-11-07-2022/dicom");

        try (var stream = Files.walk(root)) {
            List<Path> paths = stream
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString()
                    .contains(filename)
                )
                .toList();

            if (paths.isEmpty())
                return null;

            for (Path path : paths) {
                DicomHandle dh = new DicomHandle(path.toFile());
                String studyUid = dh.getString(TagFromName.StudyInstanceUID);
                if (path.toString().contains(studyUid))
                    return dh;
            }

            return null;
        } catch (IOException | DicomException e) {
            return null;
        }
    }

    public DicomHandle getDicomFile(ImagingStudy img) {
        return getDicomFile(getFilenamePart(img));
    }

    public String getBase64Binary(ImagingStudy img) {
        try {
            File file = getFileObject(getFilenamePart(img));
            byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
            return new String(encoded, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            return null;
        }
    }
}
