package dhsa.project.bridge;

import dhsa.project.view.ViewService;
import dhsa.project.view.PractitionerView;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PractitionerBridge implements ResourceBridge<Practitioner, PractitionerView> {

    @Autowired
    private ViewService viewService;

    public PractitionerView transform(Practitioner res) {
        return new PractitionerView(
            res.getIdElement().getIdPart(),
            res.getId(),
            res.getNameFirstRep().getNameAsSingleString(),
            res.getGender().getDisplay(),

            List.of(
                res.getAddressFirstRep().getCity(),
                res.getAddressFirstRep().getState(),
                res.getAddressFirstRep().getPostalCode() == null ? "N/A" :
                    res.getAddressFirstRep().getPostalCode()
            ),

            res.getQualificationFirstRep().getCode().getCodingFirstRep().getDisplay(),
            viewService.getOrganizationNR(res.getQualificationFirstRep().getIssuer())
        );
    }
}
