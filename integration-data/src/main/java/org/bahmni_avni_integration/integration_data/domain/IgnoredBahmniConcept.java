package org.bahmni_avni_integration.integration_data.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class IgnoredBahmniConcept extends BaseEntity {
    @Column
    private String conceptUuid;

    public IgnoredBahmniConcept() {
    }

    public IgnoredBahmniConcept(String conceptUuid) {
        this.conceptUuid = conceptUuid;
    }

    public String getConceptUuid() {
        return conceptUuid;
    }
}