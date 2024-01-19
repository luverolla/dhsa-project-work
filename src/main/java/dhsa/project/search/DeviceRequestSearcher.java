package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.adapter.AdapterService;
import dhsa.project.fhir.FhirService;
import dhsa.project.filter.DeviceRequestFilter;
import dhsa.project.view.DeviceRequestView;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.DeviceRequest;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceRequestSearcher extends BaseSearcher<DeviceRequestView, DeviceRequestFilter> {

    @Autowired
    private FhirService fhirService;

    @Autowired
    private AdapterService adapter;

    public DeviceRequestView transform(Resource res) {
        return adapter.getView((DeviceRequest) res);
    }

    public IQuery<?> search(DeviceRequestFilter drf) {
        IQuery<?> query = fhirService.getClient().search()
            .forResource(DeviceRequest.class);

        if (!drf.getPatient().isEmpty())
            query = query.where(DeviceRequest.SUBJECT.hasId("Patient/" + drf.getPatient()));
        if (!drf.getEncounter().isEmpty())
            query = query.where(DeviceRequest.ENCOUNTER.hasId("Encounter/" + drf.getEncounter()));

        if (!drf.getCode().isEmpty())
            query = query.and(DeviceRequest.CODE.exactly().code(drf.getCode()));
        if (!drf.getUdi().isEmpty())
            query = query.and(DeviceRequest.DEVICE.hasChainedProperty(
                Device.IDENTIFIER.exactly().code(drf.getUdi())
            ));
        if (!drf.getStartFrom().isEmpty())
            query = query.and(DeviceRequest.EVENT_DATE.afterOrEquals().day(drf.getStartFrom()));
        if (!drf.getStartTo().isEmpty())
            query = query.and(DeviceRequest.EVENT_DATE.beforeOrEquals().day(drf.getStartTo()));

        return query;
    }
}
