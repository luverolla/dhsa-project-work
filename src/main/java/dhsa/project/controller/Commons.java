package dhsa.project.controller;

import dhsa.project.adapter.AdapterService;
import dhsa.project.cda.CDAImporter;
import dhsa.project.fhir.FhirService;
import dhsa.project.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Utility class with shared fields and methods for controllers.
 */
@Service
public class Commons {

    final Logger logger = Logger.getLogger("UIController");
    @Autowired
    SearchService searchService;
    @Autowired
    AdapterService adapterService;
    @Autowired
    FhirService fhirService;
    @Autowired
    CDAImporter cdaImporter;

    /**
     * Builds an URL query string from a map of parameters.
     * Given a <pre>{(name1, value1), ..., (nameN, valueN)}</pre> map of parameters, returns a string of the form
     * <pre>&name1=value1&...&nameN=valueN</pre>
     * No sanitization is performed, it's assumed that the entity providing
     * the parameters yields safe values.
     *
     * @param params (name, value) map of parameters
     * @return query string
     */
    String getParamString(Map<String, String> params) {
        StringBuilder paramString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramString.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return paramString.toString();
    }
}
