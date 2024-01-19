package dhsa.project.filter;

/**
 * Interface for filters
 * Classes implementing this interface holds filtering values directly connected to the web-forms' fields
 */
public interface Filter {
    /**
     * Returns the maximum number of elements to be displayed in each page
     */
    int getPerPage();

    /**
     * Returns the sorting element
     */
    SortElement getSort();
}
