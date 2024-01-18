package dhsa.project.bridge;

import dhsa.project.view.ViewService;
import dhsa.project.view.ConditionView;
import org.hl7.fhir.r4.model.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConditionBridge implements ResourceBridge<Condition, ConditionView> {

    @Autowired
    private ViewService viewService;
    public ConditionView transform(Condition res) {
        return new ConditionView(
            viewService.getPatientNR(res.getSubject()),
            viewService.getEncounterNR(res.getEncounter()),
            viewService.getCodingRef(res.getCode()),
            viewService.getDateString(res.getOnsetDateTimeType().getValue(), "Ongoing"),
            viewService.getDateString(res.getAbatementDateTimeType().getValue(), "Ongoing")
        );
    }
}
