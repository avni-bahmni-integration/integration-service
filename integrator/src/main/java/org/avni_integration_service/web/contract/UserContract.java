package org.avni_integration_service.web.contract;

import org.avni_integration_service.integration_data.domain.User;
import org.avni_integration_service.web.response.BaseEntityContract;

public class UserContract extends BaseEntityContract {
    private String email;
    private int workingIntegrationSystemId;

    public UserContract() {
    }

    public UserContract(User user) {
        super(user.getId());
        this.email = user.getEmail();
        this.workingIntegrationSystemId = user.getWorkingIntegrationSystem().getId();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getWorkingIntegrationSystemId() {
        return workingIntegrationSystemId;
    }

    public void setWorkingIntegrationSystemId(int workingIntegrationSystemId) {
        this.workingIntegrationSystemId = workingIntegrationSystemId;
    }
}
