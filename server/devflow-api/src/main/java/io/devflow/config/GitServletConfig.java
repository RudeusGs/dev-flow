package io.devflow.config;

import io.devflow.common.config.GitProperties;
import org.eclipse.jgit.http.server.GitServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class GitServletConfig {

    private final GitProperties gitProperties;

    public GitServletConfig(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Bean
    public ServletRegistrationBean<GitServlet> gitServletRegistrationBean() {
        GitServlet gitServlet = new GitServlet();
        File rootDir = gitProperties.getStorageLocation().toFile();
        
        gitServlet.setRepositoryResolver((req, name) -> {
            File gitDir = new File(rootDir, name);
            if (!gitDir.exists()) {
                throw new org.eclipse.jgit.errors.RepositoryNotFoundException(gitDir);
            }
            try {
                return org.eclipse.jgit.storage.file.FileRepositoryBuilder.create(gitDir);
            } catch (java.io.IOException e) {
                throw new org.eclipse.jgit.errors.RepositoryNotFoundException(gitDir, e);
            }
        });

        ServletRegistrationBean<GitServlet> reg = new ServletRegistrationBean<>(gitServlet, "/git/*");
        reg.setLoadOnStartup(1);
        return reg;
    }
}
