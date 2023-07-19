package org.avni_integration_service.goonj.config;

import org.avni_integration_service.integration_data.context.ContextIntegrationSystem;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.config.IntegrationSystemConfigCollection;
import org.springframework.util.StringUtils;

public class GoonjConfig {
    private final IntegrationSystemConfigCollection integrationSystemConfigCollection;
    private final ContextIntegrationSystem integrationSystem;

    public GoonjConfig(IntegrationSystemConfigCollection integrationSystemConfigCollection, IntegrationSystem integrationSystem) {
        this.integrationSystemConfigCollection = integrationSystemConfigCollection;
        this.integrationSystem = new ContextIntegrationSystem(integrationSystem);
    }

    private String getStringConfigValue(String key, String defaultValue) {
        String configValue = integrationSystemConfigCollection.getConfigValue(key);
        return StringUtils.hasLength(configValue) ? configValue : defaultValue;
    }

    public String getSalesForceAuthUrl() {
        return getStringConfigValue("sales_force_auth_url", "https://test.salesforce.com/services/oauth2/token");
    }

    public String getLoginUserName() {
        return getStringConfigValue("sales_force_user", "goonj-main-dummy");
    }

    public String getLoginPassword() {
        return getStringConfigValue("sales_force_password", "goonj-main-dummy");
    }

    public String getClientId() {
        return getStringConfigValue("sales_force_client_id", "goonj-main-dummy");
    }

    public String getClientSecret() {
        return getStringConfigValue("sales_force_client_secret", "goonj-main-dummy");
    }

    public String getAppUrl() {
        return getStringConfigValue("sales_force_app_url", "https://goonj--goonjstage.my.salesforce.com/services/apexrest/v1");
    }

    public String getMediaUrl() {
        return getStringConfigValue("sales_force_media_url_prefix", "https://staging.avniproject.org/web/media?url=");
    }

    public String getApiUrl() {
        return getStringConfigValue("avni_api_url", "dummy");
    }

    public String getAvniImplUser() {
        return getStringConfigValue("avni_user", "dummy");
    }

    public String getImplPassword() {
        return getStringConfigValue("avni_password", "dummy");
    }

    public boolean getAuthEnabled() {
        return Boolean.parseBoolean(getStringConfigValue("avni_auth_enabled", "true"));
    }

    public String getTasks() {
        return getStringConfigValue("int_tasks", "all");
    }

    public boolean getDeleteAndRecreateDispatchReceipt() {
        return Boolean.parseBoolean(getStringConfigValue("recreate_dispatch_receipt_enabled", "false"));
    }

    public ContextIntegrationSystem getIntegrationSystem() {
        return integrationSystem;
    }
    public Boolean getBypassErrors() {
        return Boolean.parseBoolean(getStringConfigValue("goonj_bypass_errors", "true"));
    }
}
