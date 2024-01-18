package dhsa.project.controller;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import dhsa.project.bridge.BridgeService;
import dhsa.project.cda.CDAImporter;
import dhsa.project.data.USStatesMap;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.*;
import dhsa.project.search.*;
import dhsa.project.view.EncounterView;
import dhsa.project.view.PatientView;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/web")
public class UIController {

    private final Logger logger = Logger.getLogger("UIController");

    @Autowired
    private SearchService searchService;
    @Autowired
    private BridgeService bridgeService;
    @Autowired
    private FhirService fhirService;
    @Autowired
    private CDAImporter cdaImporter;

    private String getParamString(Map<String, String> params) {
        StringBuilder paramString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramString.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return paramString.toString();
    }

    @GetMapping("/")
    public String medicalDashboard(Model model) {
        model.addAttribute("patients", searchService.retrieve(new PatientFilter(), 1));
        return "home";
    }

    @GetMapping("/patients/{page}")
    public String patients(@PathVariable("page") int page, @ModelAttribute PatientFilter pse, @RequestParam Map<String, String> params, Model model) {
        model.addAttribute("paramString", getParamString(params));
        model.addAttribute("patientSearch", pse);
        model.addAttribute("page", page);
        model.addAttribute("patients", searchService.retrieve(pse, page));
        return "patient-list";
    }

    @GetMapping("/patients")
    public String patients(@ModelAttribute PatientFilter pse, @RequestParam Map<String, String> params, Model model) {
        return patients(1, pse, params, model);
    }

