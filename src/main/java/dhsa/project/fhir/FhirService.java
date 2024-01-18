package dhsa.project.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

@Service
public class FhirService {
    public IGenericClient getClient() {
        return FhirWrapper.getClient();
    }

    public Resource resolveId(Class<? extends Resource> type, String id) {
        return FhirWrapper.getClient().read().resource(type).withId(id).execute();
    }

    public Claim getClaim(Encounter res) {
        return getClient().search().forResource(Claim.class)
            .where(Claim.ENCOUNTER.hasId("Encounter/" + res.getIdElement().getIdPart()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute().getEntry().stream()
            .map(org.hl7.fhir.r4.model.Bundle.BundleEntryComponent::getResource)
            .map(Claim.class::cast)
            .findFirst().orElse(null);
    }

    public Claim getClaim(MedicationRequest res) {
        return getClient().search().forResource(Claim.class)
            .where(Claim.ENCOUNTER.hasId(res.getEncounter().getReference()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute().getEntry().stream()
            .map(org.hl7.fhir.r4.model.Bundle.BundleEntryComponent::getResource)
            .map(Claim.class::cast)
            .toList().get(1);
    }

    public ExplanationOfBenefit getEOB(Encounter res) {
        return getClient().search().forResource(ExplanationOfBenefit.class)
            .where(ExplanationOfBenefit.CLAIM.hasId("Claim/" + getClaim(res).getIdElement().getIdPart()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute().getEntry().stream()
            .map(org.hl7.fhir.r4.model.Bundle.BundleEntryComponent::getResource)
            .map(ExplanationOfBenefit.class::cast)
            .findFirst().orElse(null);
    }

    public ExplanationOfBenefit getEOB(MedicationRequest res) {
        return getClient().search().forResource(ExplanationOfBenefit.class)
            .where(ExplanationOfBenefit.CLAIM.hasId("Claim/" + getClaim(res).getIdElement().getIdPart()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute().getEntry().stream()
            .map(org.hl7.fhir.r4.model.Bundle.BundleEntryComponent::getResource)
            .map(ExplanationOfBenefit.class::cast)
            .toList().get(1);
    }
}
