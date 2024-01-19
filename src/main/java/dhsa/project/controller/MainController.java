package dhsa.project.controller;

import dhsa.project.filter.PatientFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class MainController {

    @Autowired
    private Commons commons;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("patients", commons.searchService.retrieve(new PatientFilter(), 1));
        return "home";
    }
}
