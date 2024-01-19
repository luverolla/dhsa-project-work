package dhsa.project.controller;

import dhsa.project.filter.EncounterFilter;
import dhsa.project.filter.PractitionerFilter;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/web")
public class PractitionerController {

    @Autowired
    private Commons commons;

    @GetMapping("/practitioners")
    public String practitioners(@ModelAttribute PractitionerFilter pse, @RequestParam Map<String, String> params, Model model) {
        return practitioners(1, pse, params, model);
    }

    @GetMapping("/practitioners/{page}")
    public String practitioners(@PathVariable("page") int page, @ModelAttribute PractitionerFilter pse, @RequestParam Map<String, String> params, Model model) {
        model.addAttribute("paramString", commons.getParamString(params));
        model.addAttribute("practSearch", pse);
        model.addAttribute("page", page);
        model.addAttribute("practitioners", commons.searchService.retrieve(pse, page));
        return "practitioner-list";
    }

    @GetMapping("/practitioner/{id}")
    public String practitionerFile(@PathVariable("id") String id, Model model) {
        EncounterFilter ef = new EncounterFilter();
        ef.setPractitioner(id);
        Practitioner practitioner = (Practitioner) commons.fhirService.resolveId(Practitioner.class, id);
        model.addAttribute("id", id);
        model.addAttribute("encounters", commons.searchService.retrieve(ef, 1));
        model.addAttribute("practitioner", commons.adapterService.getView(practitioner));
        return "practitioner-file";
    }

    @GetMapping("/practitioner/{id}/encounters")
    public String practitionerEncounters(@PathVariable("id") String id, @ModelAttribute EncounterFilter ef, Model model, @RequestParam Map<String, String> params) {
        return practitionerEncounters(id, 1, ef, model, params);
    }

    @GetMapping("/practitioner/{id}/encounters/{page}")
    public String practitionerEncounters(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute EncounterFilter ef, Model model, @RequestParam Map<String, String> params) {
        ef.setPractitioner(id);
        Practitioner practitioner = (Practitioner) commons.fhirService.resolveId(Practitioner.class, id);
        model.addAttribute("practitioner", commons.adapterService.getView(practitioner));
        model.addAttribute("page", page);
        model.addAttribute("id", id);
        model.addAttribute("referral", "practitioner");
        model.addAttribute("encounterSearch", ef);
        model.addAttribute("paramString", commons.getParamString(params));
        model.addAttribute("encounters", commons.searchService.retrieve(ef, page));
        return "encounters-list";
    }
}
