package com.optimize.land.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.land.OptimizeLandRegApplication;
import com.optimize.land.model.dto.BorderingDto;
import com.optimize.land.model.dto.CheckListOperationDto;
import com.optimize.land.model.dto.ConflictDto;
import com.optimize.land.model.dto.FindingDto;
import com.optimize.land.model.dto.SearchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
public class FindingControllerITest {

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
    private DataSource dataSource;
    @MockitoBean(extraInterfaces = {})
    private com.optimize.land.service.FindingService findingService;

    @Autowired
    private com.optimize.land.repository.FindingRepository findingRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private FindingDto findingDto;

    @BeforeEach
    void setUp() {
        com.optimize.common.securities.models.User mockUser = new com.optimize.common.securities.models.User("Test", "User", "MALE", "test@test.com", "12345678", "testuser", "password");
        when(userService.getCurrentUser()).thenReturn(mockUser);

        org.mockito.Mockito.when(findingService.getRepository()).thenReturn(findingRepository);
        org.mockito.Mockito.doCallRealMethod().when(findingService).search(org.mockito.Mockito.any(String.class), org.mockito.Mockito.any(org.springframework.data.domain.Pageable.class));

        findingDto = new FindingDto();
        findingDto.setNup("NUP123");
        findingDto.setCanton("Canton1");
        findingDto.setLocality("Locality1");
        findingDto.setUin("1234567890123"); 
        
        CheckListOperationDto checkList = new CheckListOperationDto();
        checkList.setMayorUIN("1000000000001");
        checkList.setTraditionalChiefUIN("1000000000002");
        checkList.setNotableUIN("1000000000003");
        checkList.setGeometerUIN("1000000000004");
        checkList.setOwnerUIN("1000000000005");
        checkList.setTopographerUIN("1000000000006");
        checkList.setSocialLandAgentUIN("1000000000007");
        Set<BorderingDto> borderings = new HashSet<>();
        checkList.setBorderingList(borderings);

        findingDto.setFirstCheckListOperation(checkList);
        findingDto.setLastCheckListOperation(checkList);
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateFinding_NoConflict() throws Exception {
        findingDto.setHasConflict(false);
        when(findingService.register(any(FindingDto.class))).thenReturn(1L);

        mockMvc.perform(post("/land-reg/api/v1/constatations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(findingDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateFinding_WithConflict_Success() throws Exception {
        findingDto.setHasConflict(true);
        ConflictDto conflict = new ConflictDto();
        conflict.setConflictObject("Land dispute");
        findingDto.setConflict(conflict);

        when(findingService.register(any(FindingDto.class))).thenReturn(1L);

        mockMvc.perform(post("/land-reg/api/v1/constatations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(findingDto)))
                .andExpect(status().isCreated());
    }
    
    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateFinding_WithConflict_MissingConflictData() throws Exception {
        findingDto.setHasConflict(true);
        findingDto.setConflict(null); // Missing mandatory conflict data

        mockMvc.perform(post("/land-reg/api/v1/constatations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(findingDto)))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateFinding_NoConflict() throws Exception {
        findingDto.setHasConflict(false);
        when(findingService.updateFinding(any(FindingDto.class))).thenReturn(1L);

        mockMvc.perform(put("/land-reg/api/v1/constatations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(findingDto)))
                .andExpect(status().isOk());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateFinding_WithConflict_Success() throws Exception {
        findingDto.setHasConflict(true);
        ConflictDto conflict = new ConflictDto();
        conflict.setConflictObject("Updated dispute");
        findingDto.setConflict(conflict);

        when(findingService.updateFinding(any(FindingDto.class))).thenReturn(1L);

        mockMvc.perform(put("/land-reg/api/v1/constatations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(findingDto)))
                .andExpect(status().isOk());
    }
    
    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateFinding_WithConflict_MissingConflictData() throws Exception {
        findingDto.setHasConflict(true);
        findingDto.setConflict(null); // Invalid state

        mockMvc.perform(put("/land-reg/api/v1/constatations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(findingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearchFinding_SingleMatch_ByNup() throws Exception {
        createTestFindings();
        SearchDto searchDto = new SearchDto("NUP002");

        mockMvc.perform(post("/land-reg/api/v1/constatations/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].nup").value("NUP002"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearchFinding_MultipleMatches_ByLocality() throws Exception {
        createTestFindings();
        SearchDto searchDto = new SearchDto("Tokoin");

        mockMvc.perform(post("/land-reg/api/v1/constatations/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearchFinding_CaseInsensitiveMatch_ByRegion() throws Exception {
        createTestFindings();
        SearchDto searchDto = new SearchDto("pLaTeaU"); // Match Plateaux

        mockMvc.perform(post("/land-reg/api/v1/constatations/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].region").value("Plateaux"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testuser", roles = {"USER"})
    void testSearchFinding_NoMatch() throws Exception {
        createTestFindings();
        SearchDto searchDto = new SearchDto("Inexistant12345");

        mockMvc.perform(post("/land-reg/api/v1/constatations/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    private void createTestFindings() throws Exception {
        findingRepository.deleteAll();

        com.optimize.land.model.entity.Finding f1 = new com.optimize.land.model.entity.Finding();
        f1.setNup("NUP001");
        f1.setRegion("Maritime");
        f1.setPrefecture("Golfe");
        f1.setCommune("Lome");
        f1.setCanton("Aflao Gakli");
        f1.setLocality("Tokoin");
        f1.setUin("UIN001");
        f1.setHasConflict(false);
        f1.setSurface("500m2");
        f1.setLandForm("Regulier");
        f1.setState(com.optimize.common.entities.enums.State.ENABLED);
        findingRepository.save(f1);

        com.optimize.land.model.entity.Finding f2 = new com.optimize.land.model.entity.Finding();
        f2.setNup("NUP002");
        f2.setRegion("Plateaux");
        f2.setPrefecture("Ogou");
        f2.setCommune("Atakpame");
        f2.setCanton("Agou");
        f2.setLocality("Nyive");
        f2.setUin("UIN002");
        f2.setHasConflict(false);
        f2.setSurface("1000m2");
        f2.setLandForm("Irregulier");
        f2.setState(com.optimize.common.entities.enums.State.ENABLED);
        findingRepository.save(f2);

        com.optimize.land.model.entity.Finding f3 = new com.optimize.land.model.entity.Finding();
        f3.setNup("NUP003");
        f3.setRegion("Maritime");
        f3.setPrefecture("Zio");
        f3.setCommune("Tsevie");
        f3.setCanton("Davie");
        f3.setLocality("Tokoin"); // Same locality as f1
        f3.setUin("UIN003");
        f3.setHasConflict(false);
        f3.setSurface("600m2");
        f3.setLandForm("Plat");
        f3.setState(com.optimize.common.entities.enums.State.ENABLED);
        findingRepository.save(f3);
    }
}
