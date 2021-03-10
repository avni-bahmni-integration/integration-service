package org.bahmni_avni_integration.client.bahmni;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders extends HashMap<String, String> {

    public void addTo(HttpMessage httpMessage) {
        for (Entry<String, String> entry : this.entrySet()) {
            httpMessage.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
        }
    }

}
