package dhsa.project.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Setter
public class ResultSet<T> {
    @Getter
    private List<T> entries;
    private boolean prev;
    private boolean next;

    public boolean hasPrev() {
        return prev;
    }

    public boolean hasNext() {
        return next;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
