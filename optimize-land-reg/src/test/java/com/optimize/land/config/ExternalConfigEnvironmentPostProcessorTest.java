package com.optimize.land.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.StandardEnvironment;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExternalConfigEnvironmentPostProcessorTest {

    private String originalUserHome;
    private File tempDir;

    @BeforeEach
    public void setUp() throws Exception {
        originalUserHome = System.getProperty("user.home");
        tempDir = Files.createTempDirectory("user_home_mock").toFile();
        System.setProperty("user.home", tempDir.getAbsolutePath());
    }

    @AfterEach
    public void tearDown() {
        if (originalUserHome != null) {
            System.setProperty("user.home", originalUserHome);
        }
        deleteDirectory(tempDir);
    }

    private void deleteDirectory(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteDirectory(f);
                }
            }
        }
        file.delete();
    }

    @Test
    public void testPostProcessEnvironment() {
        ExternalConfigEnvironmentPostProcessor processor = new ExternalConfigEnvironmentPostProcessor();
        processor.postProcessEnvironment(new StandardEnvironment(), new SpringApplication());

        File expectedFile = new File(tempDir, ".optimize-land-reg/config/application.yml");
        assertTrue(expectedFile.exists(), "The configuration file should be copied to the mocked user.home");
    }
}
