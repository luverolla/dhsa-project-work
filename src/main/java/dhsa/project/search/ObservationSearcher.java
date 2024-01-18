package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.bridge.ObservationBridge;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.ObservationFilter;
import dhsa.project.view.ObservationView;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObservationSearcher extends BaseSearcher<ObservationView, ObservationFilter> {
    @Autowired
    private ObservationBridge bridge;

    @Autowired
    private FhirService fhirService;

    public ObservationView transform(Resource res) {
        return bridge.transform((Observation) res);
    }

    public IQuery<?> search(ObservationFilter ose) {
        IQuery<?> query = fhirService.getClient().search()
            .forResource(Observation.class);

        if (!ose.getPatient().isEmpty())
            query = query.where(Observation.SUBJECT.hasId("Patient/" + ose.getPatient()));
        if (!ose.getEncounter().isEmpty())
            query = query.where(Observation.ENCOUNTER.hasId("Encounter/" + ose.getEncounter()));

        if (!ose.getCode().isEmpty())
            query = query.where(Observation.CODE.exactly().code(ose.getCode()));
        if (!ose.getDateFrom().isEmpty())
            query = query.where(Observation.DATE.afterOrEquals().day(ose.getDateFrom()));
        if (!ose.getDateTo().isEmpty())
            query = query.where(Observation.DATE.beforeOrEquals().day(ose.getDateTo()));
        if (!ose.getValueString().isEmpty())
            query = query.where(Observation.VALUE_STRING.matches().value(ose.getValueString()));
        if (ose.getValueFrom() != null)
            query = query.where((ICriterion<?>) Observation.VALUE_QUANTITY.greaterThanOrEquals()
                .number(ose.getValueFrom().toString()));
        if (ose.getValueTo() != null)
            query = query.where((ICriterion<?>) Observation.VALUE_QUANTITY.lessThanOrEquals()
                .number(ose.getValueTo().toString()));

        return query;
    }
}