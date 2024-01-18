package dhsa.project.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncounterFilter implements Filter {
    private String patient = "";
    private String practitioner = "";
    private String provider = "";

    private String dateFrom = "";
    private String dateTo = "";
    private String type = "";
    private String reason = "";

    private int perPage = 15;
    private SortElement sort = new SortElement("date", "desc");
}
