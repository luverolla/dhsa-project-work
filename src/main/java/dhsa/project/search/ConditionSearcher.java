package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.adapter.AdapterService;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.ConditionFilter;
import dhsa.project.view.ConditionView;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConditionSearcher extends BaseSearcher<ConditionView, ConditionFilter> {

    @Autowired
    private FhirService fhirService;

    @Autowired
    private AdapterService adapter;

    public ConditionView transform(Resource r) {
        return adapter.getView((Condition) r);
    }

    public IQuery<?> search(ConditionFilter cse) {
        IQuery<?> query = fhirService.getClient().search()
            .forResource(Condition.class);

        if (!cse.getPatient().isEmpty())
            query = query.where(Condition.SUBJECT.hasId("Patient/" + cse.getPatient()));
        if (!cse.getEncounter().isEmpty())
            query = query.where(Condition.ENCOUNTER.hasId("Encounter/" + cse.getEncounter()));

        if (!cse.getCode().isEmpty())
            query = query.where(Condition.CODE.exactly().code(cse.getCode()));
        if (!cse.getOnsetFrom().isEmpty())
            query = query.where(Condition.ONSET_DATE.afterOrEquals().day(cse.getOnsetFrom()));
        if (!cse.getOnsetTo().isEmpty())
            query = query.where(Condition.ONSET_DATE.beforeOrEquals().day(cse.getOnsetTo()));

        if (cse.getActive().equals("yes"))
            query = query.where(Condition.ABATEMENT_DATE.isMissing(true));
        else if (cse.getActive().equals("no")) {
            if (!cse.getAbatementFrom().isEmpty())
                query = query.where(Condition.ABATEMENT_DATE.afterOrEquals().day(cse.getAbatementFrom()));
            if (!cse.getAbatementTo().isEmpty())
                query = query.where(Condition.ABATEMENT_DATE.beforeOrEquals().day(cse.getAbatementTo()));
        }

        return query;
    }
}
