package dhsa.project.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ImagingStudyView {
    private String id;
    private String url;
    private NamedReference patient;
    private NamedReference encounter;
    private NamedReference bodySite;
    private NamedReference modality;
    private NamedReference sop;
    private String date;
    private List<String> frames;
}
