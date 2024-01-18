package dhsa.project.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PractitionerView {

    private String id;
    private String url;

    private String name;
    private String sex;
    private List<String> address;
    private String speciality;
    private NamedReference organization;
}
