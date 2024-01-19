package dhsa.project.adapter;

import dhsa.project.view.*;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdapterService {

    @Autowired
    private ConditionAdapter cond;

    @Autowired
    private DeviceRequestAdapter dev;

    @Autowired
    private OrganizationAdapter org;

    @Autowired
    private PatientAdapter pat;

    @Autowired
    private PractitionerAdapter pra;

    @Autowired
    private ImagingStudyAdapter img;

    @Autowired
    private EncounterAdapter enc;

    public ConditionView getView(Condition c) {
        return cond.transform(c);
    }

    public EncounterView getView(Encounter e) {
        return enc.transform(e);
    }

    public DeviceRequestView getView(DeviceRequest d) {
        return dev.transform(d);
    }

    public ImagingStudyView getView(ImagingStudy i) {
        return img.transform(i);
    }

    public OrganizationView getView(Organization o) {
        return org.transform(o);
    }

    public PatientView getView(Patient p) {
        return pat.transform(p);
    }

    public PractitionerView getView(Practitioner p) {
        return pra.transform(p);
    }

}
