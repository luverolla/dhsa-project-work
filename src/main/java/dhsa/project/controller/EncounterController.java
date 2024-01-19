package dhsa.project.controller;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import dhsa.project.filter.*;
import dhsa.project.view.EncounterView;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Controller
@RequestMapping("/web")
public class EncounterController {

    @Autowired
    private Commons commons;

    @GetMapping("/encounter/{id}")
    public String encounterFile(@PathVariable("id") String id, @RequestParam(required = false) String referral, Model model) {
        EncounterView em = commons.adapterService.getView((Encounter) commons.fhirService.resolveId(Encounter.class, id));

        PrescriptionFilter presFilter = new PrescriptionFilter();
        presFilter.setEncounter(id);
        ObservationFilter obsFilter = new ObservationFilter();
        obsFilter.setEncounter(id);
        ConditionFilter condFilter = new ConditionFilter();
        condFilter.setEncounter(id);
        ImagingStudyFilter isFilter = new ImagingStudyFilter();
        isFilter.setEncounter(id);
        ProcedureFilter procFilter = new ProcedureFilter();
        procFilter.setEncounter(id);
        ImmunizationFilter immFilter = new ImmunizationFilter();
        immFilter.setEncounter(id);
        DeviceRequestFilter devFilter = new DeviceRequestFilter();
        devFilter.setEncounter(id);

        try {
            model.addAttribute("encounter", em);
            model.addAttribute("devices", commons.searchService.retrieve(devFilter, 1));
            model.addAttribute("prescriptions", commons.searchService.retrieve(presFilter, 1));
            model.addAttribute("observations", commons.searchService.retrieve(obsFilter, 1));
            model.addAttribute("conditions", commons.searchService.retrieve(condFilter, 1));
            model.addAttribute("imagingStudies", commons.searchService.retrieve(isFilter, 1));
            model.addAttribute("procedures", commons.searchService.retrieve(procFilter, 1));
            model.addAttribute("immunizations", commons.searchService.retrieve(immFilter, 1));
            model.addAttribute("referral", referral);
            return "encounter-file";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encounter with Id " + id, e);
        }
    }

    @GetMapping("/encounter/{id}/conditions")
    public String encounterConditions(@PathVariable("id") String id, @ModelAttribute ConditionFilter cse, @RequestParam Map<String, String> params, Model model) {
        return encounterConditions(id, 1, cse, params, model);
    }


    @GetMapping("/encounter/{id}/conditions/{page}")
    public String encounterConditions(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ConditionFilter cse, @RequestParam Map<String, String> params, Model model) {
        try {
            EncounterView enc = commons.adapterService.getView((Encounter) commons.fhirService.resolveId(Encounter.class, id));
            cse.setEncounter(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("conditionSearch", cse);
            model.addAttribute("id", id);
            model.addAttribute("referral", "encounter");
            model.addAttribute("encounter", enc);
            model.addAttribute("conditions", commons.searchService.retrieve(cse, page));
            model.addAttribute("page", page);
            return "conditions-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encounter with Id " + id, e);
        }
    }

    @GetMapping("/encounter/{id}/imaging-studies")
    public String encounterImagingStudies(@PathVariable("id") String id, @ModelAttribute ImagingStudyFilter imf, @RequestParam Map<String, String> params, Model model) {
        return encounterImagingStudies(id, 1, imf, params, model);
    }

    @GetMapping("/encounter/{id}/imaging-studies/{page}")
    public String encounterImagingStudies(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ImagingStudyFilter imf, @RequestParam Map<String, String> params, Model model) {
        try {
            EncounterView enc = commons.adapterService.getView((Encounter) commons.fhirService.resolveId(Encounter.class, id));
            imf.setEncounter(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("imagingStudySearch", imf);
            model.addAttribute("encounter", enc);
            model.addAttribute("id", id);
            model.addAttribute("referral", "encounter");
            model.addAttribute("imagingStudies", commons.searchService.retrieve(imf, page));
            model.addAttribute("page", page);
            return "imaging-studies-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encounter with Id " + id, e);
        }
    }

    @GetMapping("/encounter/{id}/observations")
    public String encounterObservations(@PathVariable("id") String id, @ModelAttribute ObservationFilter ose, @RequestParam Map<String, String> params, Model model) {
        return encounterObservations(id, 1, ose, params, model);
    }

