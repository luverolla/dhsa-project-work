package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.adapter.PatientAdapter;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.PatientFilter;
import dhsa.project.view.PatientView;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientSearcher extends BaseSearcher<PatientView, PatientFilter> {

    @Autowired
    private PatientAdapter adapter;

    @Autowired
    private FhirService fhirService;

    public PatientView transform(Resource res) {
        return adapter.transform((Patient) res);
    }

    public IQuery<?> search(PatientFilter pse) {

        IQuery<?> query = fhirService.getClient()
            .search().forResource(Patient.class);

        if (!pse.getName().isEmpty())
            query = query.and(Patient.GIVEN.matches().value(pse.getName()));
        if (!pse.getSurname().isEmpty())
            query = query.and(Patient.FAMILY.matches().value(pse.getSurname()));

        if (!pse.getSex().isEmpty())
            query = query.and(Patient.GENDER.exactly().code(pse.getSex()));

        if (!pse.getBirthDateFrom().isEmpty())
            query = query.and(Patient.BIRTHDATE.afterOrEquals().day(pse.getBirthDateFrom()));
        if (!pse.getBirthDateTo().isEmpty())
            query = query.and(Patient.BIRTHDATE.beforeOrEquals().day(pse.getBirthDateTo()));

        if (pse.getDead().equals("yes")) {
            query = query.and(Patient.DECEASED.exactly().code("true"));
            if (!pse.getDeathDateFrom().isEmpty())
                query = query.and(Patient.DEATH_DATE.afterOrEquals().day(pse.getDeathDateFrom()));
            if (!pse.getDeathDateTo().isEmpty())
                query = query.and(Patient.DEATH_DATE.beforeOrEquals().day(pse.getDeathDateTo()));
        } else if (pse.getDead().equals("no")) {
            query = query.and(Patient.DEATH_DATE.isMissing(true));
        }

        if (!pse.getSsnNumber().isEmpty())
            query = query.and(
                Patient.IDENTIFIER.exactly()
                    .systemAndIdentifier("http://hl7.org/fhir/sid/us-ssn", pse.getSsnNumber().trim())
            );
        if (!pse.getPassport().isEmpty())
            query = query.and(
                Patient.IDENTIFIER.exactly()
                    .systemAndIdentifier("http://standardhealthrecord.org/fhir/StructureDefinition/passportNumber", pse.getPassport())
            );

        if (!pse.getDriverLicense().isEmpty())
            query = query.and(
                Patient.IDENTIFIER.exactly()
                    .systemAndIdentifier("urn:oid:2.16.840.1.113883.4.3.25", pse.getDriverLicense())
            );

        return query;
    }
}
