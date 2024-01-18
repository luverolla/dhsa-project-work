package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.bridge.ImagingStudyBridge;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.ImagingStudyFilter;
import dhsa.project.view.ImagingStudyView;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImagingStudySearcher extends BaseSearcher<ImagingStudyView, ImagingStudyFilter> {

    @Autowired
    private ImagingStudyBridge bridge;

    @Autowired
    private FhirService fhirService;

    public ImagingStudyView transform(Resource res) {
        return bridge.transform((ImagingStudy) res);
    }

    public IQuery<?> search(ImagingStudyFilter ise) {
        IQuery<?> query = fhirService.getClient().search()
            .forResource(ImagingStudy.class);

        if (!ise.getPatient().isEmpty())
            query = query.where(ImagingStudy.SUBJECT.hasId("Patient/" + ise.getPatient()));
        if (!ise.getEncounter().isEmpty())
            query = query.where(ImagingStudy.ENCOUNTER.hasId("Encounter/" + ise.getEncounter()));

        if (!ise.getModality().isEmpty())
            query = query.where(ImagingStudy.MODALITY.exactly().code(ise.getModality()));
        if (!ise.getDateFrom().isEmpty())
            query = query.where(ImagingStudy.STARTED.afterOrEquals().day(ise.getDateFrom()));
        if (!ise.getDateTo().isEmpty())
            query = query.where(ImagingStudy.STARTED.beforeOrEquals().day(ise.getDateTo()));
        if (!ise.getBodySite().isEmpty())
            query = query.where(ImagingStudy.BODYSITE.exactly().code(ise.getBodySite()));

        return query;
    }
}
