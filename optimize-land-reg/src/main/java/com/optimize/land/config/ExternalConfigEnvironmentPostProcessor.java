package com.optimize.land.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class ExternalConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ExternalConfigEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String appName = "optimize-land-reg";
        String userHome = System.getProperty("user.home");

        File configDir = new File(userHome, "." + appName + File.separator + "config");
        File externalConfigFile = new File(configDir, "application.yml");

        if (!externalConfigFile.exists()) {
            // Spring properties can have profiles in spring.profiles.active
            String activeProfilesProp = environment.getProperty("spring.profiles.active");
            String profileToCopy = null;

            if (activeProfilesProp != null && !activeProfilesProp.isEmpty()) {
                List<String> activeProfiles = Arrays.asList(activeProfilesProp.split(","));
                if (!activeProfiles.isEmpty()) {
                    profileToCopy = activeProfiles.get(0).trim();
                }
            } else {
                String[] activeProfiles = environment.getActiveProfiles();
                if (activeProfiles.length > 0) {
                    profileToCopy = activeProfiles[0];
                } else {
                    String[] defaultProfiles = environment.getDefaultProfiles();
                    if (defaultProfiles.length > 0) {
                        profileToCopy = defaultProfiles[0];
                    }
                }
            }

            String sourceFileName = (profileToCopy != null && !profileToCopy.isEmpty())
                    ? "application-" + profileToCopy + ".yml"
                    : "application.yml";

            try {
                ClassPathResource resource = new ClassPathResource(sourceFileName);
                if (!resource.exists()) {
                    sourceFileName = "application.yml";
                    resource = new ClassPathResource(sourceFileName);
                }

                if (resource.exists()) {
                    if (!configDir.exists()) {
                        configDir.mkdirs();
                    }

                    try (InputStream inputStream = resource.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(externalConfigFile)) {
                        FileCopyUtils.copy(inputStream, outputStream);
                        log.info("Successfully copied {} to {}", sourceFileName, externalConfigFile.getAbsolutePath());
                    }
                }
            } catch (IOException e) {
                log.error("Error while copying configuration file: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
