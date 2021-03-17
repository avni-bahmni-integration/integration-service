package org.bahmni_avni_integration.migrator.domain;

public final class AvniFormElement {
    private Long id;
    private AvniConcept concept;

    public AvniConcept getConcept() {
        return concept;
    }

    public void setConcept(AvniConcept avniConcept) {
        this.concept = avniConcept;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}