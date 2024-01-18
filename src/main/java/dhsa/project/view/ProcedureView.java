package dhsa.project.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ProcedureView {
    private NamedReference patient;
    private NamedReference encounter;
    private NamedReference code;
    private NamedReference reason;
    private String datetime;
}
