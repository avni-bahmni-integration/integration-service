package org.avni_integration_service.migrator.domain;

public final class CreateConceptResult {
    private final int conceptId;
    private final boolean conceptExists;

    public CreateConceptResult(int conceptId, boolean conceptExists) {
        this.conceptId = conceptId;
        this.conceptExists = conceptExists;
    }

    public int conceptId() {
        return conceptId;
    }

    public boolean conceptExists() {
        return conceptExists;
    }
}
