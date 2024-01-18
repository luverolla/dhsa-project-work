package dhsa.project.bridge;

import dhsa.project.view.ViewService;
import dhsa.project.view.DeviceRequestView;
import org.hl7.fhir.r4.model.DeviceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceRequestBridge implements ResourceBridge<DeviceRequest, DeviceRequestView> {

    @Autowired
    private ViewService viewService;
    public DeviceRequestView transform(DeviceRequest res) {
        return new DeviceRequestView(
            res.getIdentifierFirstRep().getValue(),
            viewService.getDateTimeString(res.getOccurrencePeriod().getStart()),
            viewService.getDateTimeString(res.getOccurrencePeriod().getEnd()),
            viewService.getPatientNR(res.getSubject()),
            viewService.getEncounterNR(res.getEncounter()),
            viewService.getCodingRef(res.getCodeCodeableConcept())
        );
    }
}
