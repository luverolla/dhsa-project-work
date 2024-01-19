package dhsa.project.controller;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/web")
public class ObservationController {

    @Autowired
    private Commons commons;

    @GetMapping("/ecg/{id}")
    public String graphPage(@PathVariable("id") String observationId, Model model) {
        try {
            Observation obs = (Observation) commons.fhirService.resolveId(Observation.class, observationId);
            if (!obs.getCode().getCodingFirstRep().getSystem().equals("http://snomed.info/sct"))
                throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Observation is not an electrocardiogram"
                );

            String value = ((StringType) obs.getValue()).getValue();
            List<Float> data = Arrays.stream(value.split(" "))
                .map(Float::parseFloat)
                .toList();
            model.addAttribute("data", data);
            return "ecg-result";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "Observation not found"
            );
        }
    }
}