    @GetMapping("/encounter/{id}/observations/{page}")
    public String encounterObservations(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ObservationFilter ose, @RequestParam Map<String, String> params, Model model) {
        try {
            EncounterView pm = commons.adapterService.getView((Encounter) commons.fhirService.resolveId(Encounter.class, id));
            ose.setEncounter(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("observationSearch", ose);
            model.addAttribute("id", id);
            model.addAttribute("referral", "encounter");
            model.addAttribute("encounter", pm);
            model.addAttribute("observations", commons.searchService.retrieve(ose, page));
            model.addAttribute("page", page);
            return "observations-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encounter with Id " + id, e);
        }
    }

    @GetMapping("/encounter/{id}/immunizations")
    public String encounterImmunizations(@PathVariable("id") String id, @ModelAttribute ImmunizationFilter ise, @RequestParam Map<String, String> params, Model model) {
        return encounterImmunizations(id, 1, ise, params, model);
    }

    @GetMapping("/encounter/{id}/immunizations/{page}")
    public String encounterImmunizations(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ImmunizationFilter ose, @RequestParam Map<String, String> params, Model model) {
        try {
            EncounterView pm = commons.adapterService.getView((Encounter) commons.fhirService.resolveId(Encounter.class, id));
            ose.setEncounter(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("immunizationSearch", ose);
            model.addAttribute("id", id);
            model.addAttribute("referral", "encounter");
            model.addAttribute("encounter", pm);
            model.addAttribute("immunizations", commons.searchService.retrieve(ose, page));
            model.addAttribute("page", page);
            return "immunizations-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encounter with Id " + id, e);
        }
    }

    @GetMapping("/encounter/{id}/prescriptions")
    public String encounterPrescriptions(@PathVariable("id") String id, @ModelAttribute PrescriptionFilter pse, @RequestParam Map<String, String> params, Model model) {
        return encounterPrescriptions(id, 1, pse, params, model);
    }

    @GetMapping("/encounter/{id}/prescriptions/{page}")
    public String encounterPrescriptions(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute PrescriptionFilter pse, @RequestParam Map<String, String> params, Model model) {
        try {
            EncounterView pm = commons.adapterService.getView((Encounter) commons.fhirService.resolveId(Encounter.class, id));
            pse.setEncounter(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("prescriptionSearch", pse);
            model.addAttribute("id", id);
            model.addAttribute("referral", "encounter");
            model.addAttribute("encounter", pm);
            model.addAttribute("prescriptions", commons.searchService.retrieve(pse, page));
            model.addAttribute("page", page);
            return "prescriptions-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encounter with Id " + id, e);
        }
    }

    @GetMapping("/encounter/{id}/devices")
    public String encounterDevices(@PathVariable("id") String id, @ModelAttribute DeviceRequestFilter pse, @RequestParam Map<String, String> params, Model model) {
        return encounterDevices(id, 1, pse, params, model);
    }

    @GetMapping("/encounter/{id}/devices/{page}")
    public String encounterDevices(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute DeviceRequestFilter pse, @RequestParam Map<String, String> params, Model model) {
        try {
            EncounterView pm = commons.adapterService.getView((Encounter) commons.fhirService.resolveId(Encounter.class, id));
            pse.setEncounter(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("deviceSearch", pse);
            model.addAttribute("id", id);
            model.addAttribute("referral", "encounter");
            model.addAttribute("encounter", pm);
            model.addAttribute("devices", commons.searchService.retrieve(pse, page));
            model.addAttribute("page", page);
            return "devices-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encounter with Id " + id, e);
        }
    }

    @GetMapping("/encounter/{id}/procedures")
    public String encounterProcedures(@PathVariable("id") String id, @ModelAttribute ProcedureFilter pse, @RequestParam Map<String, String> params, Model model) {
        return encounterProcedures(id, 1, pse, params, model);
    }

    @GetMapping("/encounter/{id}/procedures/{page}")
    public String encounterProcedures(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ProcedureFilter pse, @RequestParam Map<String, String> params, Model model) {
        try {
            EncounterView pm = commons.adapterService.getView((Encounter) commons.fhirService.resolveId(Encounter.class, id));
            pse.setEncounter(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("procedureSearch", pse);
            model.addAttribute("id", id);
            model.addAttribute("referral", "encounter");
            model.addAttribute("encounter", pm);
            model.addAttribute("procedures", commons.searchService.retrieve(pse, page));
            model.addAttribute("page", page);
            return "procedures-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encounter with Id " + id, e);
        }
    }

    @GetMapping("/encounter/{id}/cda")
    public String encounterCDA(@PathVariable("id") String id) {
        commons.cdaImporter.saveCDAToFhir(id);
        return "redirect:/web/encounter/" + id;
    }
}
