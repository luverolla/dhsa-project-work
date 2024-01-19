package dhsa.project.controller;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import dhsa.project.filter.*;
import dhsa.project.view.PatientView;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Controller
@RequestMapping("/web")
public class PatientController {

    @Autowired
    private Commons commons;

    @GetMapping("/patients")
    public String patients(@ModelAttribute PatientFilter pse, @RequestParam Map<String, String> params, Model model) {
        return patients(1, pse, params, model);
    }

    @GetMapping("/patients/{page}")
    public String patients(@PathVariable("page") int page, @ModelAttribute PatientFilter pse, @RequestParam Map<String, String> params, Model model) {
        model.addAttribute("paramString", commons.getParamString(params));
        model.addAttribute("patientSearch", pse);
        model.addAttribute("page", page);
        model.addAttribute("patients", commons.searchService.retrieve(pse, page));
        return "patient-list";
    }

    @GetMapping("/patient/{id}")
    public String patientFile(@PathVariable("id") String id, Model model) {
        try {
            Patient patient = (Patient) commons.fhirService.resolveId(Patient.class, id);

            EncounterFilter encFilter = new EncounterFilter();
            encFilter.setPatient(id);
            ObservationFilter obsFilter = new ObservationFilter();
            obsFilter.setPatient(id);
            ConditionFilter condFilter = new ConditionFilter();
            condFilter.setPatient(id);
            ImagingStudyFilter isFilter = new ImagingStudyFilter();
            isFilter.setPatient(id);
            ProcedureFilter procFilter = new ProcedureFilter();
            procFilter.setPatient(id);
            ImmunizationFilter immFilter = new ImmunizationFilter();
            immFilter.setPatient(id);
            PrescriptionFilter presFilter = new PrescriptionFilter();
            presFilter.setPatient(id);
            DeviceRequestFilter devFilter = new DeviceRequestFilter();
            devFilter.setPatient(id);

            model.addAttribute("patient", commons.adapterService.getView(patient));
            model.addAttribute("prescriptions", commons.searchService.retrieve(presFilter, 1));
            model.addAttribute("devices", commons.searchService.retrieve(devFilter, 1));
            model.addAttribute("immunizations", commons.searchService.retrieve(immFilter, 1));
            model.addAttribute("procedures", commons.searchService.retrieve(procFilter, 1));
            model.addAttribute("encounters", commons.searchService.retrieve(encFilter, 1));
            model.addAttribute("observations", commons.searchService.retrieve(obsFilter, 1));
            model.addAttribute("conditions", commons.searchService.retrieve(condFilter, 1));
            model.addAttribute("imagingStudies", commons.searchService.retrieve(isFilter, 1));
            return "patient-file";
        } catch (ResourceNotFoundException e) {
            commons.logger.severe("No patient with Id " + id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/patient/{id}/encounters")
    public String patientEncounters(@PathVariable("id") String id, @ModelAttribute EncounterFilter ese, @RequestParam Map<String, String> params, Model model) {
        return patientEncounters(id, 1, ese, params, model);
    }

    @GetMapping("/patient/{id}/encounters/{page}")
    public String patientEncounters(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute EncounterFilter ese, @RequestParam Map<String, String> params, Model model) {
        try {
            PatientView pm = commons.adapterService.getView((Patient) commons.fhirService.resolveId(Patient.class, id));
            ese.setPatient(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("encounterSearch", ese);
            model.addAttribute("patientId", id);
            model.addAttribute("patient", pm);
            model.addAttribute("referral", "patient");
            model.addAttribute("encounters", commons.searchService.retrieve(ese, page));
            model.addAttribute("page", page);
            return "encounters-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/patient/{id}/conditions")
    public String patientConditions(@PathVariable("id") String id, @ModelAttribute ConditionFilter cse, @RequestParam Map<String, String> params, Model model) {
        return patientConditions(id, 1, cse, params, model);
    }

    @GetMapping("/patient/{id}/conditions/{page}")
    public String patientConditions(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ConditionFilter cse, @RequestParam Map<String, String> params, Model model) {
        try {
            PatientView pm = commons.adapterService.getView((Patient) commons.fhirService.resolveId(Patient.class, id));
            cse.setPatient(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("conditionSearch", cse);
            model.addAttribute("id", id);
            model.addAttribute("referral", "patient");
            model.addAttribute("patient", pm);
            model.addAttribute("conditions", commons.searchService.retrieve(cse, page));
            model.addAttribute("page", page);
            return "conditions-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/patient/{id}/imaging-studies")
    public String patientImagingStudies(@PathVariable("id") String id, @ModelAttribute ImagingStudyFilter isse, @RequestParam Map<String, String> params, Model model) {
        return patientImagingStudies(id, 1, isse, params, model);
    }

    @GetMapping("/patient/{id}/imaging-studies/{page}")
    public String patientImagingStudies(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ImagingStudyFilter imf, @RequestParam Map<String, String> params, Model model) {
        try {
            PatientView pm = commons.adapterService.getView((Patient) commons.fhirService.resolveId(Patient.class, id));
            imf.setPatient(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("imagingStudySearch", imf);
            model.addAttribute("id", id);
            model.addAttribute("referral", "patient");
            model.addAttribute("patient", pm);
            model.addAttribute("imagingStudies", commons.searchService.retrieve(imf, page));
            model.addAttribute("page", page);
            return "imaging-studies-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/patient/{id}/observations")
    public String patientObservations(@PathVariable("id") String id, @ModelAttribute ObservationFilter ose, @RequestParam Map<String, String> params, Model model) {
        return patientObservations(id, 1, ose, params, model);
    }

    @GetMapping("/patient/{id}/observations/{page}")
    public String patientObservations(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ObservationFilter ose, @RequestParam Map<String, String> params, Model model) {
        try {
            PatientView pm = commons.adapterService.getView((Patient) commons.fhirService.resolveId(Patient.class, id));
            ose.setPatient(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("observationSearch", ose);
            model.addAttribute("id", id);
            model.addAttribute("referral", "patient");
            model.addAttribute("patient", pm);
            model.addAttribute("observations", commons.searchService.retrieve(ose, page));
            model.addAttribute("page", page);
            return "observations-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/patient/{id}/immunizations")
    public String patientImmunizations(@PathVariable("id") String id, @ModelAttribute ImmunizationFilter ise, @RequestParam Map<String, String> params, Model model) {
        return patientImmunizations(id, 1, ise, params, model);
    }

    @GetMapping("/patient/{id}/immunizations/{page}")
    public String patientImmunizations(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ImmunizationFilter ose, @RequestParam Map<String, String> params, Model model) {
        try {
            PatientView pm = commons.adapterService.getView((Patient) commons.fhirService.resolveId(Patient.class, id));
            ose.setPatient(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("immunizationSearch", ose);
            model.addAttribute("id", id);
            model.addAttribute("referral", "patient");
            model.addAttribute("patient", pm);
            model.addAttribute("immunizations", commons.searchService.retrieve(ose, page));
            model.addAttribute("page", page);
            return "immunizations-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/patient/{id}/prescriptions")
    public String patientPrescriptions(@PathVariable("id") String id, @ModelAttribute PrescriptionFilter pse, @RequestParam Map<String, String> params, Model model) {
        return patientPrescriptions(id, 1, pse, params, model);
    }

    @GetMapping("/patient/{id}/prescriptions/{page}")
    public String patientPrescriptions(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute PrescriptionFilter pse, @RequestParam Map<String, String> params, Model model) {
        try {
            PatientView pm = commons.adapterService.getView((Patient) commons.fhirService.resolveId(Patient.class, id));
            pse.setPatient(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("prescriptionSearch", pse);
            model.addAttribute("id", id);
            model.addAttribute("referral", "patient");
            model.addAttribute("patient", pm);
            model.addAttribute("prescriptions", commons.searchService.retrieve(pse, page));
            model.addAttribute("page", page);
            return "prescriptions-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/patient/{id}/devices")
    public String patientDevices(@PathVariable("id") String id, @ModelAttribute DeviceRequestFilter pse, @RequestParam Map<String, String> params, Model model) {
        return patientDevices(id, 1, pse, params, model);
    }

    @GetMapping("/patient/{id}/devices/{page}")
    public String patientDevices(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute DeviceRequestFilter pse, @RequestParam Map<String, String> params, Model model) {
        try {
            PatientView pm = commons.adapterService.getView((Patient) commons.fhirService.resolveId(Patient.class, id));
            pse.setPatient(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("deviceSearch", pse);
            model.addAttribute("id", id);
            model.addAttribute("referral", "patient");
            model.addAttribute("patient", pm);
            model.addAttribute("devices", commons.searchService.retrieve(pse, page));
            model.addAttribute("page", page);
            return "devices-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/patient/{id}/procedures")
    public String patientProcedures(@PathVariable("id") String id, @ModelAttribute ProcedureFilter pse, @RequestParam Map<String, String> params, Model model) {
        return patientProcedures(id, 1, pse, params, model);
    }

    @GetMapping("/patient/{id}/procedures/{page}")
    public String patientProcedures(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ProcedureFilter pse, @RequestParam Map<String, String> params, Model model) {
        try {
            PatientView pm = commons.adapterService.getView((Patient) commons.fhirService.resolveId(Patient.class, id));
            pse.setPatient(id);

            model.addAttribute("paramString", commons.getParamString(params));
            model.addAttribute("procedureSearch", pse);
            model.addAttribute("id", id);
            model.addAttribute("referral", "patient");
            model.addAttribute("patient", pm);
            model.addAttribute("procedures", commons.searchService.retrieve(pse, page));
            model.addAttribute("page", page);
            return "procedures-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }
}
