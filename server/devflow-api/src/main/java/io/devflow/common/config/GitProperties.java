package io.devflow.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "app.git")
public class GitProperties {

    private String storagePath;

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public Path getStorageLocation() {
        if (storagePath == null || storagePath.trim().isEmpty()) {
            storagePath = System.getProperty("user.home") + "/.devflow/git-storage";
        }
        return Path.of(storagePath);
    }
}
