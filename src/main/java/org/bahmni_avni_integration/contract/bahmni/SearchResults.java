package org.bahmni_avni_integration.contract.bahmni;

import java.util.List;

public class SearchResults<T> {
    private List<T> results;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}