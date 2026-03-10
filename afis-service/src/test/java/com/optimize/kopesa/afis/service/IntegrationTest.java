package com.optimize.kopesa.afis.service;

import com.optimize.kopesa.afis.service.config.AsyncSyncConfiguration;
import com.optimize.kopesa.afis.service.config.EmbeddedKafka;
import com.optimize.kopesa.afis.service.config.EmbeddedMongo;
import com.optimize.kopesa.afis.service.config.JacksonConfiguration;
import com.optimize.kopesa.afis.service.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { AfisServiceApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class }
)
@EmbeddedMongo
@EmbeddedKafka
public @interface IntegrationTest {
}
