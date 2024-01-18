package dhsa.project.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DeviceRequestView {
    private String udi;
    private String start;
    private String stop;
    private NamedReference patient;
    private NamedReference encounter;
    private NamedReference device;
}
