package com.ecobridge.fcm.server.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

@Configuration
public class JasyptConfig {

    @Autowired
    private Environment env;

    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        String jasyptEnvName = env.getProperty("jasypt.encryptor.env.name", "JASYPT_ENCRYPTOR_PASSWORD");
        String jasyptPassword = env.getProperty(jasyptEnvName);
        if (jasyptPassword == null) {
            throw new IllegalStateException(jasyptEnvName + " environment variable not found");
        }
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(jasyptPassword);
        config.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }
}
