package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.bridge.EncounterBridge;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.EncounterFilter;
import dhsa.project.view.EncounterView;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EncounterSearcher extends BaseSearcher<EncounterView, EncounterFilter> {

    @Autowired
    private EncounterBridge bridge;

    @Autowired
    private FhirService fhirService;

    public EncounterView transform(Resource res) {
        return bridge.transform((Encounter) res);
    }

    public IQuery<?> search(EncounterFilter ese) {
        IQuery<?> query = fhirService.getClient().search().forResource(Encounter.class);

        if (!ese.getPatient().isEmpty())
            query = query.where(Encounter.SUBJECT.hasId("Patient/" + ese.getPatient()));

        if (!ese.getDateFrom().isEmpty())
            query = query.where(Encounter.DATE.afterOrEquals().day(ese.getDateFrom()));
        if (!ese.getDateTo().isEmpty())
            query = query.where(Encounter.DATE.beforeOrEquals().day(ese.getDateTo()));
        if (!ese.getType().isEmpty())
            query = query.where(Encounter.TYPE.exactly().code(ese.getType()));
        if (!ese.getReason().isEmpty())
            query = query.where(Encounter.REASON_CODE.exactly().code(ese.getReason()));

        return query;
    }
}
