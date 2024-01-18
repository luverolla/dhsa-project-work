package dhsa.project.controller;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import dhsa.project.cda.CDAImporter;
import dhsa.project.fhir.FhirService;
import org.hl7.fhir.r4.model.DocumentReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/cda")
public class CDAController {

    private final Logger logger = Logger.getLogger(CDAController.class.getName());

    @Autowired
    private CDAImporter cdaImporter;

    @Autowired
    private FhirService fhirService;

    @GetMapping(value = "/encounter/{encounterId}", produces = "application/xml")
    public ResponseEntity<String> generateCDA(@PathVariable("encounterId") String encId) {
        try {
            DocumentReference cda = fhirService.getClient()
                .read().resource(DocumentReference.class)
                .withId(encId).execute();
            return ResponseEntity.ok(new String(cda.getContentFirstRep().getAttachment().getData()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.ok(cdaImporter.saveCDAToFhir(encId));
        }
    }
}
