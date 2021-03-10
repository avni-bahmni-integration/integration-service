package org.bahmni_avni_integration.contract.bahmni;

import java.util.List;
import java.util.stream.Collectors;

public class SearchResults<T> {
    private List<T> results;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public SearchResults<T> removeDuplicates() {
        SearchResults<T> tSearchResults = new SearchResults<>();
        tSearchResults.setResults(this.results.stream().distinct().collect(Collectors.toList()));
        return tSearchResults;
    }
}