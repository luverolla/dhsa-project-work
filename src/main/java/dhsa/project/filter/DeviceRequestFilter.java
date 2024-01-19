package dhsa.project.filter;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeviceRequestFilter implements Filter {
    private String patient = "";
    private String encounter = "";

    private String udi = "";
    private String code = "";
    private String startFrom = "";
    private String startTo = "";
    private boolean active = false;
    private String stopFrom = "";
    private String stopTo = "";

    private int perPage = 5;
    private SortElement sort = new SortElement("event-date", "desc");
}
