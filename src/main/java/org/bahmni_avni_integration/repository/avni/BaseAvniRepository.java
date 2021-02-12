package org.bahmni_avni_integration.repository.avni;

import org.bahmni_avni_integration.repository.MultipleResultsFoundException;

public class BaseAvniRepository {
    protected <T> T pickAndExpectOne(T[] array) {
        if (array.length == 0) return null;
        if (array.length > 1) throw new MultipleResultsFoundException("More than one entity found");
        return array[0];
    }
}
