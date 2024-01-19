package dhsa.project.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionFilter implements Filter {
    private String patient = "";
    private String encounter = "";

    private String code = "";
    private String onsetFrom = "";
    private String onsetTo = "";
    private String active = "all";
    private String abatementFrom = "";
    private String abatementTo = "";

    private int perPage;
    private SortElement sort = new SortElement("onset-date", "desc");
}
