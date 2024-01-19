package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.adapter.PrescriptionAdapter;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.PrescriptionFilter;
import dhsa.project.view.PrescriptionView;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionSearcher extends BaseSearcher<PrescriptionView, PrescriptionFilter> {

    @Autowired
    private FhirService fhirService;
    @Autowired
    private PrescriptionAdapter adapter;

    public PrescriptionView transform(Resource res) {
        return adapter.transform((MedicationRequest) res);
    }

    public IQuery<?> search(PrescriptionFilter pse) {
        IQuery<?> query = fhirService.getClient().search()
            .forResource(MedicationRequest.class);

        if (!pse.getPatient().isEmpty())
            query = query.where(MedicationRequest.SUBJECT.hasId("Patient/" + pse.getPatient()));
        if (!pse.getEncounter().isEmpty())
            query = query.where(MedicationRequest.ENCOUNTER.hasId("Encounter/" + pse.getEncounter()));

        if (!pse.getMedication().isEmpty())
            query = query.where(MedicationRequest.CODE.exactly().code(pse.getMedication()));
        if (!pse.getStartDateFrom().isEmpty())
            query = query.where(MedicationRequest.DATE.afterOrEquals().day(pse.getStartDateFrom()));
        if (!pse.getStartDateTo().isEmpty())
            query = query.where(MedicationRequest.DATE.beforeOrEquals().day(pse.getStartDateTo()));

        if (pse.getActive().equals("no"))
            query = query.where(MedicationRequest.STATUS.exactly().code("completed"));
        else if (pse.getActive().equals("yes"))
            query = query.where(MedicationRequest.STATUS.exactly().code("active"));

        return query;
    }
}
