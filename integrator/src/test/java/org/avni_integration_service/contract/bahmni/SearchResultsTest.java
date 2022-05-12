package org.avni_integration_service.contract.bahmni;

import org.avni_integration_service.bahmni.contract.OpenMRSUuidHolder;
import org.avni_integration_service.bahmni.contract.SearchResults;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SearchResultsTest {
    @Test
    public void removeDuplicates() {
        SearchResults<OpenMRSUuidHolder> searchResults = new SearchResults<>();
        searchResults.setResults(List.of(createHolder("bee99a93-a024-43c2-afe4-f305183b1a9b"), createHolder("bee99a93-a024-43c2-afe4-f305183b1a9b")));
        assertEquals(2, searchResults.getResults().size());
        SearchResults<OpenMRSUuidHolder> withoutDuplicates = searchResults.removeDuplicates();
        assertEquals(1, withoutDuplicates.getResults().size());
    }

    private OpenMRSUuidHolder createHolder(String uuid) {
        OpenMRSUuidHolder openMRSUuidHolder = new OpenMRSUuidHolder();
        openMRSUuidHolder.setUuid(uuid);
        return openMRSUuidHolder;
    }
}
