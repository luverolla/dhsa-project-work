package dhsa.project.adapter;

import dhsa.project.dicom.DicomService;
import dhsa.project.view.ImagingStudyView;
import dhsa.project.view.ViewService;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImagingStudyAdapter implements ResourceAdapter<ImagingStudy, ImagingStudyView> {

    @Autowired
    private ViewService viewService;

    @Autowired
    private DicomService dicomService;

    public ImagingStudyView transform(ImagingStudy res) {
        return new ImagingStudyView(
            res.getIdPart(),
            res.getId(),
            viewService.getPatientNR(res.getSubject()),
            viewService.getEncounterNR(res.getEncounter()),
            viewService.getCodingRef(res.getSeriesFirstRep().getBodySite()),
            viewService.getCodingRef(res.getModalityFirstRep()),
            viewService.getCodingRef(res.getSeriesFirstRep().getInstanceFirstRep().getSopClass()),
            viewService.getDateString(res.getStarted()),
            dicomService.getDicomFile(res).serveAllFrames()
        );
    }
}
