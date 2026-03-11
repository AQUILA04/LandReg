package com.optimize.land.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.land.OptimizeLandRegApplication;
import com.optimize.land.model.dto.ActorDto;
import com.optimize.land.model.enumeration.ActorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.mock.mockito.MockBean;
import javax.sql.DataSource;

import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import com.optimize.common.securities.security.services.UserAccountService;

@SpringBootTest(classes = OptimizeLandRegApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
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
    "spring.kafka.consumer.auto-offset-reset=earliest",
    "optimise.app.kafka.topics.afismaster-response=afismaster-response-test",
    "lang-reg.kafka.config.num-partitions.afis-master-topic=1",
    "lang-reg.kafka.config.replication-factor.afis-master-topic=1",
    "lang-reg.kafka.config.num-partitions.afis-matcher-topic=1",
    "lang-reg.kafka.config.replication-factor.afis-matcher-topic=1",
    "lang-reg.kafka.config.num-partitions.afis-matcher-result-topic=1",
    "lang-reg.kafka.config.replication-factor.afis-matcher-result-topic=1",
    "lang-reg.kafka.config.num-partitions.afis-master-feedback-topic=1",
    "lang-reg.kafka.config.replication-factor.afis-master-feedback-topic=1"
})
public class ActorControllerITest {

    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private com.optimize.common.securities.service.DeploymentLicenceService deploymentLicenceService;

    @MockBean
    private com.optimize.common.securities.security.services.RefreshTokenService refreshTokenService;

    @MockBean
    private com.optimize.common.securities.config.Initializer initializer;

    @MockBean
    private com.optimize.common.securities.security.services.UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        com.optimize.common.securities.models.User mockUser = new com.optimize.common.securities.models.User("Test", "User", "MALE", "test@test.com", "12345678", "testuser", "password");
        org.mockito.Mockito.when(userService.getCurrentUser()).thenReturn(mockUser);
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testGetAllActors() throws Exception {
        mockMvc.perform(get("/land-reg/api/v1/actors/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
