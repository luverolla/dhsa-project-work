package dhsa.project.view;

import dhsa.project.fhir.FhirService;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ViewService {

    private final SimpleDateFormat ONLYDATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat DATETIME_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Autowired
    private FhirService fhirService;

    public String getDateString(Date dt, String replacement) {
        if (dt == null)
            return replacement;
        return ONLYDATE_FMT.format(dt);
    }

    public String getDateString(Date dt) {
        return getDateString(dt, "N/A");
    }

    public String getDateTimeString(Date dt) {
        return DATETIME_FMT.format(dt);
    }

    public String getMoneyString(Money m) {
        return m.getValue().toString() + " " + m.getCurrency();
    }

    public NamedReference getCodingRef(Coding coding) {
        if (coding.getCode() == null)
            return new NamedReference("", "N/A");
        return new NamedReference(coding.getSystem() + "/" + coding.getCode(), coding.getDisplay());
    }

    public NamedReference getCodingRef(CodeableConcept cc) {
        if (cc.getCoding().isEmpty())
            return new NamedReference("", "N/A");
        return getCodingRef(cc.getCodingFirstRep());
    }

    public NamedReference getPatientNR(Reference pt) {
        Patient p = fhirService.getClient().read().resource(Patient.class).withId(pt.getReference()).execute();
        String url = "/web/patient/" + p.getIdElement().getIdPart(),
            display = p.getNameFirstRep().getFamily() + " " + p.getNameFirstRep().getGivenAsSingleString();
        return new NamedReference(url, display);
    }

    public NamedReference getPractitionerNR(Reference pr) {
        Practitioner p = fhirService.getClient().read().resource(Practitioner.class).withId(pr.getReference()).execute();
        String url = "/web/practitioner/" + p.getIdElement().getIdPart(),
            display = p.getNameFirstRep().getText();
        return new NamedReference(url, display);
    }

    public NamedReference getOrganizationNR(Reference or) {
        if (or.getReference() == null)
            return new NamedReference("", "N/A");
        Organization o = fhirService.getClient().read().resource(Organization.class).withId(or.getReference()).execute();
        String url = "/web/organization/" + o.getIdElement().getIdPart();
        return new NamedReference(url, o.getName());
    }

    public NamedReference getEncounterNR(Reference en) {
        if (en.getReference() == null)
            return new NamedReference("", "N/A");

        Encounter e = fhirService.getClient().read().resource(Encounter.class).withId(en.getReference()).execute();
        String url = "/web/encounter/" + e.getIdElement().getIdPart();
        return new NamedReference(url, e.getIdElement().getIdPart());
    }
}
