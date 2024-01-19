package dhsa.project.search;

import dhsa.project.filter.*;
import dhsa.project.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service bean holding the <tt>retrieve()</tt> method of all types of resource.
 */
@Service
public class SearchService {

    @Autowired
    private PatientSearcher pat;

    @Autowired
    private OrganizationSearcher org;

    @Autowired
    private PractitionerSearcher prac;

    @Autowired
    private EncounterSearcher enc;

    @Autowired
    private ProcedureSearcher proc;

    @Autowired
    private ImmunizationSearcher imm;

    @Autowired
    private ObservationSearcher obs;

    @Autowired
    private PrescriptionSearcher pre;

    @Autowired
    private DeviceRequestSearcher dev;

    @Autowired
    private ConditionSearcher cond;

    @Autowired
    private ImagingStudySearcher img;

    public ResultSet<PatientView> retrieve(PatientFilter pf, int page) {
        return pat.retrieve(pf, page);
    }

    public ResultSet<OrganizationView> retrieve(OrganizationFilter of, int page) {
        return org.retrieve(of, page);
    }

    public ResultSet<PractitionerView> retrieve(PractitionerFilter pf, int page) {
        return prac.retrieve(pf, page);
    }

    public ResultSet<EncounterView> retrieve(EncounterFilter ef, int page) {
        return enc.retrieve(ef, page);
    }

    public ResultSet<ProcedureView> retrieve(ProcedureFilter pf, int page) {
        return proc.retrieve(pf, page);
    }

    public ResultSet<ImmunizationView> retrieve(ImmunizationFilter ifl, int page) {
        return imm.retrieve(ifl, page);
    }

    public ResultSet<ObservationView> retrieve(ObservationFilter of, int page) {
        return obs.retrieve(of, page);
    }

    public ResultSet<PrescriptionView> retrieve(PrescriptionFilter pf, int page) {
        return pre.retrieve(pf, page);
    }

    public ResultSet<DeviceRequestView> retrieve(DeviceRequestFilter df, int page) {
        return dev.retrieve(df, page);
    }

    public ResultSet<ConditionView> retrieve(ConditionFilter cf, int page) {
        return cond.retrieve(cf, page);
    }

    public ResultSet<ImagingStudyView> retrieve(ImagingStudyFilter isf, int page) {
        return img.retrieve(isf, page);
    }
}
