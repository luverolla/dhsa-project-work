package dhsa.project.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NamedReference {
    private String uri = "";
    private String display = "";

    @JsonIgnore
    public boolean isValid() {
        return (
            !uri.isEmpty() && !uri.isBlank() &&
                (uri.startsWith("http://") || uri.startsWith("urn:"))
        );
    }
}
