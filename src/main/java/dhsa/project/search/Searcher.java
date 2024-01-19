package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.filter.Filter;

/**
 * Interface for searchers
 * Classes implementing this interface holds the logic for searching and retrieving data
 */
public interface Searcher<V, F extends Filter> {
    /**
     * Returns the query complete of all search parameter but still not sorted nor paged
     *
     * @param filter the filter containing the search parameters
     * @return query object
     */
    IQuery<?> search(F filter);

    /**
     * Performs a full search query with sorting and paging parameters
     *
     * @param filter the filter containing the search parameters
     * @param page   the page number
     * @return the result set containing the results of the search
     */
    ResultSet<V> retrieve(F filter, int page);
}
