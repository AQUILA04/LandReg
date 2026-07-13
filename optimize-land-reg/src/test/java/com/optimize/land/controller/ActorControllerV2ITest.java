package com.optimize.land.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.common.entities.config.CustomMessageSource;
import com.optimize.land.OptimizeLandRegApplication;
import com.optimize.land.model.dto.PersonDto;
import com.optimize.land.model.dto.v2.ActorDtoV2;
import com.optimize.land.model.dto.v2.FingerprintStoreV2Dto;
import com.optimize.land.model.enumeration.ActorType;
import com.optimize.land.model.enumeration.Finger;
import com.optimize.land.model.enumeration.HandType;
import com.optimize.land.model.enumeration.RoleActor;
import com.optimize.land.repository.ActorRepository;
import com.optimize.land.service.ActorService;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = OptimizeLandRegApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9095", "port=9095" })
@DirtiesContext
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdbv2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.kafka.bootstrap-servers=localhost:9095",
    "spring.flyway.enabled=false",
    "jakarta.persistence.jdbc.url=jdbc:h2:mem:testdbv2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.jpa.properties.jakarta.persistence.jdbc.url=jdbc:h2:mem:testdbv2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "bezkoder.app.jwtSecret=testjwtsecrettestjwtsecrettestjwtsecrettestjwtsecret",
    "bezkoder.app.jwtExpirationMs=86400000",
    "bezkoder.app.jwtRefreshExpirationMs=86400000",
    "security.licence.prod.active=0",
    "security.licence.prod.society=TestSociety",
    "landreg.storage.enabled=false",
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
class ActorControllerV2ITest {

    private static final String RID_RESPONSE = "{\"rid\":\"RID-V2-001\"}";

    @MockitoBean
    private com.optimize.common.securities.security.services.UserAccountService userAccountService;

    @MockitoBean
    private com.optimize.common.securities.service.DeploymentLicenceService deploymentLicenceService;

    @MockitoBean
    private com.optimize.common.securities.security.services.RefreshTokenService refreshTokenService;

    @MockitoBean
    private com.optimize.common.securities.config.Initializer initializer;

    @MockitoBean
    private com.optimize.common.securities.security.services.UserService userService;

    @MockitoBean
    private ActorService actorService;

    @MockitoBean
    private CustomMessageSource messageSource;

    @MockitoBean
    private ActorRepository actorRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws com.fasterxml.jackson.core.JsonProcessingException {
        com.optimize.common.securities.models.User mockUser = new com.optimize.common.securities.models.User(
            "Test",
            "User",
            "MALE",
            "test@test.com",
            "12345678",
            "testuser",
            "password"
        );
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(messageSource.getMessage(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(actorService.getRepository()).thenReturn(actorRepository);
        when(actorService.registerV2(any(), anyList())).thenReturn(RID_RESPONSE);
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "USER" })
    void registerV2_multipart_returnsCreated() throws Exception {
        ActorDtoV2 actorDto = buildPhysicalPersonActor();
        byte[] actorJson = objectMapper.writeValueAsBytes(actorDto);

        MockMultipartFile actorPart = new MockMultipartFile(
            "actor",
            "actor.json",
            MediaType.APPLICATION_JSON_VALUE,
            actorJson
        );
        MockMultipartFile fingerprintPart = new MockMultipartFile(
            "fingerprints",
            "left-thumb.jpg",
            "image/jpeg",
            "fingerprint-binary".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc
            .perform(multipart("/land-reg/api/v2/actors").file(actorPart).file(fingerprintPart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data").value(RID_RESPONSE));

        verify(actorService).registerV2(any(ActorDtoV2.class), anyList());
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "USER" })
    void registerV2_multipart_acceptsMultipleFingerprintFiles() throws Exception {
        ActorDtoV2 actorDto = buildPhysicalPersonActor();
        FingerprintStoreV2Dto second = new FingerprintStoreV2Dto();
        second.setFingerStr("Index Gauche");
        second.setHandType(HandType.LEFT);
        second.setFingerName(Finger.INDEX);
        Set<FingerprintStoreV2Dto> stores = new LinkedHashSet<>(actorDto.getFingerprintStores());
        stores.add(second);
        actorDto.setFingerprintStores(stores);

        MockMultipartFile actorPart = new MockMultipartFile(
            "actor",
            "actor.json",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(actorDto)
        );
        MockMultipartFile thumb = new MockMultipartFile(
            "fingerprints",
            "thumb.jpg",
            "image/jpeg",
            "thumb".getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile index = new MockMultipartFile(
            "fingerprints",
            "index.jpg",
            "image/jpeg",
            "index".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc
            .perform(multipart("/land-reg/api/v2/actors").file(actorPart).file(thumb).file(index))
            .andExpect(status().isCreated());

        verify(actorService).registerV2(any(ActorDtoV2.class), anyList());
    }

    private ActorDtoV2 buildPhysicalPersonActor() {
        ActorDtoV2 actorDto = new ActorDtoV2();
        actorDto.setSynchroBatchNumber("BATCH-V2-001");
        actorDto.setSynchroPacketNumber("1");
        actorDto.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actorDto.setType(ActorType.PHYSICAL_PERSON);

        PersonDto person = new PersonDto();
        person.setFirstname("Jane");
        person.setLastname("Doe");
        actorDto.setPhysicalPerson(person);

        FingerprintStoreV2Dto fingerprint = new FingerprintStoreV2Dto();
        fingerprint.setFingerStr("Pouce Gauche");
        fingerprint.setHandType(HandType.LEFT);
        fingerprint.setFingerName(Finger.THUMB);
        actorDto.setFingerprintStores(Set.of(fingerprint));
        return actorDto;
    }
}
