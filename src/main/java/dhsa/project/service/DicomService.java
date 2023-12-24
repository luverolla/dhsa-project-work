package dhsa.project.service;

import dhsa.project.data.DicomHandle;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class DicomService {

    private final Logger lg = Logger.getLogger(getClass().getName());

    public String getFrame(String filePath, int index) {
        DicomHandle handle = new DicomHandle(filePath);
        return handle.serveFrame(index);
    }
}
