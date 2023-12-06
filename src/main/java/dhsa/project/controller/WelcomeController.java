package dhsa.project.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import dhsa.project.dataset.AllergiesLoader;
import dhsa.project.dataset.DatasetLoader;
import dhsa.project.service.FhirService;
import org.checkerframework.checker.units.qual.A;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Luigi Verolla
 */

@Controller
public class WelcomeController {

	@Autowired
	private FhirService fs;

	// Inizio metodi di test
	@GetMapping("/")
	String home() {
		return "home";
	}

	@GetMapping("/patients/{id}")
	String patientPage(@PathVariable("id") String id, Model model) {
		Patient p = fs.getPatientById(id);
		model.addAttribute("patient", p);
		return "patient";
	}
	
	@GetMapping("/welcome")
	String welcome(Model model) {
		DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		model.addAttribute("serverTime", LocalDateTime.now().format(df));
		return "welcome";
	}
	// fine metodi di test
}
