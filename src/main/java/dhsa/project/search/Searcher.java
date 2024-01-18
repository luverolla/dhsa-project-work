package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.filter.Filter;

public interface Searcher<V, F extends Filter> {
    IQuery<?> search(F filter);
    ResultSet<V> retrieve(F filter, int page);
}
