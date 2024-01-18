package dhsa.project.search;

import ca.uhn.fhir.rest.gclient.IQuery;
import dhsa.project.filter.Filter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.util.List;

public abstract class BaseSearcher<V, F extends Filter> {

    public abstract IQuery<?> search(F filter);

    public abstract V transform(Resource entry);

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
