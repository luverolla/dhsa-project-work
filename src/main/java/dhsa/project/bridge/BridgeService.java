package dhsa.project.bridge;

import dhsa.project.view.*;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BridgeService {

    @Autowired
    private ConditionBridge cond;

    @Autowired
    private DeviceRequestBridge dev;

    @Autowired
    private PrescriptionBridge pre;

    @Autowired
    private ObservationBridge obs;

    @Autowired
    private OrganizationBridge org;

    @Autowired
    private PatientBridge pat;

    @Autowired
    private PractitionerBridge pra;

    @Autowired
    private ImmunizationBridge imm;

    @Autowired
    private ImagingStudyBridge img;

    @Autowired
    private ProcedureBridge pro;

    @Autowired
    private EncounterBridge enc;

    public ConditionView getView(Condition c) {
        return cond.transform(c);
    }

    public EncounterView getView(Encounter e) {
        return enc.transform(e);
    }

    public DeviceRequestView getView(DeviceRequest d) {
        return dev.transform(d);
    }

    public PrescriptionView getView(MedicationRequest r) {
        return pre.transform(r);
    }

    public ImmunizationView getView(Immunization i) {
        return imm.transform(i);
    }

    public ProcedureView getView(Procedure p) {
        return pro.transform(p);
    }

    public ImagingStudyView getView(ImagingStudy i) {
        return img.transform(i);
    }

    public ObservationView getView(Observation o) {
        return obs.transform(o);
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
