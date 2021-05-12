package org.bahmni_avni_integration.migrator.domain;

import java.util.List;
import java.util.Objects;

public final class AvniConcept {
    private long id;
    private String name;
    private AvniConceptDataType dataType;
    private List<AvniConcept> answerConcepts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AvniConceptDataType getDataType() {
        return dataType;
    }

    public void setDataType(AvniConceptDataType dataType) {
        this.dataType = dataType;
    }

    public List<AvniConcept> getAnswerConcepts() {
        return answerConcepts;
    }

    public void setAnswerConcepts(List<AvniConcept> answerConcepts) {
        this.answerConcepts = answerConcepts;
    }

    public boolean isCoded() {
        return dataType == AvniConceptDataType.Coded;
    }
}