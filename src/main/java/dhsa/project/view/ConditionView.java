package dhsa.project.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ConditionView {
    private NamedReference patient;
    private NamedReference encounter;
    private NamedReference code;
    private String start;
    private String stop;
}
