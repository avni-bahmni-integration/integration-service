package org.avni_integration_service.goonj.config;

import org.avni_integration_service.avni.client.AvniSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GoonjAvniSessionFactory {
    private final GoonjContextProvider goonjContextProvider;

    @Autowired
    public GoonjAvniSessionFactory(GoonjContextProvider goonjContextProvider) {
        this.goonjContextProvider = goonjContextProvider;
    }

    public AvniSession createSession() {
        GoonjConfig goonjConfig = goonjContextProvider.get();
        return new AvniSession(goonjConfig.getApiUrl(), goonjConfig.getAvniImplUser(), goonjConfig.getImplPassword(), goonjConfig.getAuthEnabled());
    }
}
