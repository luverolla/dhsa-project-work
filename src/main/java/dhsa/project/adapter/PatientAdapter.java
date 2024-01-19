package dhsa.project.adapter;

import dhsa.project.view.PatientView;
import dhsa.project.view.ViewService;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientAdapter implements ResourceAdapter<Patient, PatientView> {

    @Autowired
    private ViewService viewService;

    public PatientView transform(Patient res) {
        return new PatientView(
            res.getIdentifierFirstRep().getValue(),
            "Patient/" + res.getIdElement().getIdPart(),

            res.getNameFirstRep().getFamily(),
            res.getNameFirstRep().getGivenAsSingleString(),
            res.getGender().getDisplay(),
            res.getBirthDateElement().toHumanDisplay(),

            res.getDeceased() instanceof DateTimeType ?
                viewService.getDateString(res.getDeceasedDateTimeType().getValue()) : "no",

            res.getIdentifier().get(1).getValue(),
            res.getIdentifier().get(2).getValue(),
            res.getIdentifier().get(3).getValue(),

            ((Address) res.getExtensionByUrl("http://hl7.org/fhir/StructureDefinition/patient-birthPlace")
                .getValue()).getText(),

            List.of(
                res.getAddressFirstRep().getLine().get(0).getValue(),
                res.getAddressFirstRep().getCity(),
                res.getAddressFirstRep().getState(),
                res.getAddressFirstRep().getPostalCode() == null ? "N/A" :
                    res.getAddressFirstRep().getPostalCode()
            )
        );
    }
}
