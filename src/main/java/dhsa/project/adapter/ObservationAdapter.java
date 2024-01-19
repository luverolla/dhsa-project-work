package dhsa.project.adapter;

import dhsa.project.view.ObservationView;
import dhsa.project.view.ViewService;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObservationAdapter implements ResourceAdapter<Observation, ObservationView> {

    @Autowired
    private ViewService viewService;

    /**
     * Transform FHIR Observation to an ObservationView object
     * A particular case is the electrocardiograph procedure, which is present twice in the database for each entry:
     * <ul>
     * <li>one entry with the graph data on y-axis, represented as a string with space-separated values</li>
     * <li>one entry with the image encoded in Base64</li>
     * For rendering we used GraphJS engine on the data string, therefore the copy with encoded image is discarded
     * </ul>
     *
     * @param obs FHIR Observation resource
     * @return ObservationView object
     */
    public ObservationView transform(Observation obs) {
        boolean isBinary = false;
        String value = switch (obs.getValue().fhirType()) {
            case "Quantity" -> {
                Quantity qnt = (Quantity) obs.getValue();
                String unit = qnt.getUnit().matches("\\{(.*)}") ? "" : qnt.getUnit();
                yield qnt.getValue().toString() + " " + unit;
            }
            case "IntegerType" -> ((IntegerType) obs.getValue()).getValue().toString();
            default -> {
                String stringValue = ((StringType) obs.getValue()).getValue();
                if (obs.getCode().getCodingFirstRep().getSystem().equals("http://snomed.info/sct")) {
                    isBinary = true;
                    // electrocardiograph procedure
                    if (!stringValue.contains(" "))
                        // graph data, discard, we will use the images
                        yield "__DISCARD__";
                }
                // base64 image
                yield stringValue;
            }
        };
        return value.equals("__DISCARD__") ? null : new ObservationView(
            obs.getIdElement().getIdPart(),
            viewService.getCodingRef(obs.getCode()),
            viewService.getEncounterNR(obs.getEncounter()).getUri(),
            viewService.getDateTimeString(obs.getEffectiveDateTimeType().getValue()),
            value, isBinary
        );
    }
}
