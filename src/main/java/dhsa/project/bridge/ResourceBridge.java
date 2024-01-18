package dhsa.project.bridge;

import org.hl7.fhir.r4.model.Resource;

public interface ResourceBridge<R extends Resource, V> {
    V transform(R resource);
}
