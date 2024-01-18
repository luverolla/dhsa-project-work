package dhsa.project.bridge;

import dhsa.project.view.ViewService;
import dhsa.project.view.ImmunizationView;
import org.hl7.fhir.r4.model.Immunization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImmunizationBridge implements ResourceBridge<Immunization, ImmunizationView> {

    @Autowired
    private ViewService viewService;

    public ImmunizationView transform(Immunization res) {
        return new ImmunizationView(
            viewService.getPatientNR(res.getPatient()),
            viewService.getEncounterNR(res.getEncounter()),
            viewService.getCodingRef(res.getVaccineCode()),
            viewService.getDateTimeString(res.getRecorded())
        );
    }
}
