package dhsa.project.bridge;

import dhsa.project.view.OrganizationView;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationBridge implements ResourceBridge<Organization, OrganizationView> {
    public OrganizationView transform(Organization res) {
        return new OrganizationView(
            res.getIdElement().getIdPart(),
            res.getId(),
            res.getName(),
            res.getTelecomFirstRep().getValue(),
            List.of(
                res.getAddressFirstRep().getCity(),
                res.getAddressFirstRep().getState(),
                res.getAddressFirstRep().getPostalCode()
            )
        );
    }
}
