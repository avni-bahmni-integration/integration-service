package org.avni_integration_service.migrator.domain;

import org.avni_integration_service.util.ObsDataType;

import java.util.ArrayList;
import java.util.List;

public class OpenMRSPersonAttribute implements OpenMRSTerminology {
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

    public String getAvniName() {
        return NameMapping.fromBahmniPersonAttributeToAvni(name);
    }

    public enum AttributeType {
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

    public String getAvniDataType() {
        if (attributeType.equals(AttributeType.Primitive)) return ObsDataType.Text.toString();
        if (attributeType.equals(AttributeType.Coded)) return ObsDataType.Coded.toString();
        throw new RuntimeException(String.format("OpenMRS attribute type: %s not mapped to Avni data type", attributeType.name()));
    }

    public List<OpenMRSConcept> getAnswers() {
        return answers;
    }
}
