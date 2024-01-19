package dhsa.project.adapter;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import dhsa.project.fhir.FhirService;
import dhsa.project.view.EncounterView;
import dhsa.project.view.ViewService;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EncounterAdapter implements ResourceAdapter<Encounter, EncounterView> {

    @Autowired
    private ViewService viewService;

    @Autowired
    private FhirService fhirService;

    private DocumentReference getCDA(Encounter res) {
        try {
            return fhirService.getClient()
                .read().resource(DocumentReference.class)
                .withId(res.getIdElement().getIdPart())
                .execute();
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    private String getBase64CDA(DocumentReference cda) {
        if (cda == null)
            return "";
        return cda.getContentFirstRep().getAttachment().getDataElement().getValueAsString();
    }

    public EncounterView transform(Encounter res) {
        DocumentReference cda = getCDA(res);
        return new EncounterView(
            res.getIdElement().getIdPart(),
            "Encounter/" + res.getIdElement().getIdPart(),
            viewService.getPatientNR(res.getSubject()),
            viewService.getPractitionerNR(res.getParticipantFirstRep().getIndividual()),
            viewService.getOrganizationNR(res.getServiceProvider()),
            viewService.getCodingRef(res.getTypeFirstRep()),
            viewService.getCodingRef(res.getReasonCodeFirstRep()),
            viewService.getOrganizationNR(fhirService.getEOB(res).getInsurer()),
            viewService.getMoneyString(fhirService.getClaim(res).getItemFirstRep().getUnitPrice()),
            viewService.getMoneyString(fhirService.getClaim(res).getTotal()),
            viewService.getMoneyString(fhirService.getEOB(res).getItemFirstRep().getAdjudicationFirstRep().getAmount()),
            viewService.getDateString(res.getPeriod().getStart()),
            getBase64CDA(cda),
            cda == null ? "" : viewService.getDateTimeString(cda.getDate())
        );
    }
}
