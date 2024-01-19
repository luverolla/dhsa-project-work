package dhsa.project.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObservationFilter implements Filter {
    private String patient = "";
    private String encounter = "";

    private String code = "";
    private String dateFrom = "";
    private String dateTo = "";
    private String valueString = "";
    private Float valueFrom = null;
    private Float valueTo = null;

    private int perPage = 5;
    private SortElement sort = new SortElement("date", "desc");
}
