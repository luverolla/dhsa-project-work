package dhsa.project.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrescriptionFilter implements Filter {
    private String patient = "";
    private String encounter = "";

    private String medication = "";
    private String reason = "";
    private String startDateFrom = "";
    private String startDateTo = "";
    private String active = "all";

    private int perPage = 5;
    private SortElement sort = new SortElement("date", "desc");
}
