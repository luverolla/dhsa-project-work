package dhsa.project.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Class holding the result of a search
 *
 * @param <T> type of the entries
 */
@AllArgsConstructor
@Setter
public class ResultSet<T> {
    @Getter
    private List<T> entries;
    private boolean prev;
    private boolean next;

    /**
     * Checks if there is a previous page
     *
     * @return true if there is a previous page, false otherwise
     */
    public boolean hasPrev() {
        return prev;
    }

    /**
     * Checks if there is a next page
     *
     * @return true if there is a next page, false otherwise
     */
    public boolean hasNext() {
        return next;
    }

    /**
     * Checks if the result set is empty
     *
     * @return true if the result set is empty, false otherwise
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
