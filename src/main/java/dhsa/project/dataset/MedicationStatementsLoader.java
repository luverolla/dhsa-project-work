package dhsa.project.dataset;

import ca.uhn.fhir.util.BundleBuilder;
import dhsa.project.service.FhirWrapper;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicationStatementsLoader implements Loader {

    private final Iterable<CSVRecord> records;
    private final Iterable<CSVRecord> encRecords;
    private final Map<String, CSVRecord> encIndex;

    public MedicationStatementsLoader() {
        records = Helper.parse("medications");
        encRecords = Helper.parse("encounters");
        if (records == null || encRecords == null) {
            Helper.logSevere("Failed to load medications");
        }
        encIndex = makeIndex();
    }

    private Map<String, CSVRecord> makeIndex() {
        Map<String, CSVRecord> index = new HashMap<>();
        for (CSVRecord rec : encRecords)
            index.put(rec.get("Id"), rec);
        return index;
    }

    @Override
    @SneakyThrows
    public void load() {
        int count = 0;
        List<MedicationStatement> buffer = new ArrayList<>();
        List<Claim> clmBuffer = new ArrayList<>();
        List<ExplanationOfBenefit> eobBuffer = new ArrayList<>();

        for (CSVRecord rec : records) {
            Reference pat = Helper.resolveUID(Patient.class, rec.get("PATIENT"));
            Reference enc = Helper.resolveUID(Encounter.class, rec.get("ENCOUNTER"));

            if (pat == null || enc == null)
                continue;

            MedicationStatement mst = new MedicationStatement();
            mst.setSubject(pat);
            mst.setContext(enc);
            mst.setMedication(new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(rec.get("CODE"))
                    .setDisplay(rec.get("DESCRIPTION"))
                )
            );

            if (Helper.hasProp(rec, "STOP")) {
                mst.getEffectivePeriod().setStart(Helper.parseDate(rec.get("START")));
                mst.getEffectivePeriod().setEnd(Helper.parseDate(rec.get("STOP")));
                mst.setStatus(MedicationStatement.MedicationStatementStatus.COMPLETED);
            } else {
                mst.getEffectivePeriod().setStart(Helper.parseDate(rec.get("START")));
                mst.setStatus(MedicationStatement.MedicationStatementStatus.ACTIVE);
            }

            // claim
            Claim claim = new Claim();
            claim.setPatient(pat);
            claim.setPrescription(new Reference(mst));
            claim.setTotal(new Money()
                .setValue(Double.parseDouble(rec.get("TOTALCOST")))
                .setCurrency("USD")
            );

            claim.addItem()
                .addEncounter(enc)
                .setQuantity(new Quantity()
                    .setValue(Integer.parseInt(rec.get("DISPENSES")))
                    .setUnit("#")
                    .setSystem("http://unitsofmeasure.org")
                    .setCode("#")
                )
                .setUnitPrice(new Money()
                    .setValue(Double.parseDouble(rec.get("BASE_COST")))
                    .setCurrency("USD")
                )
                .setNet(new Money()
                    .setValue(Double.parseDouble(rec.get("TOTALCOST")))
                    .setCurrency("USD")
                );

            // eob
            String payId = encIndex.get(rec.get("ENCOUNTER")).get("PAYER");
            Reference pay = Helper.resolveUID(Organization.class, payId);

            ExplanationOfBenefit eob = new ExplanationOfBenefit();
            eob.setStatus(ExplanationOfBenefit.ExplanationOfBenefitStatus.ACTIVE);
            eob.setOutcome(ExplanationOfBenefit.RemittanceOutcome.COMPLETE);
            eob.setClaim(new Reference(claim));
            eob.setPatient(pat);
            eob.setInsurer(pay);
            eob.getPayee().setParty(pat);
            eob.addItem()
                .addEncounter(enc)
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
            buffer.add(mst);
            clmBuffer.add(claim);
            eobBuffer.add(eob);

            if (count % 100 == 0) {
                BundleBuilder bb = new BundleBuilder(FhirWrapper.getContext());
                buffer.forEach(bb::addTransactionCreateEntry);
                clmBuffer.forEach(bb::addTransactionCreateEntry);
                eobBuffer.forEach(bb::addTransactionCreateEntry);
                FhirWrapper.getClient().transaction().withBundle(bb.getBundle()).execute();

                if (count % 1000 == 0)
                    Helper.logInfo("Loaded %d medication statements", count);

                buffer.clear();
                clmBuffer.clear();
                eobBuffer.clear();
            }
        }

        Helper.logInfo("Loaded ALL medication statements");
    }
}
