package dhsa.project.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class OrganizationView {

    private String id;
    private String url;
    private String name;
    private String phone;
    private List<String> address;
}
