package org.avni_integration_service.goonj.config;

import org.springframework.stereotype.Component;

// Using thread local in a component is not a recommended approach. Since GoonjConfig class is used in several places this is a simplest way to achieve this without change a lot of code.
@Component
public class GoonjContextProvider {
    private static ThreadLocal<GoonjConfig> goonjConfigs = new ThreadLocal<>();

    public void set(GoonjConfig goonjConfig) {
        goonjConfigs.set(goonjConfig);
    }

    public GoonjConfig get() {
        GoonjConfig goonjConfig = goonjConfigs.get();
        if (goonjConfig == null)
            throw new IllegalStateException("No Goonj config available. Have you called org.avni_integration_service.goonj.config.GoonjContextProvider.set.");
        return goonjConfig;
    }
}
