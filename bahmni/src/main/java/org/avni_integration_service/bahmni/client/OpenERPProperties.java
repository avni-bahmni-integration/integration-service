package org.avni_integration_service.bahmni.client;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public interface OpenERPProperties {
    public int getConnectionTimeoutInMilliseconds();
    public int getReplyTimeoutInMilliseconds();
}
