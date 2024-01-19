package dhsa.project.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

/**
 * Utility class with shared fields and methods for executing FHIR requests.
 */
@Service
public class FhirService {

    /**
     * Returns the FHIR client from the global instance of the FHIR context.
     *
     * @return FHIR client
     */
    public IGenericClient getClient() {
        return FhirWrapper.getClient();
    }

    /**
     * Resolves a FHIR resource by its type and ID.
     *
     * @param type FHIR resource type class
     * @param id   resource ID
     * @return FHIR resource (needs casting to match the desired type)
     */
    public Resource resolveId(Class<? extends Resource> type, String id) {
        return FhirWrapper.getClient().read().resource(type).withId(id).execute();
    }

    /**
     * Get the Claim related to the given Encounter.
     *
     * @param res Encounter resource
     * @return Claim resource
     */
    public Claim getClaim(Encounter res) {
        return getClient().search().forResource(Claim.class)
            .where(Claim.ENCOUNTER.hasId("Encounter/" + res.getIdElement().getIdPart()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute().getEntry().stream()
            .map(org.hl7.fhir.r4.model.Bundle.BundleEntryComponent::getResource)
            .map(Claim.class::cast)
            .findFirst().orElse(null);
    }

    /**
     * Get the Claim related to the given MedicationRequest.
     *
     * @param res MedicationRequest resource
     * @return Claim resource
     */
    public Claim getClaim(MedicationRequest res) {
        return getClient().search().forResource(Claim.class)
            .where(Claim.ENCOUNTER.hasId(res.getEncounter().getReference()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute().getEntry().stream()
            .map(org.hl7.fhir.r4.model.Bundle.BundleEntryComponent::getResource)
            .map(Claim.class::cast)
            .toList().get(1);
    }

    /**
     * Get the ExplanationOfBenefit related to the given Encounter.
     *
     * @param res Encounter resource
     * @return ExplanationOfBenefit resource
     */
    public ExplanationOfBenefit getEOB(Encounter res) {
        return getClient().search().forResource(ExplanationOfBenefit.class)
            .where(ExplanationOfBenefit.CLAIM.hasId("Claim/" + getClaim(res).getIdElement().getIdPart()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute().getEntry().stream()
            .map(org.hl7.fhir.r4.model.Bundle.BundleEntryComponent::getResource)
            .map(ExplanationOfBenefit.class::cast)
            .findFirst().orElse(null);
    }

    /**
     * Get the ExplanationOfBenefit related to the given MedicationRequest.
     *
     * @param res MedicationRequest resource
     * @return ExplanationOfBenefit resource
     */
    public ExplanationOfBenefit getEOB(MedicationRequest res) {
        return getClient().search().forResource(ExplanationOfBenefit.class)
            .where(ExplanationOfBenefit.CLAIM.hasId("Claim/" + getClaim(res).getIdElement().getIdPart()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute().getEntry().stream()
            .map(org.hl7.fhir.r4.model.Bundle.BundleEntryComponent::getResource)
            .map(ExplanationOfBenefit.class::cast)
            .findFirst().orElse(null);
    }
}
