package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.adapter.PractitionerAdapter;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.PractitionerFilter;
import dhsa.project.view.PractitionerView;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PractitionerSearcher extends BaseSearcher<PractitionerView, PractitionerFilter> {

    @Autowired
    private FhirService fhirService;

    @Autowired
    private PractitionerAdapter adapter;

    public PractitionerView transform(Resource res) {
        return adapter.transform((Practitioner) res);
    }

    public IQuery<?> search(PractitionerFilter pse) {
        IQuery<?> query = fhirService.getClient()
            .search().forResource(Practitioner.class);

        if (pse.getName() != null)
            query = query.where(Practitioner.NAME.matches().value(pse.getName()));
        if (pse.getSex() != null)
            query = query.and(Practitioner.GENDER.exactly().code(pse.getSex()));

        return query;
    }
}
