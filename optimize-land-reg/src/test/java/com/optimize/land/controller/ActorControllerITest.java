package com.optimize.land.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.land.OptimizeLandRegApplication;
import com.optimize.land.model.dto.*;
import com.optimize.land.model.enumeration.*;
import com.optimize.land.repository.ActorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;

import org.springframework.test.context.TestPropertySource;
import org.springframework.kafka.test.context.EmbeddedKafka;
import com.optimize.common.securities.security.services.UserAccountService;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Collections;
import com.optimize.land.model.enumeration.Sex;
import com.optimize.land.model.enumeration.MaritalStatus;
import com.optimize.land.model.enumeration.PrivateEntityType;
import com.optimize.land.model.enumeration.PublicEntityType;

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

    @MockitoBean(extraInterfaces = {})
    private com.optimize.land.service.ActorService actorService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ActorRepository actorRepository;

    @MockitoBean
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        com.optimize.common.securities.models.User mockUser = new com.optimize.common.securities.models.User("Test", "User", "MALE", "test@test.com", "12345678", "testuser", "password");
        org.mockito.Mockito.when(userService.getCurrentUser()).thenReturn(mockUser);
        Mockito.when(actorService.getRepository()).thenReturn(actorRepository);
        org.mockito.Mockito.when(actorService.register(org.mockito.ArgumentMatchers.any(ActorDto.class))).thenReturn(String.valueOf(1L));
        Mockito.doCallRealMethod().when(actorService).getUINDetails(Mockito.any(UINWrapper.class));
        Mockito.doCallRealMethod().when(actorService).search(Mockito.any(String.class), Mockito.any(Pageable.class));
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


    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser")
    void testGetUIN_ExistingUIN_ReturnsActor() throws Exception {
        createTestActors();

        mockMvc.perform(get("/land-reg/api/v1/actors/uin/UIN001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uin").value("UIN001"))
                .andExpect(jsonPath("$.data.name").value("Jean Dupont"))
                .andExpect(jsonPath("$.data.firstname").value("Jean"))
                .andExpect(jsonPath("$.data.lastname").value("Dupont"))
                .andExpect(jsonPath("$.data.type").value("PHYSICAL_PERSON"))
                .andExpect(jsonPath("$.data.role").value("OWNER_OR_REPRESENTATIVE"))
                .andExpect(jsonPath("$.data.contact").value("1234567890"))
                .andExpect(jsonPath("$.data.address").value("123 Rue de Paris"))
                .andExpect(jsonPath("$.data.email").value("jean.dupont@example.com"));

    }


    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testGetUIN_ExistingUIN_InformalGroup_ReturnsActor() throws Exception {
        createTestActors();

        mockMvc.perform(get("/land-reg/api/v1/actors/uin/UIN003")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uin").value("UIN003"))
                .andExpect(jsonPath("$.data.name").value("Association des Agriculteurs"))
                .andExpect(jsonPath("$.data.type").value("INFORMAL_GROUP"))
                .andExpect(jsonPath("$.data.contact").value("5555555555"))
                .andExpect(jsonPath("$.data.email").value("contact@association.com"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testGetUIN_ExistingUIN_PrivateLegalEntity_ReturnsActor() throws Exception {
        createTestActors();

        mockMvc.perform(get("/land-reg/api/v1/actors/uin/UIN004")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uin").value("UIN004"))
                .andExpect(jsonPath("$.data.name").value("Société Fermière"))
                .andExpect(jsonPath("$.data.type").value("PRIVATE_LEGAL_ENTITY"))
                .andExpect(jsonPath("$.data.contact").value("4444444444"))
                .andExpect(jsonPath("$.data.identificationDocType").value("RCCM"))
                .andExpect(jsonPath("$.data.identificationDocNumber").value("RC123456"));
    }


    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testGetUIN_ExistingUIN_PublicLegalEntity_ReturnsActor() throws Exception {
        createTestActors();

        mockMvc.perform(get("/land-reg/api/v1/actors/uin/UIN005")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uin").value("UIN005"))
                .andExpect(jsonPath("$.data.name").value("Mairie de Paris"))
                .andExpect(jsonPath("$.data.type").value("PUBLIC_LEGAL_ENTITY"))
                .andExpect(jsonPath("$.data.contact").value("3333333333"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testGetUIN_NonExistingUIN_ReturnsNull() throws Exception {
        createTestActors();

        mockMvc.perform(get("/land-reg/api/v1/actors/uin/NONEXISTENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testGetUIN_EmptyUIN_ReturnsNull() throws Exception {
        createTestActors();

        mockMvc.perform(get("/land-reg/api/v1/actors/uin/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_ByName_ReturnsMatchingActors() throws Exception {
        createTestActors();


        SearchDto searchDto = new SearchDto("Dupont");

        var result = mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("Search Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_ByName_CaseInsensitive() throws Exception {
        createTestActors();

        SearchDto searchDto = new SearchDto("dupont");

        mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].uin").value("UIN001"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_ByUIN_ReturnsMatchingActor() throws Exception {
        createTestActors();

        SearchDto searchDto = new SearchDto("UIN002");

        mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].uin").value("UIN002"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_ByPhone_ReturnsMatchingActor() throws Exception {
        createTestActors();

        SearchDto searchDto = new SearchDto("9876543210");

        mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].uin").value("UIN002"));
    }


    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_ByOperatorAgent_ReturnsMatchingActors() throws Exception {
        createTestActors();

        SearchDto searchDto = new SearchDto("agent1");

        mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isNotEmpty());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_ByRID_ReturnsMatchingActor() throws Exception {
        createTestActors();

        SearchDto searchDto = new SearchDto("RID003");

        mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].uin").value("UIN003"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_ByCompanyName_ReturnsMatchingActor() throws Exception {
        createTestActors();

        SearchDto searchDto = new SearchDto("Société Fermière");

        mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].uin").value("UIN004"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_NoResults_ReturnsEmptyPage() throws Exception {
        createTestActors();

        SearchDto searchDto = new SearchDto("NONEXISTENTKEYWORD12345");

        mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_EmptyKeyword_ReturnsEmptyPage() throws Exception {
        createTestActors();

        SearchDto searchDto = new SearchDto("");

        mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }
    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_NullKeyword_ReturnsEmptyPage() throws Exception {
        createTestActors();

        SearchDto searchDto = new SearchDto(null);

        mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ValidationErrorDTO(field=keyword, message=Le mot clé de la recherche est obligatoire !)"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_PartialMatch_ReturnsMatchingActors() throws Exception {
        createTestActors();

        SearchDto searchDto = new SearchDto("Jean");

        mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].uin").value("UIN001"))
                .andExpect(jsonPath("$.data.content[0].name").value("Jean Dupont"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearch_MultipleMatches_ReturnsAllMatchingActors() throws Exception {
        createTestActors();

        SearchDto searchDto = new SearchDto("agent1");

        mockMvc.perform(post("/land-reg/api/v1/actors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isNotEmpty());
    }

    private void createTestActors() throws Exception {
        actorRepository.deleteAll();

        // =====================================================
        // ACTOR 1: Personne physique (Jean Dupont)
        // =====================================================
        com.optimize.land.model.entity.Actor actor1 = new com.optimize.land.model.entity.Actor();
        actor1.setUin("UIN001");
        actor1.setType(ActorType.PHYSICAL_PERSON);
        actor1.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actor1.setRegistrationStatus(RegistrationStatus.ACTOR);
        actor1.setName("Jean Dupont");
        actor1.setPhone("1234567890");
        actor1.setOperatorAgent("agent1");
        actor1.setSynchroBatchNumber("BATCH001");
        actor1.setSynchroPacketNumber("PACKET001");

        com.optimize.land.model.entity.Person person1 = new com.optimize.land.model.entity.Person();
        person1.setFirstname("Jean");
        person1.setLastname("Dupont");
        person1.setSex(Sex.MASCULIN);
        person1.setMaritalStatus(MaritalStatus.MARIE);
        person1.setBirthDate(LocalDate.of(1980, 1, 15));
        person1.setPlaceOfBirth("Paris");
        person1.setNationality("Française");
        person1.setProfession("Agriculteur");
        person1.setAddress("123 Rue de Paris");
        person1.setPrimaryPhone("1234567890");
        person1.setEmail("jean.dupont@example.com");
        person1.setHasIDDoc(Boolean.TRUE);  // Ceci déclenche @ConditionalNotNull

        // Créer un IdentificationDoc COMPLET et VALIDE
        com.optimize.land.model.entity.IdentificationDoc idDoc1 = new com.optimize.land.model.entity.IdentificationDoc();
        idDoc1.setIdentificationDocType("CNI");
        idDoc1.setIdentificationDocNumber("CNI123456");
        idDoc1.setIdentificationDocPhoto(new byte[]{1, 2, 3});  // Non vide pour éviter isNull() = true
        idDoc1.setIdentificationDocPhotoContentType("image/jpeg");
        // IMPORTANT: S'assurer que tous les champs sont remplis pour que isNull() retourne false
        person1.setIdentificationDoc(idDoc1);

        person1.setRegistrationStatus(RegistrationStatus.ACTOR);
        person1.setRole(RoleActor.SURVEYOR.name());

        actor1.setPhysicalPerson(person1);
        actorRepository.save(actor1);

        // =====================================================
        // ACTOR 2: Personne physique (Marie Durand) - SANS document d'identité
        // =====================================================
        com.optimize.land.model.entity.Actor actor2 = new com.optimize.land.model.entity.Actor();
        actor2.setUin("UIN002");
        actor2.setType(ActorType.PHYSICAL_PERSON);
        actor2.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actor2.setRegistrationStatus(RegistrationStatus.ACTOR);
        actor2.setName("Marie Durand");
        actor2.setPhone("9876543210");
        actor2.setOperatorAgent("agent1");
        actor2.setSynchroBatchNumber("BATCH001");
        actor2.setSynchroPacketNumber("PACKET002");

        com.optimize.land.model.entity.Person person2 = new com.optimize.land.model.entity.Person();
        person2.setFirstname("Marie");
        person2.setLastname("Durand");
        person2.setSex(Sex.FEMININ);
        person2.setMaritalStatus(MaritalStatus.CELIBATAIRE);
        person2.setBirthDate(LocalDate.of(1985, 5, 20));
        person2.setPlaceOfBirth("Lyon");
        person2.setNationality("Française");
        person2.setProfession("Enseignante");
        person2.setAddress("456 Avenue Victor Hugo");
        person2.setPrimaryPhone("9876543210");
        person2.setEmail("marie.durand@example.com");
        person2.setHasIDDoc(Boolean.FALSE);  // Pas de document d'identité
        person2.setIdentificationDoc(null);  // Doit être null car hasIDDoc = false
        person2.setRegistrationStatus(RegistrationStatus.ACTOR);
        person2.setRole(RoleActor.NOTABLE.name());

        actor2.setPhysicalPerson(person2);
        actorRepository.save(actor2);

        // =====================================================
        // ACTOR 3: Groupe informel
        // =====================================================
        com.optimize.land.model.entity.Actor actor3 = new com.optimize.land.model.entity.Actor();
        actor3.setUin("UIN003");
        actor3.setRid("RID003");
        actor3.setType(ActorType.INFORMAL_GROUP);
        actor3.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actor3.setRegistrationStatus(RegistrationStatus.ACTOR);
        actor3.setName("Association des Agriculteurs");
        actor3.setPhone("5555555555");
        actor3.setOperatorAgent("agent1");
        actor3.setSynchroBatchNumber("BATCH002");
        actor3.setSynchroPacketNumber("PACKET001");

        com.optimize.land.model.entity.InformalGroup group = new com.optimize.land.model.entity.InformalGroup();
        group.setGroupName("Association des Agriculteurs");
        group.setAddress("123 Rue de la Paix");
        group.setPhoneNumber("5555555555");
        group.setEmail("contact@association.com");
        group.setGroupType("ASSOCIATION");
        group.setRepresentativeFullname("Jean Représentant");
        group.setRepresentativeUIN("UIN001");
        // Mandat photo optionnel mais peut être null
        // group.setMandatePhoto(new byte[]{7, 8, 9});
        // group.setMandatePhotoContentType("image/jpeg");

        actor3.setInformalGroup(group);
        actorRepository.save(actor3);

        // =====================================================
        // ACTOR 4: Personne morale de droit privé AVEC document
        // =====================================================
        com.optimize.land.model.entity.Actor actor4 = new com.optimize.land.model.entity.Actor();
        actor4.setUin("UIN004");
        actor4.setType(ActorType.PRIVATE_LEGAL_ENTITY);
        actor4.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actor4.setRegistrationStatus(RegistrationStatus.ACTOR);
        actor4.setName("Société Fermière");
        actor4.setPhone("4444444444");
        actor4.setOperatorAgent("agent1");
        actor4.setSynchroBatchNumber("BATCH002");
        actor4.setSynchroPacketNumber("PACKET002");

        com.optimize.land.model.entity.PrivateLegalEntity privateEntity = new com.optimize.land.model.entity.PrivateLegalEntity();
        privateEntity.setCompanyName("Société Fermière");
        privateEntity.setAddress("456 Rue de l'Entreprise");
        privateEntity.setPhoneNumber("4444444444");
        privateEntity.setEmail("contact@fermiere.com");
        privateEntity.setEntityType(PrivateEntityType.ENTREPRISE);
        privateEntity.setMainActivity("Agriculture");
        privateEntity.setAcronym("SF");
        privateEntity.setCompanyCreatedDate(LocalDate.of(2010, 1, 1));
        privateEntity.setRepresentativeFullname("Marie Représentante");
        privateEntity.setRepresentativeUIN("UIN002");

        // Document d'identification complet pour l'entité privée
        com.optimize.land.model.entity.IdentificationDoc idDoc3 = new com.optimize.land.model.entity.IdentificationDoc();
        idDoc3.setIdentificationDocType("RCCM");
        idDoc3.setIdentificationDocNumber("RC123456");
        idDoc3.setIdentificationDocPhoto(new byte[]{10, 11, 12});
        idDoc3.setIdentificationDocPhotoContentType("image/jpeg");
        privateEntity.setIdentificationDoc(idDoc3);

        actor4.setPrivateLegalEntity(privateEntity);
        actorRepository.save(actor4);

        // =====================================================
        // ACTOR 5: Personne morale de droit public
        // =====================================================
        com.optimize.land.model.entity.Actor actor5 = new com.optimize.land.model.entity.Actor();
        actor5.setUin("UIN005");
        actor5.setType(ActorType.PUBLIC_LEGAL_ENTITY);
        actor5.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actor5.setRegistrationStatus(RegistrationStatus.ACTOR);
        actor5.setName("Mairie de Paris");
        actor5.setPhone("3333333333");
        actor5.setOperatorAgent("agent1");
        actor5.setSynchroBatchNumber("BATCH002");
        actor5.setSynchroPacketNumber("PACKET003");

        com.optimize.land.model.entity.PublicLegalEntity publicEntity = new com.optimize.land.model.entity.PublicLegalEntity();
        publicEntity.setName("Mairie de Paris");
        publicEntity.setPhoneNumber("3333333333");
        publicEntity.setPublicEntityType(PublicEntityType.ETABLISSEMENT_PUBLIC);
        actor5.setPublicLegalEntity(publicEntity);


        actorRepository.save(actor5);
    }
}
