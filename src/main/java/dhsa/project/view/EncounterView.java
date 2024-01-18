package dhsa.project.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EncounterView {
    private String id = "";
    private String url = "";

    private NamedReference patient = new NamedReference();
    private NamedReference practitioner = new NamedReference();
    private NamedReference organization = new NamedReference();
    private NamedReference type = new NamedReference();
    private NamedReference reason = new NamedReference();
    private NamedReference payer = new NamedReference();
    private String baseCost = "";
    private String totalCost = "";
    private String covered = "";
    private String datetime = "";
    private String base64CDA = "";
    private String cdaCreationDate = "";
}
