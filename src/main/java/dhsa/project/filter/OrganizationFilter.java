package dhsa.project.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationFilter implements Filter {

    private String name = "";
    private int perPage = 15;
    private String state = "";
    private String city = "";
    private String zip = "";
    private SortElement sort = new SortElement("name", "asc");
}
