package dhsa.project.cda;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import dhsa.project.fhir.FhirService;
import lombok.SneakyThrows;
import org.apache.commons.net.util.Base64;
import org.hl7.fhir.r4.model.*;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.Encounter;
import org.openhealthtools.mdht.uml.cda.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Class responsible for creating C-CDA document from encounter and importing them as FHIR DocumentReference resource.
 */
@Service
public class CDAImporter {
    private final SimpleDateFormat DATETIME_FMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Autowired
    private CDAService cdaService;

    @Autowired
    private FhirService fhirService;

    /**
     * Creates C-CDA document from encounter and imports it as FHIR DocumentReference resource.
     *
     * @param encounterId encounter ID
     * @return C-CDA document
     * @throws ResourceNotFoundException if encounter is not found
     */
    public String saveCDAToFhir(String encounterId) throws ResourceNotFoundException {
        DocumentReference doc = convert(cdaService.getClinicalDocument(encounterId));
        fhirService.getClient().update().resource(doc).execute();
        String base64 = doc.getContentFirstRep().getAttachment().getDataElement().getValueAsString();
        return new String(Base64.decodeBase64(base64));
    }

    /**
     * Converts ClinicalDocument to DocumentReference.
     * Takes, as input, a ClinicalDocument object produced by the MDHT library and converts it to a FHIR DocumentReference
     *
     * @param cda ClinicalDocument object
     * @return DocumentReference object
     */
    @SneakyThrows
    private DocumentReference convert(ClinicalDocument cda) {
        Section body = cda.getSections().get(0);
        Encounter encounter = body.getEncounters().get(0);
        String encounterId = encounter.getIds().get(0).getRoot();

        DocumentReference document = new DocumentReference();
        document.setId(encounterId);
        document.setStatus(Enumerations.DocumentReferenceStatus.CURRENT);
        document.setType(new CodeableConcept().addCoding(
            new Coding()
                .setSystem("http://loinc.org")
                .setCode("34133-9")
                .setDisplay("Summarization of episode note")
        ));

        document.setSubject(new Reference("Patient/" + cda.getPatients().get(0).getId().getRoot()));
        document.setContext(new DocumentReference.DocumentReferenceContextComponent()
            .setEncounter(List.of(new Reference("Encounter/" + encounterId)))
            .setPeriod(new Period()
                .setStartElement(new DateTimeType(encounter.getEffectiveTime().getLow().getValue()))
                .setEndElement(new DateTimeType(encounter.getEffectiveTime().getHigh().getValue()))
            )
        );
        document.setAuthor(cda.getParticipants().stream().map(author ->
            new Reference("Practitioner/" + author.getAssociatedEntity().getIds().get(0).getRoot())
        ).toList());
        document.setDate(DATETIME_FMT.parse(cda.getEffectiveTime().getValue()));
        document.setDescription("Report of Encounter " + encounterId);

        document.setContent(List.of(new DocumentReference.DocumentReferenceContentComponent()
                .setFormat(new Coding()
                    .setSystem("http://hl7.org/fhir/R4/valueset-formatcodes.html")
                    .setCode("urn:hl7-org:sdwg:ccda-structuredBody:2.1")
                    .setDisplay("C-CDA Structured Body")
                )
                .setAttachment(new Attachment()
                    .setContentType("application/xml")
                    .setLanguage("en")
                    .setData(cdaService.generateCD(encounterId).getBytes())
                )
            )
        );

        return document;
    }
}
