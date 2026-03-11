package com.optimize.land;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = OptimizeLandRegApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.kafka.bootstrap-servers=localhost:9092",
    "spring.flyway.enabled=false",
    "jakarta.persistence.jdbc.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.jpa.properties.jakarta.persistence.jdbc.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "bezkoder.app.jwtSecret=testjwtsecrettestjwtsecrettestjwtsecrettestjwtsecret",
    "bezkoder.app.jwtExpirationMs=86400000",
    "bezkoder.app.jwtRefreshExpirationMs=86400000",
    "security.licence.prod.active=0",
    "security.licence.prod.society=TestSociety",
    "optimise.app.host.master=localhost",
    "lang-reg.kafka.config.num-partitions.afis-master-topic=1",
    "lang-reg.kafka.config.replication-factor.afis-master-topic=1",
    "lang-reg.kafka.config.num-partitions.afis-matcher-topic=1",
    "lang-reg.kafka.config.replication-factor.afis-matcher-topic=1",
    "lang-reg.kafka.config.num-partitions.afis-matcher-result-topic=1",
    "lang-reg.kafka.config.replication-factor.afis-matcher-result-topic=1",
    "lang-reg.kafka.config.num-partitions.afis-master-feedback-topic=1",
    "lang-reg.kafka.config.replication-factor.afis-master-feedback-topic=1"
})
class OptimizeLandRegApplicationTests {

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.optimize.common.securities.security.services.UserAccountService userAccountService;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.optimize.common.securities.service.DeploymentLicenceService deploymentLicenceService;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.optimize.common.securities.security.services.RefreshTokenService refreshTokenService;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.optimize.common.securities.config.Initializer initializer;

	@Test
	void contextLoads() {
	}

}
