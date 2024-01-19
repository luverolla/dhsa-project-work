package dhsa.project.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ObservationView {
    private String id;
    private NamedReference code;
    private String encounter;
    private String datetime;
    private String value;
    private boolean valueBinary;
}
