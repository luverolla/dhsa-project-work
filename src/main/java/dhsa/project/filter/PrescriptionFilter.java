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
    private boolean active = false;
    private String endDateFrom = "";
    private String endDateTo = "";

    private int perPage = 15;
    private SortElement sort = new SortElement("date", "desc");
}