    @GetMapping("/patient/{id}")
    public String patientFile(@PathVariable("id") String id, Model model) {
        try {
            Patient patient = (Patient) fhirService.resolveId(Patient.class, id);

            EncounterFilter encFilter = new EncounterFilter();
            encFilter.setPatient(id);
            ObservationFilter obsFilter = new ObservationFilter();
            obsFilter.setPatient(id);
            ConditionFilter condFilter = new ConditionFilter();
            condFilter.setPatient(id);
            ImagingStudyFilter isFilter = new ImagingStudyFilter();
            isFilter.setPatient(id);

            model.addAttribute("patient", bridgeService.getView(patient));
            model.addAttribute("encounters", searchService.retrieve(encFilter, 1));
            model.addAttribute("observations", searchService.retrieve(obsFilter, 1));
            model.addAttribute("conditions", searchService.retrieve(condFilter, 1));
            model.addAttribute("imagingStudies", searchService.retrieve(isFilter, 1));
            return "patient-file";
        } catch (ResourceNotFoundException e) {
            logger.severe("No patient with Id " + id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/patient/{id}/encounters")
    public String patientEncounters(@PathVariable("id") String id, @ModelAttribute EncounterFilter ese, @RequestParam Map<String, String> params, Model model) {
        return patientEncounters(id, 1, ese, params, model);
    }

    @GetMapping("/patient/{id}/conditions")
    public String patientConditions(@PathVariable("id") String id, @ModelAttribute ConditionFilter cse, @RequestParam Map<String, String> params, Model model) {
        return patientConditions(id, 1, cse, params, model);
    }

    @GetMapping("/patient/{id}/conditions/{page}")
    public String patientConditions(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ConditionFilter cse, @RequestParam Map<String, String> params, Model model) {
        try {
            PatientView pm = bridgeService.getView((Patient) fhirService.resolveId(Patient.class, id));
            ConditionFilter condFilter = new ConditionFilter();
            condFilter.setPatient(id);

            model.addAttribute("paramString", getParamString(params));
            model.addAttribute("conditionSearch", cse);
            model.addAttribute("patientId", id);
            model.addAttribute("patient", pm);
            model.addAttribute("conditions", searchService.retrieve(condFilter, page));
            model.addAttribute("page", page);
            return "patient-conditions";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/patient/{id}/imaging-studies")
    public String patientImagingStudies(@PathVariable("id") String id, @ModelAttribute ImagingStudyFilter isse, @RequestParam Map<String, String> params, Model model) {
        return patientImagingStudies(id, 1, isse, params, model);
    }

    @GetMapping("/patient/{id}/imaging-studies/{page}")
    public String patientImagingStudies(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute ImagingStudyFilter isse, @RequestParam Map<String, String> params, Model model) {
        try {
            PatientView pm = bridgeService.getView((Patient) fhirService.resolveId(Patient.class, id));
            ImagingStudyFilter isFilter = new ImagingStudyFilter();
            isFilter.setPatient(id);

            model.addAttribute("paramString", getParamString(params));
            model.addAttribute("imagingStudySearch", isse);
            model.addAttribute("patientId", id);
            model.addAttribute("patient", pm);
            model.addAttribute("imagingStudies", searchService.retrieve(isFilter, page));
            model.addAttribute("page", page);
            return "patient-imaging-studies";
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
            PatientView pm = bridgeService.getView((Patient) fhirService.resolveId(Patient.class, id));
            ObservationFilter obsFilter = new ObservationFilter();
            obsFilter.setPatient(id);

            model.addAttribute("paramString", getParamString(params));
            model.addAttribute("observationSearch", ose);
            model.addAttribute("patientId", id);
            model.addAttribute("patient", pm);
            model.addAttribute("observations", searchService.retrieve(obsFilter, page));
            model.addAttribute("page", page);
            return "patient-observations";
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
            PatientView pm = bridgeService.getView((Patient) fhirService.resolveId(Patient.class, id));
            ImmunizationFilter immFilter = new ImmunizationFilter();
            immFilter.setPatient(id);

            model.addAttribute("paramString", getParamString(params));
            model.addAttribute("immunizationSearch", ose);
            model.addAttribute("patientId", id);
            model.addAttribute("patient", pm);
            model.addAttribute("immunizations", searchService.retrieve(immFilter, page));
            model.addAttribute("page", page);
            return "patient-immunizations";
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
            PatientView pm = bridgeService.getView((Patient) fhirService.resolveId(Patient.class, id));
            PrescriptionFilter presFilter = new PrescriptionFilter();
            presFilter.setPatient(id);

            model.addAttribute("paramString", getParamString(params));
            model.addAttribute("prescriptionSearch", pse);
            model.addAttribute("patientId", id);
            model.addAttribute("patient", pm);
            model.addAttribute("prescriptions", searchService.retrieve(presFilter, page));
            model.addAttribute("page", page);
            return "patient-prescriptions";
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
            PatientView pm = bridgeService.getView((Patient) fhirService.resolveId(Patient.class, id));
            DeviceRequestFilter devFilter = new DeviceRequestFilter();
            devFilter.setPatient(id);

            model.addAttribute("paramString", getParamString(params));
            model.addAttribute("deviceSearch", pse);
            model.addAttribute("patientId", id);
            model.addAttribute("patient", pm);
            model.addAttribute("devices", searchService.retrieve(devFilter, page));
            model.addAttribute("page", page);
            return "patient-devices";
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
            PatientView pm = bridgeService.getView((Patient) fhirService.resolveId(Patient.class, id));
            ProcedureFilter procFilter = new ProcedureFilter();
            procFilter.setPatient(id);

            model.addAttribute("paramString", getParamString(params));
            model.addAttribute("procedureSearch", pse);
            model.addAttribute("patientId", id);
            model.addAttribute("patient", pm);
            model.addAttribute("procedures", searchService.retrieve(procFilter, page));
            model.addAttribute("page", page);
            return "patient-procedures";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/patient/{id}/encounters/{page}")
    public String patientEncounters(@PathVariable("id") String id, @PathVariable("page") int page, @ModelAttribute EncounterFilter ese, @RequestParam Map<String, String> params, Model model) {
        try {
            PatientView pm = bridgeService.getView((Patient) fhirService.resolveId(Patient.class, id));
            EncounterFilter encFilter = new EncounterFilter();
            encFilter.setPatient(id);

            model.addAttribute("paramString", getParamString(params));
            model.addAttribute("encounterSearch", ese);
            model.addAttribute("patientId", id);
            model.addAttribute("patient", pm);
            model.addAttribute("encounters", searchService.retrieve(encFilter, page));
            model.addAttribute("page", page);
            return "encounter-list";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No patient with Id " + id, e);
        }
    }

    @GetMapping("/encounter/{id}")
    public String encounterFile(@PathVariable("id") String id, @RequestParam(required = false) String referral, Model model) {
        EncounterView em = bridgeService.getView((Encounter) fhirService.resolveId(Encounter.class, id));

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

        try {
            model.addAttribute("encounter", em);
            model.addAttribute("prescriptions", searchService.retrieve(presFilter, 1));
            model.addAttribute("observations", searchService.retrieve(obsFilter, 1));
            model.addAttribute("conditions", searchService.retrieve(condFilter, 1));
            model.addAttribute("imagingStudies", searchService.retrieve(isFilter, 1));
            model.addAttribute("procedures", searchService.retrieve(procFilter, 1));
            model.addAttribute("immunizations", searchService.retrieve(immFilter, 1));
            model.addAttribute("referral", referral);
            return "encounter-file";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encounter with Id " + id, e);
        }
    }

    @GetMapping("/practitioners")
    public String practitioners(@ModelAttribute PractitionerFilter pse, @RequestParam Map<String, String> params, Model model) {
        return practitioners(1, pse, params, model);
    }

    @GetMapping("/practitioners/{page}")
    public String practitioners(@PathVariable("page") int page, @ModelAttribute PractitionerFilter pse, @RequestParam Map<String, String> params, Model model) {
        model.addAttribute("paramString", getParamString(params));
        model.addAttribute("practSearch", pse);
        model.addAttribute("page", page);
        model.addAttribute("practitioners", searchService.retrieve(pse, page));
        return "practitioner-list";
    }

    @GetMapping("/organizations")
    public String organizations(@ModelAttribute OrganizationFilter ose, @RequestParam Map<String, String> params, Model model) {
        return organizations(1, ose, params, model);
    }

    @GetMapping("/organizations/{page}")
    public String organizations(@PathVariable("page") int page, @ModelAttribute OrganizationFilter ose, @RequestParam Map<String, String> params, Model model) {
        model.addAttribute("paramString", getParamString(params));
        model.addAttribute("orgSearch", ose);
        model.addAttribute("page", page);
        model.addAttribute("usStates", USStatesMap.get());
        model.addAttribute("organizations", searchService.retrieve(ose, page));
        return "organization-list";
    }

    @GetMapping("/imaging-study/{id}")
    public String imagingStudyFile(@PathVariable("id") String id, Model model) {
        model.addAttribute("imagingStudy",
            bridgeService.getView((ImagingStudy) fhirService.resolveId(ImagingStudy.class, id))
        );
        return "imaging-study-file";
    }

    @GetMapping("/encounter/{id}/cda")
    public String encounterCDA(@PathVariable("id") String id) {
        cdaImporter.saveCDAToFhir(id);
        return "redirect:/web/encounter/" + id;
    }
}
