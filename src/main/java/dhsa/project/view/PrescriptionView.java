package dhsa.project.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PrescriptionView {
    private NamedReference patient;
    private NamedReference encounter;
    private NamedReference medication;
    private NamedReference reason;
    private String start;
    private String end;
    private String dispenses;
    private String baseCost;
    private String totalCost;
    private String covered;
    private NamedReference payer;
}
