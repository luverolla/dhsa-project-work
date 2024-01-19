package dhsa.project.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcedureFilter implements Filter {
    private String patient = "";
    private String encounter = "";

    private String code = "";
    private String reason = "";
    private String dateFrom = "";
    private String dateTo = "";

    private int perPage = 5;
    private SortElement sort = new SortElement("date", "desc");
}
