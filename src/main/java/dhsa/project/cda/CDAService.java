package dhsa.project.cda;

import dhsa.project.dicom.DicomService;
import dhsa.project.fhir.FhirService;
import lombok.SneakyThrows;
import org.hl7.fhir.r4.model.*;
import org.openhealthtools.mdht.uml.cda.Encounter;
import org.openhealthtools.mdht.uml.cda.Observation;
import org.openhealthtools.mdht.uml.cda.Patient;
import org.openhealthtools.mdht.uml.cda.Person;
import org.openhealthtools.mdht.uml.cda.Procedure;
import org.openhealthtools.mdht.uml.cda.*;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.hl7.datatypes.*;
import org.openhealthtools.mdht.uml.hl7.vocab.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class CDAService {

    private final SimpleDateFormat onlyDateFmt = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat datetimeFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    @Autowired
    private DicomService dicomService;
    @Autowired
    private FhirService fhirService;

    private Patient mapPatient(org.hl7.fhir.r4.model.Patient patient) {
        Patient mapped = CDAFactory.eINSTANCE.createPatient();

        CE administrativeGenderCode = DatatypesFactory.eINSTANCE.createCE();
        administrativeGenderCode.setCode(patient.getGender().toCode());
        mapped.setAdministrativeGenderCode(administrativeGenderCode);

        Birthplace birthplace = CDAFactory.eINSTANCE.createBirthplace();
        Place place = CDAFactory.eINSTANCE.createPlace();
        Address address = (Address)
            patient.getExtensionByUrl("http://hl7.org/fhir/StructureDefinition/patient-birthPlace")
                .getValue();
        place.setAddr(
            DatatypesFactory.eINSTANCE.createAD().addText(address.getText())
        );
        birthplace.setPlace(place);
        mapped.setBirthplace(birthplace);

        TS birthTime = DatatypesFactory.eINSTANCE.createTS();
        birthTime.setValue(onlyDateFmt.format(patient.getBirthDate()));
        mapped.setBirthTime(birthTime);

        II id = DatatypesFactory.eINSTANCE.createII();
        id.setRoot(patient.getIdElement().getIdPart());
        mapped.setId(id);

        CE marital = DatatypesFactory.eINSTANCE.createCE();
        marital.setCode(patient.getMaritalStatus().getCodingFirstRep().getCode());
        mapped.setMaritalStatusCode(marital);

        PN name = DatatypesFactory.eINSTANCE.createPN();
        name
            .addGiven(patient.getNameFirstRep().getGivenAsSingleString())
            .addFamily(patient.getNameFirstRep().getFamily())
            .addPrefix(patient.getNameFirstRep().getPrefixAsSingleString());
        if (patient.getNameFirstRep().getSuffix() != null && !patient.getNameFirstRep().getSuffix().isEmpty())
            name.addSuffix(patient.getNameFirstRep().getSuffixAsSingleString());
        mapped.getNames().add(name);

        return mapped;
    }

    private Encounter mapEncounter(org.hl7.fhir.r4.model.Encounter encounter) {
        org.hl7.fhir.r4.model.Practitioner practitioner =
            (org.hl7.fhir.r4.model.Practitioner) fhirService.resolveId(
                org.hl7.fhir.r4.model.Practitioner.class,
                encounter.getParticipantFirstRep().getIndividual().getReference());

        Encounter mapped = CDAFactory.eINSTANCE.createEncounter();
        mapped.setClassCode(ActClass.ENC);
        mapped.setMoodCode(x_DocumentEncounterMood.EVN);

        CD code = DatatypesFactory.eINSTANCE.createCD();
        code.setCode(encounter.getTypeFirstRep().getCodingFirstRep().getCode());
        code.setCodeSystem(encounter.getTypeFirstRep().getCodingFirstRep().getSystem());
        code.setCodeSystemName("SNOMED-CT");
        code.setDisplayName(encounter.getTypeFirstRep().getCodingFirstRep().getDisplay());
        mapped.setCode(code);

        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
        low.setValue(datetimeFmt.format(encounter.getPeriod().getStart()));
        IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
        high.setValue(datetimeFmt.format(encounter.getPeriod().getEnd()));
        effectiveTime.setLow(low);
        effectiveTime.setHigh(high);
        mapped.setEffectiveTime(effectiveTime);

        II id = DatatypesFactory.eINSTANCE.createII();
        id.setRoot(encounter.getIdElement().getIdPart());
        mapped.getIds().add(id);

        mapped.getPerformers().add(mapPractitioner(practitioner));

        return mapped;
    }

    private Performer2 mapPractitioner(org.hl7.fhir.r4.model.Practitioner practitioner) {
        Performer2 mapped = CDAFactory.eINSTANCE.createPerformer2();
        AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
        II performerId = DatatypesFactory.eINSTANCE.createII();
        performerId.setRoot(practitioner.getIdElement().getIdPart());
        assignedEntity.getIds().add(performerId);
        Person assignedPerson = CDAFactory.eINSTANCE.createPerson();
        assignedPerson.getNames().add(
            (PN) DatatypesFactory.eINSTANCE.createPN()
                .addText(practitioner.getNameFirstRep().getText())
        );
        assignedEntity.setAssignedPerson(assignedPerson);
        mapped.setAssignedEntity(assignedEntity);
        return mapped;
    }

    private SubstanceAdministration mapPrescription(MedicationRequest mst) {
        SubstanceAdministration mapped = CDAFactory.eINSTANCE.createSubstanceAdministration();
        mapped.setClassCode(ActClass.SBADM);
        mapped.setMoodCode(x_DocumentSubstanceMood.EVN);

        CD code = DatatypesFactory.eINSTANCE.createCD();
        code.setCode("DRUG");
        code.setDisplayName("Drug");
        code.setCodeSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode");
        code.setCodeSystemName("HL7 CDA ActCode");
        mapped.setCode(code);

        if (mst.getStatus().equals(MedicationRequest.MedicationRequestStatus.ACTIVE)) {
            CS statusCode = DatatypesFactory.eINSTANCE.createCS();
            statusCode.setCode("active");
            mapped.setStatusCode(statusCode);

            SXCM_TS startTime = DatatypesFactory.eINSTANCE.createSXCM_TS();
            startTime.setValue(datetimeFmt.format(mst.getDispenseRequest().getValidityPeriod().getStart()));
            mapped.getEffectiveTimes().add(startTime);
        } else {
            CS statusCode = DatatypesFactory.eINSTANCE.createCS();
            statusCode.setCode("completed");
            mapped.setStatusCode(statusCode);

            SXCM_TS startTime = DatatypesFactory.eINSTANCE.createSXCM_TS();
            startTime.setValue(datetimeFmt.format(mst.getDispenseRequest().getValidityPeriod().getStart()));
            mapped.getEffectiveTimes().add(startTime);
            SXCM_TS endTime = DatatypesFactory.eINSTANCE.createSXCM_TS();
            endTime.setValue(datetimeFmt.format(mst.getDispenseRequest().getValidityPeriod().getEnd()));
            mapped.getEffectiveTimes().add(endTime);
        }

        Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
        consumable.setTypeCode(ParticipationType.CSM);
        ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
        LabeledDrug drug = CDAFactory.eINSTANCE.createLabeledDrug();
        CE drugCode = DatatypesFactory.eINSTANCE.createCE();
        drugCode.setCode(mst.getMedicationCodeableConcept().getCodingFirstRep().getCode());
        drugCode.setDisplayName(mst.getMedicationCodeableConcept().getCodingFirstRep().getDisplay());
        drugCode.setCodeSystem(mst.getMedicationCodeableConcept().getCodingFirstRep().getSystem());
        drugCode.setCodeSystemName("SNOMED-CT");
        drug.setCode(drugCode);
        manufacturedProduct.setManufacturedLabeledDrug(drug);
        consumable.setManufacturedProduct(manufacturedProduct);
        mapped.setConsumable(consumable);

        return mapped;
    }

    private SubstanceAdministration mapDeviceRequest(DeviceRequest req) {
        SubstanceAdministration mapped = CDAFactory.eINSTANCE.createSubstanceAdministration();
        mapped.setClassCode(ActClass.SBADM);
        mapped.setMoodCode(x_DocumentSubstanceMood.EVN);

        if (req.getStatus().equals(DeviceRequest.DeviceRequestStatus.ACTIVE)) {
            CS statusCode = DatatypesFactory.eINSTANCE.createCS();
            statusCode.setCode("active");
            mapped.setStatusCode(statusCode);

            SXCM_TS startTime = DatatypesFactory.eINSTANCE.createSXCM_TS();
            startTime.setValue(datetimeFmt.format(req.getOccurrencePeriod().getStart()));
            mapped.getEffectiveTimes().add(startTime);
        } else {
            CS statusCode = DatatypesFactory.eINSTANCE.createCS();
            statusCode.setCode("completed");
            mapped.setStatusCode(statusCode);

            SXCM_TS startTime = DatatypesFactory.eINSTANCE.createSXCM_TS();
            startTime.setValue(datetimeFmt.format(req.getOccurrencePeriod().getStart()));
            mapped.getEffectiveTimes().add(startTime);
            SXCM_TS endTime = DatatypesFactory.eINSTANCE.createSXCM_TS();
            endTime.setValue(datetimeFmt.format(req.getOccurrencePeriod().getEnd()));
            mapped.getEffectiveTimes().add(endTime);
        }

        Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
        consumable.setTypeCode(ParticipationType.DEV);
        ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
        II deviceUDI = DatatypesFactory.eINSTANCE.createII();
        deviceUDI.setRoot(req.getIdentifierFirstRep().getValue());
        manufacturedProduct.getIds().add(deviceUDI);
        Material device = CDAFactory.eINSTANCE.createMaterial();
        CE deviceCode = DatatypesFactory.eINSTANCE.createCE();
        deviceCode.setCode(req.getCodeCodeableConcept().getCodingFirstRep().getCode());
        deviceCode.setDisplayName(req.getCodeCodeableConcept().getCodingFirstRep().getDisplay());
        deviceCode.setCodeSystem(req.getCodeCodeableConcept().getCodingFirstRep().getSystem());
        deviceCode.setCodeSystemName("SNOMED-CT");
        device.setCode(deviceCode);
        manufacturedProduct.setManufacturedMaterial(device);
        consumable.setManufacturedProduct(manufacturedProduct);
        mapped.setConsumable(consumable);

        return mapped;
    }

    private Observation mapObservation(org.hl7.fhir.r4.model.Observation observation) {
        Observation mapped = CDAFactory.eINSTANCE.createObservation();
        mapped.setClassCode(ActClassObservation.OBS);
        mapped.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II id = DatatypesFactory.eINSTANCE.createII();
        id.setRoot("Observation/" + observation.getIdElement().getIdPart());
        mapped.getIds().add(id);

        CD code = DatatypesFactory.eINSTANCE.createCD();
        code.setCode(observation.getCode().getCodingFirstRep().getCode());
        code.setCodeSystem(observation.getCode().getCodingFirstRep().getSystem());
        code.setCodeSystemName("LOINC");
        code.setDisplayName(observation.getCode().getCodingFirstRep().getDisplay());
        mapped.setCode(code);

        if (observation.getValue() instanceof Quantity quantity) {
            PQ value = DatatypesFactory.eINSTANCE.createPQ();
            value.setValue(quantity.getValue().doubleValue());
            value.setUnit(quantity.getUnit());
            mapped.getValues().add(value);
        } else if (observation.getValue() instanceof IntegerType integer) {
            INT value = DatatypesFactory.eINSTANCE.createINT();
            value.setValue(integer.getValue());
            mapped.getValues().add(value);
        } else if (observation.getValue() instanceof StringType string) {
            ST value = DatatypesFactory.eINSTANCE.createST();
            value.addText(string.getValue());
            mapped.getValues().add(value);
        }

        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        effectiveTime.setValue(observation.getEffectiveDateTimeType().getValueAsString());
        mapped.setEffectiveTime(effectiveTime);

        return mapped;
    }

    private Observation mapCondition(Condition condition) {
        Observation mapped = CDAFactory.eINSTANCE.createObservation();
        mapped.setClassCode(ActClassObservation.COND);
        mapped.setMoodCode(x_ActMoodDocumentObservation.EVN);

        CD code = DatatypesFactory.eINSTANCE.createCD();
        code.setCode(condition.getCode().getCodingFirstRep().getCode());
        code.setCodeSystem(condition.getCode().getCodingFirstRep().getSystem());
        code.setCodeSystemName("SNOMED-CT");
        code.setDisplayName(condition.getCode().getCodingFirstRep().getDisplay());
        mapped.setCode(code);

        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
        low.setValue(condition.getOnsetDateTimeType().getValueAsString());
        effectiveTime.setLow(low);
        if (condition.getAbatementDateTimeType() != null) {
            IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
            high.setValue(condition.getAbatementDateTimeType().getValueAsString());
            effectiveTime.setHigh(high);
        }
        mapped.setEffectiveTime(effectiveTime);

        return mapped;
    }

    private Procedure mapProcedure(org.hl7.fhir.r4.model.Procedure procedure) {
        Procedure mapped = CDAFactory.eINSTANCE.createProcedure();
        mapped.setClassCode(ActClass.PROC);
        mapped.setMoodCode(x_DocumentProcedureMood.EVN);

        CD code = DatatypesFactory.eINSTANCE.createCD();
        code.setCode(procedure.getCode().getCodingFirstRep().getCode());
        code.setCodeSystem(procedure.getCode().getCodingFirstRep().getSystem());
        code.setCodeSystemName("SNOMED-CT");
        code.setDisplayName(procedure.getCode().getCodingFirstRep().getDisplay());
        mapped.setCode(code);

        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        effectiveTime.setValue(procedure.getPerformedDateTimeType().getValueAsString());
        mapped.setEffectiveTime(effectiveTime);

        return mapped;
    }

    private ObservationMedia mapImagingStudy(ImagingStudy study) {
        Observation reference = CDAFactory.eINSTANCE.createObservation();
        reference.setClassCode(ActClassObservation.OBS);
        reference.setMoodCode(x_ActMoodDocumentObservation.EVN);

        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        effectiveTime.setValue(datetimeFmt.format(study.getStarted()));
        reference.setEffectiveTime(effectiveTime);

        ObservationMedia media = CDAFactory.eINSTANCE.createObservationMedia();
        ED value = DatatypesFactory.eINSTANCE.createED();
        value.setMediaType("application/dicom");
        value.addText(dicomService.getBase64Binary(study));
        media.setValue(value);
        media.addObservation(reference);

        return media;
    }

    private void addAllObservations(org.hl7.fhir.r4.model.Encounter encounter, Section section) {
        String id = "Encounter/" + encounter.getIdElement().getIdPart();
        List<org.hl7.fhir.r4.model.Observation> obsList = fhirService.getClient()
            .search()
            .forResource(org.hl7.fhir.r4.model.Observation.class)
            .where(org.hl7.fhir.r4.model.Observation.ENCOUNTER.hasId(id))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute()
            .getEntry()
            .stream()
            .map(e -> (org.hl7.fhir.r4.model.Observation) e.getResource())
            .toList();

        obsList.forEach(obs ->
            section.addObservation(mapObservation(obs))
        );
    }

    private void addAllConditions(org.hl7.fhir.r4.model.Encounter encounter, Section section) {
        List<Condition> condList = fhirService.getClient()
            .search()
            .forResource(Condition.class)
            .where(Condition.ENCOUNTER.hasId("Encounter/" + encounter.getIdElement().getIdPart()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute()
            .getEntry()
            .stream()
            .map(e -> (Condition) e.getResource())
            .toList();

        condList.forEach(cond ->
            section.addObservation(mapCondition(cond))
        );
    }

    private void addAllPrescription(org.hl7.fhir.r4.model.Encounter encounter, Section section) {
        List<MedicationRequest> mstList = fhirService.getClient()
            .search()
            .forResource(MedicationRequest.class)
            .where(MedicationRequest.ENCOUNTER.hasId("Encounter/" + encounter.getIdElement().getIdPart()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute()
            .getEntry()
            .stream()
            .map(e -> (MedicationRequest) e.getResource())
            .toList();

        mstList.forEach(mst ->
            section.addSubstanceAdministration(mapPrescription(mst))
        );
    }

    private void addAllDeviceRequests(org.hl7.fhir.r4.model.Encounter encounter, Section section) {
        List<DeviceRequest> reqList = fhirService.getClient()
            .search()
            .forResource(DeviceRequest.class)
            .where(DeviceRequest.ENCOUNTER.hasId("Encounter/" + encounter.getIdElement().getIdPart()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute()
            .getEntry()
            .stream()
            .map(e -> (DeviceRequest) e.getResource())
            .toList();

        reqList.forEach(req ->
            section.addSubstanceAdministration(mapDeviceRequest(req))
        );
    }

    private void addAllProcedures(org.hl7.fhir.r4.model.Encounter encounter, Section section) {
        List<org.hl7.fhir.r4.model.Procedure> procList = fhirService.getClient()
            .search()
            .forResource(org.hl7.fhir.r4.model.Procedure.class)
            .where(org.hl7.fhir.r4.model.Procedure.ENCOUNTER.hasId("Encounter/" + encounter.getIdElement().getIdPart()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute()
            .getEntry()
            .stream()
            .map(e -> (org.hl7.fhir.r4.model.Procedure) e.getResource())
            .toList();

        procList.forEach(proc ->
            section.addProcedure(mapProcedure(proc))
        );
    }

    private void addAllImagingStudies(org.hl7.fhir.r4.model.Encounter encounter, Section section) {
        List<ImagingStudy> studyList = fhirService.getClient()
            .search()
            .forResource(ImagingStudy.class)
            .where(ImagingStudy.ENCOUNTER.hasId("Encounter/" + encounter.getIdElement().getIdPart()))
            .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
            .execute()
            .getEntry()
            .stream()
            .map(e -> (ImagingStudy) e.getResource())
            .toList();

        studyList.forEach(study ->
            section.addObservationMedia(mapImagingStudy(study))
        );
    }

    public ClinicalDocument getClinicalDocument(String encounterId) {
        datetimeFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        org.hl7.fhir.r4.model.Encounter encounter = (org.hl7.fhir.r4.model.Encounter) fhirService.resolveId(
            org.hl7.fhir.r4.model.Encounter.class,
            encounterId
        );

        org.hl7.fhir.r4.model.Patient patient = (org.hl7.fhir.r4.model.Patient) fhirService.resolveId(
            org.hl7.fhir.r4.model.Patient.class,
            encounter.getSubject().getReference()
        );

        ClinicalDocument clinicalDocument = CDAFactory.eINSTANCE.createClinicalDocument();
        clinicalDocument.getRealmCodes().add(DatatypesFactory.eINSTANCE.createCS("US"));

        TS effectiveTime = DatatypesFactory.eINSTANCE.createTS();
        effectiveTime.setValue(datetimeFmt.format(new Date()));
        clinicalDocument.setEffectiveTime(effectiveTime);

        RecordTarget recordTarget = CDAFactory.eINSTANCE.createRecordTarget();
        recordTarget.getRealmCodes().add(DatatypesFactory.eINSTANCE.createCS("US"));
        PatientRole patientRole = CDAFactory.eINSTANCE.createPatientRole();
        II patientId = DatatypesFactory.eINSTANCE.createII();
        patientId.setRoot(patient.getIdElement().getIdPart());
        patientRole.getIds().add(patientId);
        patientRole.setPatient(mapPatient(patient));
        recordTarget.setPatientRole(patientRole);
        clinicalDocument.getRecordTargets().add(recordTarget);

        Section section = CDAFactory.eINSTANCE.createSection();
        section.addEncounter(mapEncounter(encounter));
        addAllPrescription(encounter, section);
        addAllDeviceRequests(encounter, section);
        addAllObservations(encounter, section);
        addAllConditions(encounter, section);
        addAllProcedures(encounter, section);
        addAllImagingStudies(encounter, section);
        clinicalDocument.addSection(section);

        return clinicalDocument;
    }

    @SneakyThrows
    public String generateCD(String encounterId) {
        StringWriter wrt = new StringWriter();
        CDAUtil.save(getClinicalDocument(encounterId), wrt);
        return wrt.toString();
    }
}
