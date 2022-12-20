package org.avni_integration_service.avni.repository;

import org.avni_integration_service.avni.MultipleResultsFoundException;

public class BaseAvniRepository {
    protected <T> T pickAndExpectOne(T[] array) {
        if (array.length == 0) return null;
        if (array.length > 1)
            throw new MultipleResultsFoundException("More than one entity found");
        return array[0];
    }
}
