package dhsa.project.controller;

import dhsa.project.data.USStatesMap;
import dhsa.project.filter.EncounterFilter;
import dhsa.project.filter.OrganizationFilter;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/web")
public class OrganizationController {

    @Autowired
    private Commons commons;

    @GetMapping("/organizations")
    public String organizations(@ModelAttribute OrganizationFilter ose, @RequestParam Map<String, String> params, Model model) {
        return organizations(1, ose, params, model);
    }

    @GetMapping("/organizations/{page}")
    public String organizations(@PathVariable("page") int page, @ModelAttribute OrganizationFilter ose, @RequestParam Map<String, String> params, Model model) {
        model.addAttribute("paramString", commons.getParamString(params));
        model.addAttribute("orgSearch", ose);
        model.addAttribute("page", page);
        model.addAttribute("usStates", USStatesMap.get());
        model.addAttribute("organizations", commons.searchService.retrieve(ose, page));
        return "organization-list";
    }

    @GetMapping("/organization/{id}")
    public String organizationFile(@PathVariable("id") String id, Model model) {
        EncounterFilter ef = new EncounterFilter();
        ef.setProvider(id);
        Organization organization = (Organization) commons.fhirService.resolveId(Organization.class, id);
        model.addAttribute("id", id);
        model.addAttribute("encounterSearch", ef);
        model.addAttribute("encounters", commons.searchService.retrieve(ef, 1));
        model.addAttribute("organization", commons.adapterService.getView(organization));
        return "organization-file";
    }

    @GetMapping("/organization/{id}/encounters")
    public String organizationEncounters(@PathVariable("id") String id, @ModelAttribute EncounterFilter ef, Model model, @RequestParam Map<String, String> params) {
        return organizationEncounters(id, 1, ef, model, params);
    }

    @GetMapping("/organization/{id}/encounters/{page}")
    public String organizationEncounters(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute EncounterFilter ef, Model model, @RequestParam Map<String, String> params) {
        ef.setProvider(id);
        Organization organization = (Organization) commons.fhirService.resolveId(Organization.class, id);
        model.addAttribute("organization", organization);
        model.addAttribute("page", page);
        model.addAttribute("id", id);
        model.addAttribute("referral", "organization");
        model.addAttribute("encounterSearch", ef);
        model.addAttribute("paramString", commons.getParamString(params));
        model.addAttribute("encounters", commons.searchService.retrieve(ef, page));
        return "encounters-list";
    }
}
