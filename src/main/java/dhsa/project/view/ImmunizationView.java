package dhsa.project.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ImmunizationView {
    private NamedReference patient;
    private NamedReference encounter;
    private NamedReference vaccine;
    private String date;
}
