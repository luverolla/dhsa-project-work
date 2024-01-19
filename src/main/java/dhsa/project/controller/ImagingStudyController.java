package dhsa.project.controller;

import dhsa.project.adapter.AdapterService;
import dhsa.project.fhir.FhirService;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class ImagingStudyController {

    @Autowired
    private AdapterService adapterService;
    @Autowired
    private FhirService fhirService;

    @GetMapping("/imaging-study/{id}")
    public String imagingStudyFile(@PathVariable("id") String id, Model model) {
        model.addAttribute("imagingStudy",
            adapterService.getView((ImagingStudy) fhirService.resolveId(ImagingStudy.class, id))
        );
        return "imaging-study-file";
    }
}
