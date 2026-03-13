package com.optimize.land.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.land.OptimizeLandRegApplication;
import com.optimize.land.model.dto.*;
import com.optimize.land.model.enumeration.ActorType;
import com.optimize.land.model.enumeration.RoleActor;
import com.optimize.land.model.enumeration.PrivateEntityType;
import com.optimize.land.model.enumeration.PublicEntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;

import org.springframework.test.context.TestPropertySource;
import org.springframework.kafka.test.context.EmbeddedKafka;
import com.optimize.common.securities.security.services.UserAccountService;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;

@SpringBootTest(classes = OptimizeLandRegApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9093", "port=9093" })
@DirtiesContext
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.kafka.bootstrap-servers=localhost:9093",
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

    // Must start with "data:image/XXX;base64," and be longer than 1040 chars.
    // The prefix "data:image/png;base64," is 22 chars long (comma at index 21/22).
    // Let's construct a valid dummy string.
    private static final String DUMMY_BASE64_IMAGE;

    static {
        String header = "data:image/png;base64,"; // Comma is at index 21 (0-based)
        // Generate enough characters to pass the > 1040 check.
        // 1041 - 22 = 1019 chars needed. We'll add 1050 'A's to be safe.
        String body = String.join("", Collections.nCopies(1050, "A"));
        DUMMY_BASE64_IMAGE = header + body;
    }

    @MockitoBean
    private UserAccountService userAccountService;

    @MockitoBean
    private com.optimize.common.securities.service.DeploymentLicenceService deploymentLicenceService;

    @MockitoBean
    private com.optimize.common.securities.security.services.RefreshTokenService refreshTokenService;

    @MockitoBean
    private com.optimize.common.securities.config.Initializer initializer;

    @MockitoBean
    private com.optimize.common.securities.security.services.UserService userService;
    
    @MockitoBean
    private com.optimize.land.service.ActorService actorService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        com.optimize.common.securities.models.User mockUser = new com.optimize.common.securities.models.User("Test", "User", "MALE", "test@test.com", "12345678", "testuser", "password");
        org.mockito.Mockito.when(userService.getCurrentUser()).thenReturn(mockUser);
        org.mockito.Mockito.when(actorService.register(org.mockito.ArgumentMatchers.any(ActorDto.class))).thenReturn(String.valueOf(1L));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testGetAllActors() throws Exception {
        mockMvc.perform(get("/land-reg/api/v1/actors/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateActor_PhysicalPerson_WithIdentificationDoc() throws Exception {
        ActorDto actorDto = new ActorDto();
        actorDto.setUin("1234567890123");
        actorDto.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actorDto.setType(ActorType.PHYSICAL_PERSON);
        
        PersonDto person = new PersonDto();
        person.setFirstname("John");
        person.setLastname("Doe");
        
        IdentificationDocDto doc = new IdentificationDocDto();
        doc.setIdentificationDocType("CNI");
        doc.setIdentificationDocNumber("12345");
        doc.setIdentificationDocPhoto(DUMMY_BASE64_IMAGE);
        person.setIdentificationDoc(doc);
        
        actorDto.setPhysicalPerson(person);

        mockMvc.perform(post("/land-reg/api/v1/actors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actorDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateActor_PhysicalPerson_WithoutIdentificationDoc() throws Exception {
        ActorDto actorDto = new ActorDto();
        actorDto.setUin("1234567890123");
        actorDto.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actorDto.setType(ActorType.PHYSICAL_PERSON);
        
        PersonDto person = new PersonDto();
        person.setFirstname("John");
        person.setLastname("Doe");
        person.setIdentificationDoc(null);
        
        actorDto.setPhysicalPerson(person);

        mockMvc.perform(post("/land-reg/api/v1/actors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actorDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateActor_InformalGroup() throws Exception {
        ActorDto actorDto = new ActorDto();
        actorDto.setUin("1234567890124");
        actorDto.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actorDto.setType(ActorType.INFORMAL_GROUP);
        
        InformalGroupDto informalGroup = new InformalGroupDto();
        informalGroup.setGroupName("Test Group");
        informalGroup.setGroupType("ASSOCIATION");
        informalGroup.setMandatePhoto(DUMMY_BASE64_IMAGE);
        actorDto.setInformalGroup(informalGroup);

        mockMvc.perform(post("/land-reg/api/v1/actors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actorDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateActor_PrivateLegalEntity_WithIdentificationDoc() throws Exception {
        ActorDto actorDto = new ActorDto();
        actorDto.setUin("1234567890125");
        actorDto.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actorDto.setType(ActorType.PRIVATE_LEGAL_ENTITY);
        
        PrivateLegalEntityDto privateEntity = new PrivateLegalEntityDto();
        privateEntity.setCompanyName("Test Company");
        privateEntity.setEntityType(PrivateEntityType.COOPERATIVE_AGRICOLE);
        
        IdentificationDocDto doc = new IdentificationDocDto();
        doc.setIdentificationDocType("RCCM");
        doc.setIdentificationDocNumber("67890");
        doc.setIdentificationDocPhoto(DUMMY_BASE64_IMAGE);
        privateEntity.setIdentificationDoc(doc);
        
        actorDto.setPrivateLegalEntity(privateEntity);

        mockMvc.perform(post("/land-reg/api/v1/actors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actorDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateActor_PrivateLegalEntity_WithoutIdentificationDoc() throws Exception {
        ActorDto actorDto = new ActorDto();
        actorDto.setUin("1234567890125");
        actorDto.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actorDto.setType(ActorType.PRIVATE_LEGAL_ENTITY);
        
        PrivateLegalEntityDto privateEntity = new PrivateLegalEntityDto();
        privateEntity.setCompanyName("Test Company");
        privateEntity.setEntityType(PrivateEntityType.COOPERATIVE_AGRICOLE);
        privateEntity.setIdentificationDoc(null);

        actorDto.setPrivateLegalEntity(privateEntity);

        mockMvc.perform(post("/land-reg/api/v1/actors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actorDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateActor_PublicLegalEntity() throws Exception {
        ActorDto actorDto = new ActorDto();
        actorDto.setUin("1234567890126");
        actorDto.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actorDto.setType(ActorType.PUBLIC_LEGAL_ENTITY);
        
        PublicLegalEntityDto publicEntity = new PublicLegalEntityDto();
        publicEntity.setName("Public Entity");
        publicEntity.setPublicEntityType(PublicEntityType.ETABLISSEMENT_PUBLIC);
        actorDto.setPublicLegalEntity(publicEntity);

        mockMvc.perform(post("/land-reg/api/v1/actors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actorDto)))
                .andExpect(status().isCreated());
    }
}
