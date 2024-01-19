package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.filter.Filter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.util.List;

/**
 * Abstract class for searchers
 *
 * @param <V> the view object
 * @param <F> the filter
 */
public abstract class BaseSearcher<V, F extends Filter> implements Searcher<V, F> {

    public abstract IQuery<?> search(F filter);

    /**
     * Transforms a generic FHIR resource into a view object
     *
     * @param entry the FHIR resource
     * @return the view object
     */
    public abstract V transform(Resource entry);

    /**
     * First perform a search query and then sort and page the results
     *
     * @param filter the filter containing the search parameters
     * @param page   the page number
     * @return the result set containing the results of the search
     */
    public ResultSet<V> retrieve(F filter, int page) {
        IQuery<?> query = search(filter);

        if (filter.getSort().getSortOrder().equals("asc"))
            query = query.sort().ascending(filter.getSort().getSortField());
        else
            query = query.sort().descending(filter.getSort().getSortField());

        Bundle bundle = query.offset((page - 1) * filter.getPerPage())
            .count(filter.getPerPage())
            .returnBundle(Bundle.class)
            .execute();

        List<V> entries = bundle.getEntry()
            .stream()
            .map(Bundle.BundleEntryComponent::getResource)
            .map(this::transform)
            .toList();

        boolean hasPrev = bundle.getLink(Bundle.LINK_PREV) != null,
            hasNext = bundle.getLink(Bundle.LINK_NEXT) != null;
        return new ResultSet<>(entries, hasPrev, hasNext);
    }
}
