package dhsa.project.view;

import dhsa.project.fhir.FhirService;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service bean with utility methods for dealing with view objects.
 */
@Service
public class ViewService {

    private final SimpleDateFormat ONLYDATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat DATETIME_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Autowired
    private FhirService fhirService;

    /**
     * Returns a string representation of the given date
     *
     * @param dt          the date
     * @param replacement the string to return if the date is null
     * @return the string representation of the date or the replacement string
     */
    public String getDateString(Date dt, String replacement) {
        if (dt == null)
            return replacement;
        return ONLYDATE_FMT.format(dt);
    }

    /**
     * Returns a string representation of the given date or "N/A" if the date is null
     *
     * @param dt the date
     * @return date string or "N/A"
     */
    public String getDateString(Date dt) {
        return getDateString(dt, "N/A");
    }

    /**
     * Returns a string representation of the given date and time
     *
     * @param dt the date object (with time)
     * @return the string representation of the date and time
     */
    public String getDateTimeString(Date dt) {
        return DATETIME_FMT.format(dt);
    }

    /**
     * Returns a string representation of the given money object
     *
     * @param m the money object
     * @return the string representation of the money object
     */
    public String getMoneyString(Money m) {
        return m.getValue().toString() + " " + m.getCurrency();
    }

    /**
     * Returns a (uri, display) pair for the given coding object
     * For null-alike fields, the returned pair is ("", "N/A")
     *
     * @param coding the coding object
     * @return the (uri, display) pair
     */
    public NamedReference getCodingRef(Coding coding) {
        if (coding.getCode() == null)
            return new NamedReference("", "N/A");
        return new NamedReference(coding.getSystem() + "/" + coding.getCode(), coding.getDisplay());
    }

    /**
     * Returns a (uri, display) pair for the given <tt>CodeableConcept</tt> object
     * For null-alike fields, the returned pair is ("", "N/A")
     *
     * @param cc the <tt>CodeableConcept</tt> object
     * @return the (uri, display) pair
     */
    public NamedReference getCodingRef(CodeableConcept cc) {
        if (cc.getCoding().isEmpty())
            return new NamedReference("", "N/A");
        return getCodingRef(cc.getCodingFirstRep());
    }

    /**
     * Returns a (uri, display) pair for the given <tt>Reference</tt> object of <tt>Patient</tt> type
     * The display is the patient's full name, the URI is the URL to the patient's file webpage
     * For null-alike fields, the returned pair is ("", "N/A")
     *
     * @param pt the <tt>Reference</tt> object
     * @return the (uri, display) pair
     */
    public NamedReference getPatientNR(Reference pt) {
        Patient p = fhirService.getClient().read().resource(Patient.class).withId(pt.getReference()).execute();
        String url = "/web/patient/" + p.getIdElement().getIdPart(),
            display = p.getNameFirstRep().getFamily() + " " + p.getNameFirstRep().getGivenAsSingleString();
        return new NamedReference(url, display);
    }

    /**
     * Returns a (uri, display) pair for the given <tt>Reference</tt> object of <tt>Practitioner</tt> type
     * The display is the practitioner's full name, the URI is the URL to the practitioner's file webpage
     * For null-alike fields, the returned pair is ("", "N/A")
     *
     * @param pr the <tt>Reference</tt> object
     * @return the (uri, display) pair
     */
    public NamedReference getPractitionerNR(Reference pr) {
        Practitioner p = fhirService.getClient().read().resource(Practitioner.class).withId(pr.getReference()).execute();
        String url = "/web/practitioner/" + p.getIdElement().getIdPart(),
            display = p.getNameFirstRep().getText();
        return new NamedReference(url, display);
    }

    /**
     * Returns a (uri, display) pair for the given <tt>Reference</tt> object of <tt>Organization</tt> type
     * The display is the organization's business name, the URI is the URL to the organization's file webpage
     * For null-alike fields, the returned pair is ("", "N/A")
     *
     * @param or the <tt>Reference</tt> object
     * @return the (uri, display) pair
     */
    public NamedReference getOrganizationNR(Reference or) {
        if (or.getReference() == null)
            return new NamedReference("", "N/A");
        Organization o = fhirService.getClient().read().resource(Organization.class).withId(or.getReference()).execute();
        String url = "/web/organization/" + o.getIdElement().getIdPart();
        return new NamedReference(url, o.getName());
    }

    /**
     * Returns a (uri, display) pair for the given <tt>Reference</tt> object of <tt>Encounter</tt> type
     * The display is the encounter's UID, the URI is the URL to the encounter's file webpage
     * For null-alike fields, the returned pair is ("", "N/A")
     *
     * @param en the <tt>Reference</tt> object
     * @return the (uri, display) pair
     */
    public NamedReference getEncounterNR(Reference en) {
        if (en.getReference() == null)
            return new NamedReference("", "N/A");

        Encounter e = fhirService.getClient().read().resource(Encounter.class).withId(en.getReference()).execute();
        String url = "/web/encounter/" + e.getIdElement().getIdPart();
        return new NamedReference(url, e.getIdElement().getIdPart());
    }

    /**
     * Returns a (uri, display) pair for the given <tt>Reference</tt> object of <tt>Device</tt> type
     * The display is the display name associated with the SNOMED type code,
     * the URI is the URL to the SNOMED page of the type code
     * For null-alike fields, the returned pair is ("", "N/A")
     *
     * @param ref the <tt>Reference</tt> object
     * @return the (uri, display) pair
     */
    public NamedReference getDeviceNR(Reference ref) {
        if (ref.getReference() == null)
            return new NamedReference("", "N/A");

        Device d = fhirService.getClient().read().resource(Device.class).withId(ref.getReference()).execute();
        String url = "https://snomed.info/sct/" + d.getType().getCodingFirstRep().getCode();
        return new NamedReference(url, d.getType().getCodingFirstRep().getDisplay());
    }
}
