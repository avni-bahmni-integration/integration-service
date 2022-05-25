package org.avni_integration_service.web.response;

public class BaseEntityContract {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BaseEntityContract(int id) {
        this.id = id;
    }

    public BaseEntityContract() {
    }
}
