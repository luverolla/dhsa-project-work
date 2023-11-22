/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dhsa.project.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Luigi Verolla
 */

@RestController
public class APIController {
	
	// Inizio metodi di test
	// Sono solo per vedere se il mapping REST funziona
	@GetMapping("/api")
	String sendWelcome() {
		return "Welcome to Client API!";
	}
	
	@GetMapping("/api/endpoint")
	String sendWelcomeEndpoint() {
		return "Welcome to Client API Endpoint!";
	}
	
	@GetMapping("/api/hello/{name}")
	String sendHelloTo(@PathVariable("name") String name) {
		try {
			String sanified = URLDecoder.decode(name, "UTF-8");
			Logger.getLogger(APIController.class.getName()).log(Level.INFO, sanified);
			return String.format("Hello %s!", sanified);
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(APIController.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "ERROR!";
	}
	// fine metodi di test
}
