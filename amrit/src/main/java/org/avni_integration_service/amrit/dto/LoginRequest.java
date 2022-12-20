package org.avni_integration_service.amrit.dto;

public class LoginRequest {
    private boolean doLogout;
    private String userName;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String userName, String password) {
        this.doLogout = true;
        this.userName = userName;
        this.password = password;
    }

    public boolean isDoLogout() {
        return doLogout;
    }

    public void setDoLogout(boolean doLogout) {
        this.doLogout = doLogout;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
