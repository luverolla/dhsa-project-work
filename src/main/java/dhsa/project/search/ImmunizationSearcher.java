package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.bridge.ImmunizationBridge;
import dhsa.project.view.ViewService;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.ImmunizationFilter;
import dhsa.project.view.ImmunizationView;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImmunizationSearcher extends BaseSearcher<ImmunizationView, ImmunizationFilter> {

    @Autowired
    private FhirService fhirService;

    @Autowired
    private ImmunizationBridge bridge;

    public ImmunizationView transform(Resource res) {
        return bridge.transform((Immunization) res);
    }

    public IQuery<?> search(ImmunizationFilter searchElement) {
        IQuery<?> query = fhirService.getClient()
            .search().forResource(Immunization.class);

        if (!searchElement.getPatient().isEmpty())
            query = query.where(Immunization.PATIENT.hasId("Patient/" + searchElement.getPatient()));

        if (!searchElement.getVaccine().isEmpty())
            query = query.where(Immunization.VACCINE_CODE.exactly().code(searchElement.getVaccine()));

        if (!searchElement.getDateFrom().isEmpty())
            query = query.where(Immunization.DATE.afterOrEquals().day(searchElement.getDateFrom()));
        if (!searchElement.getDateTo().isEmpty())
            query = query.where(Immunization.DATE.beforeOrEquals().day(searchElement.getDateTo()));

        return query;
    }
}
