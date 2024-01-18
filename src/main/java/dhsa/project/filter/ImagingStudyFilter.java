package dhsa.project.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImagingStudyFilter implements Filter {
    private String patient = "";
    private String encounter = "";

    private String bodySite = "";
    private String modality = "";
    private String sop = "";
    private String dateFrom = "";
    private String dateTo = "";

    private int perPage;
    private SortElement sort = new SortElement("started", "desc");
}
