package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.bridge.OrganizationBridge;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.OrganizationFilter;
import dhsa.project.view.OrganizationView;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationSearcher extends BaseSearcher<OrganizationView, OrganizationFilter> {

    @Autowired
    private OrganizationBridge bridge;

    @Autowired
    private FhirService fhirService;

    public OrganizationView transform(Resource res) {
        return bridge.transform((Organization) res);
    }

    public IQuery<?> search(OrganizationFilter ose) {
        IQuery<?> query = fhirService.getClient().search()
            .forResource(Organization.class);

        if (!ose.getName().isEmpty())
            query = query.where(Organization.NAME.matches().value(ose.getName()));

        if (!ose.getCity().isEmpty())
            query = query.where(Organization.ADDRESS_CITY.matches().value(ose.getCity()));

        if (!ose.getState().isEmpty())
            query = query.where(Organization.ADDRESS_STATE.matches().value(ose.getState()));

        return query;
    }
}
