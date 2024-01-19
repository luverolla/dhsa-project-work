package dhsa.project.adapter;

import dhsa.project.view.DeviceRequestView;
import dhsa.project.view.ViewService;
import org.hl7.fhir.r4.model.DeviceRequest;
import org.hl7.fhir.r4.model.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceRequestAdapter implements ResourceAdapter<DeviceRequest, DeviceRequestView> {

    @Autowired
    private ViewService viewService;

    public DeviceRequestView transform(DeviceRequest res) {
        Period p = res.getOccurrencePeriod();
        return new DeviceRequestView(
            res.getIdentifierFirstRep().getValue(),
            viewService.getDateTimeString(res.getOccurrencePeriod().getStart()),
            p.getEnd() == null ? "" :
                viewService.getDateTimeString(res.getOccurrencePeriod().getEnd()),
            viewService.getPatientNR(res.getSubject()),
            viewService.getEncounterNR(res.getEncounter()),
            viewService.getDeviceNR(res.getCodeReference())
        );
    }
}
