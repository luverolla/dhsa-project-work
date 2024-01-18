package dhsa.project.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PractitionerFilter implements Filter {
    private String name = "";
    private String speciality = "";
    private String sex = "";
    private List<String> organizations = List.of();
    private int perPage = 15;
    private SortElement sort = new SortElement("name", "asc");
}
