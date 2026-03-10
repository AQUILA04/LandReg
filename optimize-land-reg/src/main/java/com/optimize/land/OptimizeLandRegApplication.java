package com.optimize.land;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.optimize.common.securities.config.DefaultSecurityAuditorAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.optimize")
@ConfigurationPropertiesScan(basePackages = {"com.optimize.land.jms.config"})
@EnableFeignClients
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class OptimizeLandRegApplication {

	public static void main(String[] args) {
        ConfigurableApplicationContext app = new SpringApplicationBuilder(
                OptimizeLandRegApplication.class)
                .build().run(args);
        Environment env = app.getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        log.info("""
            
            ----------------------------------------------------------
            Application  '{}' is running!
            Version:      {}
            Access URLs:
            Local:        {}://localhost:{}
            Profile(s):   {}
            (c) LandReg Powered by Optimize-tech. All rights reserved.
            ----------------------------------------------------------
            """,
                env.getProperty("spring.application.name"),
                env.getProperty("spring.application.version"),
                protocol,
                env.getProperty("server.port"),
                env.getActiveProfiles());
	}

    @Bean
    AuditorAware<String> auditorProvider() {
        return new DefaultSecurityAuditorAware();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        mapper.findAndRegisterModules();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
