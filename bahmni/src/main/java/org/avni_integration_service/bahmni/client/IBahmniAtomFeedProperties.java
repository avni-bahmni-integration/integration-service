package org.avni_integration_service.bahmni.client;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public interface IBahmniAtomFeedProperties {
    int getConnectionTimeoutInMilliseconds();
    int getReplyTimeoutInMilliseconds();
}
