package org.bahmni_avni_integration.migrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BahmniConfig {
    @Value("${openmrs.mysql.user}")
    private String openMrsMySqlUser;

    @Value("${openmrs.mysql.password}")
    private String openMrsMySqlPassword;

    @Value("${openmrs.mysql.database}")
    private String openMrsMySqlDatabase;

    @Value("${openmrs.mysql.server.from.ssh_host}")
    private String openMrsMySqlServerFromSSHHost;

    @Value("${openmrs.mysql.port}")
    private int openMrsMySqlPort;

    @Value("${openmrs.server.ssh.local.port}")
    private int localPort;
    @Value("${openmrs.server.ssh.host}")
    private String sshHost;
    @Value("${openmrs.server.ssh.host.port}")
    private int sshPort;
    @Value("${openmrs.server.ssh.user}")
    private String sshUser;
    @Value("${openmrs.server.ssh.private.key}")
    private String sshPrivateKey;

    public String getOpenMrsMySqlUser() {
        return openMrsMySqlUser;
    }

    public String getOpenMrsMySqlPassword() {
        return openMrsMySqlPassword;
    }

    public String getOpenMrsMySqlDatabase() {
        return openMrsMySqlDatabase;
    }

    public String getOpenMrsMySqlServerFromSSHHost() {
        return openMrsMySqlServerFromSSHHost;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getSshHost() {
        return sshHost;
    }

    public int getSshPort() {
        return sshPort;
    }

    public String getSshUser() {
        return sshUser;
    }

    public int getOpenMrsMySqlPort() {
        return openMrsMySqlPort;
    }

    public String getSshPrivateKey() {
        return sshPrivateKey;
    }
}