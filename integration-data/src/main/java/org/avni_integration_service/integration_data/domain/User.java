package org.avni_integration_service.integration_data.domain;

import org.avni_integration_service.integration_data.domain.framework.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "working_integration_system_id")
    private IntegrationSystem workingIntegrationSystem;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public IntegrationSystem getWorkingIntegrationSystem() {
        return workingIntegrationSystem;
    }

    public void setWorkingIntegrationSystem(IntegrationSystem workingIntegrationSystem) {
        this.workingIntegrationSystem = workingIntegrationSystem;
    }
}
