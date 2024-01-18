package dhsa.project.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PatientView {

    private String id;
    private String url;

    private String surname;
    private String name;
    private String sex;
    private String birthDate;
    private String dead;
    private String ssnNumber;
    private String passport;
    private String driverLicense;
    private String birthPlace;
    private List<String> address;
}
