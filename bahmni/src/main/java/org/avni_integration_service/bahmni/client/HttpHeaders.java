package org.avni_integration_service.bahmni.client;

import org.apache.http.HttpMessage;
import org.apache.http.message.BasicHeader;

import java.util.HashMap;

public class HttpHeaders extends HashMap<String, String> {

    public void addTo(HttpMessage httpMessage) {
        for (Entry<String, String> entry : this.entrySet()) {
            httpMessage.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
        }
    }

}
