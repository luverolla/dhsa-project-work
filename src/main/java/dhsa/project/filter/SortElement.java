package dhsa.project.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Class holding sorting fields
 */
@Getter
@Setter
@AllArgsConstructor
public class SortElement {

    /**
     * name of the field to sort by
     */
    public String sortField;

    /**
     * order of the sorting, either "asc" or "desc"
     */
    public String sortOrder;
}
