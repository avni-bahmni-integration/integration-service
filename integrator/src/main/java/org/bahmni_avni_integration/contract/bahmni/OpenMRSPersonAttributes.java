package org.bahmni_avni_integration.contract.bahmni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.util.ObjectJsonMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class OpenMRSPersonAttributes extends ArrayList<OpenMRSPersonAttribute> implements Jsonify {
    private static Logger logger = Logger.getLogger(OpenMRSPersonAttributes.class);

    public String getGivenLocalName() {
        for (OpenMRSPersonAttribute attribute : this) {
            if(attribute.getAttributeType().isGivenLocalName()) {
                return attribute.getValue().toString();
            }
        }
        return "";
    }

    public String getFamilyLocalName() {
        for (OpenMRSPersonAttribute attribute : this) {
            if(attribute.getAttributeType().isFamilyLocalName()) {
                return attribute.getValue().toString();
            }
        }
        return "";
    }

    public String getMiddleLocalName() {
        for (OpenMRSPersonAttribute attribute : this) {
            if(attribute.getAttributeType().isMiddleLocalName()) {
                return attribute.getValue().toString();
            }
        }
        return "";
    }

    @Override
    public String toJsonString() {
        HashMap<String, Object> personAttributes = new HashMap<>();
        String attrName;
        Object attrValue = null;
        for (OpenMRSPersonAttribute openMRSPersonAttribute : this) {
            attrName = openMRSPersonAttribute.getAttributeType().getDisplay();
            try{
                if (openMRSPersonAttribute.getValue() instanceof HashMap) {
                    attrValue = ((HashMap) openMRSPersonAttribute.getValue()).get("display");
                } else {
                    attrValue = openMRSPersonAttribute.getValue();
                }
            }
             catch (ClassCastException e){
                 logger.error("Unable to convert personAttributes"+ openMRSPersonAttribute.getValue().getClass() + "to json string. " + e.getMessage());
             }

            personAttributes.put(attrName, attrValue);
        }

        return ObjectJsonMapper.writeValueAsString(personAttributes);
    }
}
