package dhsa.project.bridge;

import dhsa.project.view.ViewService;
import dhsa.project.view.ObservationView;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObservationBridge implements ResourceBridge<Observation, ObservationView> {

    @Autowired
    private ViewService viewService;

    public ObservationView transform(Observation obs) {
        String value = switch (obs.getValue().fhirType()) {
            case "Quantity" -> {
                Quantity qnt = (Quantity) obs.getValue();
                String unit = qnt.getUnit().matches("\\{(.*)}") ? "" : qnt.getUnit();
                yield qnt.getValue().toString() + " " + unit;
            }
            case "IntegerType" -> ((IntegerType) obs.getValue()).getValue().toString();
            default -> ((StringType) obs.getValue()).getValue();
        };
        return new ObservationView(
            viewService.getCodingRef(obs.getCode()),
            viewService.getEncounterNR(obs.getEncounter()).getUri(),
            viewService.getDateTimeString(obs.getEffectiveDateTimeType().getValue()),
            value
        );
    }
}
