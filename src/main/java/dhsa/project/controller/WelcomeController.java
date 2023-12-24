package dhsa.project.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
/**
 *
 * @author Luigi Verolla
 */

@Controller
public class WelcomeController {

	// Inizio metodi di test
	@GetMapping("/")
	String home() {
		return "home";
	}
	
	@GetMapping("/welcome")
	String welcome(Model model) {
		DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		model.addAttribute("serverTime", LocalDateTime.now().format(df));
		return "welcome";
	}
	// fine metodi di test
}
