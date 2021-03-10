package org.bahmni_avni_integration.client;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public interface OpenERPProperties {
    public int getConnectionTimeoutInMilliseconds();
    public int getReplyTimeoutInMilliseconds();
}
