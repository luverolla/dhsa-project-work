package dhsa.project.bridge;

import dhsa.project.view.ViewService;
import dhsa.project.fhir.FhirService;
import dhsa.project.view.PrescriptionView;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionBridge implements ResourceBridge<MedicationRequest, PrescriptionView> {

    @Autowired
    private FhirService fhirService;

    @Autowired
    private ViewService viewService;

    public PrescriptionView transform(MedicationRequest req) {
        Claim claim = fhirService.getClaim(req);
        return new PrescriptionView(
            viewService.getPatientNR(req.getSubject()),
            viewService.getEncounterNR(req.getEncounter()),
            viewService.getCodingRef(req.getMedicationCodeableConcept()),
            viewService.getCodingRef(req.getReasonCodeFirstRep()),
            viewService.getDateString(req.getDispenseRequest().getValidityPeriod().getStart()),
            req.getDispenseRequest().getValidityPeriod().getEnd() == null ? "Ongoing" :
               viewService. getDateString(req.getDispenseRequest().getValidityPeriod().getEnd()),
            claim.getItemFirstRep().getQuantity().getValue().toString(),
            viewService.getMoneyString(claim.getItemFirstRep().getUnitPrice()),
            viewService.getMoneyString(claim.getTotal()),
            viewService.getMoneyString(fhirService.getEOB(req).getItemFirstRep().getAdjudicationFirstRep().getAmount()),
            viewService.getOrganizationNR(fhirService.getEOB(req).getInsurer())
        );
    }
}
