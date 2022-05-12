package org.avni_integration_service.bahmni.client;

public class WebClientsException extends RuntimeException {
    private int statusCode;

    public WebClientsException(Throwable cause) {
        super(cause);
    }

    public WebClientsException(String message) {
        super(message);
    }

    public static WebClientsException CustomError(int statusCode, String message) {
        WebClientsException webClientsException = new WebClientsException(message);
        webClientsException.statusCode = statusCode;
        return webClientsException;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
