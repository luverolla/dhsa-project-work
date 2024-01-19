package dhsa.project.adapter;

import dhsa.project.view.ProcedureView;
import dhsa.project.view.ViewService;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcedureAdapter implements ResourceAdapter<Procedure, ProcedureView> {

    @Autowired
    private ViewService viewService;

    public ProcedureView transform(Procedure res) {
        return new ProcedureView(
            viewService.getPatientNR(res.getSubject()),
            viewService.getEncounterNR(res.getEncounter()),
            viewService.getCodingRef(res.getCode()),
            viewService.getCodingRef(res.getReasonCodeFirstRep()),
            viewService.getDateString(res.getPerformedDateTimeType().getValue())
        );
    }
}
