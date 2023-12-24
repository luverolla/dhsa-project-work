package dhsa.project.dataset;

import java.io.File;
import java.nio.file.Files;

public class DicomFilesLoader implements Loader {

    @Override
    public void load() {
        File uploadDir = new File("uploads"),
            datasetDir = new File(Helper.basePath + "/dicom");

        if (!uploadDir.exists() || !uploadDir.isDirectory()) {
            boolean created = uploadDir.mkdir();
            if (!created) {
                Helper.logSevere("Failed to create uploads directory");
                return;
            }

            File dicomSrcDir = new File(datasetDir, "dicom"),
                dicomDstDir = new File(uploadDir, "dicom");

            created = dicomDstDir.mkdir();
            if (!created) {
                Helper.logSevere("Failed to create uploads/dicom directory");
                return;
            }

            File[] files = dicomSrcDir.listFiles();
            if (files == null) {
                Helper.logSevere("Failed to list files in %s", dicomSrcDir.getAbsolutePath());
                return;
            }
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        Files.copy(file.toPath(), new File(dicomDstDir, file.getName()).toPath());
                    } catch (Exception e) {
                        Helper.logSevere("Failed to copy file %s: %s", file.getName(), e.getMessage());
                        return;
                    }
                }
            }

        }
    }

}
