package dhsa.project.adapter;

import org.hl7.fhir.r4.model.Resource;

/**
 * Interface for transforming FHIR resources into simplified view objects.
 *
 * @param <R> FHIR resource type
 * @param <V> target View object type
 */
public interface ResourceAdapter<R extends Resource, V> {
    /**
     * Transform a FHIR resource into a view object.
     *
     * @param resource FHIR resource
     * @return view object
     */
    V transform(R resource);
}
