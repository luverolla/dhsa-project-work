package dhsa.project.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImmunizationFilter implements Filter {
    private String patient = "";
    private String encounter = "";

    private String dateFrom = "";
    private String dateTo = "";
    private String vaccine = "";

    private int perPage = 5;
    private SortElement sort = new SortElement("date", "desc");
}
