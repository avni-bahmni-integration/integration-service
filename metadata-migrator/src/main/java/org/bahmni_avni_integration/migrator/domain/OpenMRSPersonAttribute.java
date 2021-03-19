package org.bahmni_avni_integration.migrator.domain;

import java.util.ArrayList;
import java.util.List;

public class OpenMRSPersonAttribute {
    private String uuid;
    private String name;
    private AttributeType attributeType;
    private List<OpenMRSConcept> answers = new ArrayList<>();

    public OpenMRSPersonAttribute(String uuid, String name, AttributeType attributeType) {
        this.uuid = uuid;
        this.name = name;
        this.attributeType = attributeType;
    }

    public static OpenMRSPersonAttribute createPrimitive(String uuid, String name) {
        return new OpenMRSPersonAttribute(uuid, name, AttributeType.Primitive);
    }

    public static OpenMRSPersonAttribute createCoded(String uuid, String name) {
        return new OpenMRSPersonAttribute(uuid, name, AttributeType.Coded);
    }

    public void addAnswer(OpenMRSConcept openMRSConcept) {
        answers.add(openMRSConcept);
    }

    enum AttributeType {
        Primitive, Coded
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public List<OpenMRSConcept> getAnswers() {
        return answers;
    }
}