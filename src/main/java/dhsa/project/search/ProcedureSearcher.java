package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.adapter.ProcedureAdapter;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.ProcedureFilter;
import dhsa.project.view.ProcedureView;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcedureSearcher extends BaseSearcher<ProcedureView, ProcedureFilter> {

    @Autowired
    private ProcedureAdapter adapter;

    @Autowired
    private FhirService fhirService;

    public ProcedureView transform(Resource res) {
        return adapter.transform((Procedure) res);
    }

    public IQuery<?> search(ProcedureFilter pse) {
        IQuery<?> query = fhirService.getClient().search()
            .forResource(Procedure.class);

        if (!pse.getPatient().isEmpty())
            query = query.where(Procedure.SUBJECT.hasId("Patient/" + pse.getPatient()));
        if (!pse.getEncounter().isEmpty())
            query = query.where(Procedure.ENCOUNTER.hasId("Encounter/" + pse.getEncounter()));

        if (!pse.getCode().isEmpty())
            query = query.where(Procedure.CODE.exactly().code(pse.getCode()));
        if (!pse.getReason().isEmpty())
            query = query.where(Procedure.REASON_CODE.exactly().code(pse.getReason()));
        if (!pse.getDateFrom().isEmpty())
            query = query.where(Procedure.DATE.afterOrEquals().day(pse.getDateFrom()));
        if (!pse.getDateTo().isEmpty())
            query = query.where(Procedure.DATE.beforeOrEquals().day(pse.getDateTo()));

        return query;
    }
}
