package dhsa.project.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientFilter implements Filter {

    private String name = "";
    private String surname = "";
    private String sex = "";
    private String birthDateFrom = "";
    private String birthDateTo = "";
    private String dead = "all";
    private String deathDateFrom = "";
    private String deathDateTo = "";
    private String ssnNumber = "";
    private String passport = "";
    private String driverLicense = "";
    private int perPage = 15;

    private SortElement sort = new SortElement("family", "asc");
}
