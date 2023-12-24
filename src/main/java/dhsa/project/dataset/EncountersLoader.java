package dhsa.project.dataset;

import ca.uhn.fhir.util.BundleBuilder;
import dhsa.project.service.FhirWrapper;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

public class EncountersLoader implements Loader {

    private final Iterable<CSVRecord> records;

    public EncountersLoader() {
        records = Helper.parse("encounters");
        if (records == null) {
            Helper.logSevere("Failed to load encounters");
        }
    }

    @Override
    @SneakyThrows
    public void load() {
        int count = 0;
        List<Encounter> encBuffer = new ArrayList<>();
        List<Claim> claimBuffer = new ArrayList<>();
        List<ExplanationOfBenefit> eobBuffer = new ArrayList<>();

        for (CSVRecord rec : records) {
            Reference pat = Helper.resolveUID(Patient.class, rec.get("PATIENT"));
            Reference pro = Helper.resolveUID(Practitioner.class, rec.get("PROVIDER"));
            Reference org = Helper.resolveUID(Organization.class, rec.get("ORGANIZATION"));
            Reference pay = Helper.resolveUID(Organization.class, rec.get("PAYER"));

            Encounter encounter = new Encounter();
            Claim claim = new Claim();
            ExplanationOfBenefit eob = new ExplanationOfBenefit();

            encounter.setId(rec.get("Id"));
            encounter.addIdentifier()
                .setType(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                        .setCode("ANON")
                        .setDisplay("Anonymous ID")
                    )
                )
                .setSystem("urn:ietf:rfc:3986")
                .setValue(rec.get("Id"));

            encounter.setSubject(pat);
            encounter.addParticipant()
                .setIndividual(pro);
            encounter.setServiceProvider(org);

            encounter.addType()
                .addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("CODE"))
                    .setDisplay(rec.get("DESCRIPTION"))
                );

            encounter.addReasonCode()
                .addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("REASONCODE"))
                    .setDisplay(rec.get("REASONDESCRIPTION"))
                );

            encounter.setPeriod(new Period()
                .setStart(Helper.parseDate(rec.get("START")))
            );

            if (Helper.hasProp(rec, "STOP"))
                encounter.getPeriod().setEnd(Helper.parseDate(rec.get("STOP")));

            claim.addItem()
                .addEncounter(new Reference(encounter))
                .setQuantity(new Quantity()
                    .setValue(1)
                    .setUnit("#")
                    .setSystem("http://unitsofmeasure.org")
                    .setCode("#")
                )
                .setUnitPrice(new Money()
                    .setValue(Double.parseDouble(rec.get("BASE_ENCOUNTER_COST")))
                    .setCurrency("USD")
                )
                .setNet(new Money()
                    .setValue(Double.parseDouble(rec.get("TOTAL_CLAIM_COST")))
                    .setCurrency("USD")
                );

            claim.setPatient(pat);
            claim.setTotal(new Money()
                .setValue(Double.parseDouble(rec.get("BASE_ENCOUNTER_COST")))
                .setCurrency("USD")
            );

            eob.setStatus(ExplanationOfBenefit.ExplanationOfBenefitStatus.ACTIVE);
            eob.setOutcome(ExplanationOfBenefit.RemittanceOutcome.COMPLETE);
            eob.setClaim(new Reference(claim));
            eob.setPatient(pat);
            eob.setInsurer(pay);
            eob.getPayee().setParty(pat);

            eob.addItem()
                .addEncounter(new Reference(encounter))
                .addAdjudication()
                .setCategory(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/adjudication")
                        .setCode("benefit")
                    )
                )
                .setAmount(new Money()
                    .setValue(Double.parseDouble(rec.get("PAYER_COVERAGE")))
                    .setCurrency("USD")
                );

            count++;
            encBuffer.add(encounter);
            claimBuffer.add(claim);
            eobBuffer.add(eob);

            if (count % 100 == 0) {
                BundleBuilder bb = new BundleBuilder(FhirWrapper.getContext());
                encBuffer.forEach(bb::addTransactionUpdateEntry);
                claimBuffer.forEach(bb::addTransactionCreateEntry);
                eobBuffer.forEach(bb::addTransactionCreateEntry);
                FhirWrapper.getClient().transaction().withBundle(bb.getBundle()).execute();

                if (count % 1000 == 0)
                    Helper.logInfo("Loaded %d encounters", count);

                encBuffer.clear();
                claimBuffer.clear();
                eobBuffer.clear();
            }
        }


        Helper.logInfo("loaded ALL encounters");
    }
}
