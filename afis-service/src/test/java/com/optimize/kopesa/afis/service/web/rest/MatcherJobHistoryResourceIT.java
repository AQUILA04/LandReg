package com.optimize.kopesa.afis.service.web.rest;

import static com.optimize.kopesa.afis.service.domain.MatcherJobHistoryAsserts.*;
import static com.optimize.kopesa.afis.service.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.kopesa.afis.service.IntegrationTest;
import com.optimize.kopesa.afis.service.domain.MatcherJobHistory;
import com.optimize.kopesa.afis.service.repository.MatcherJobHistoryRepository;
import com.optimize.kopesa.afis.service.service.dto.MatcherJobHistoryDTO;
import com.optimize.kopesa.afis.service.service.mapper.MatcherJobHistoryMapper;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link MatcherJobHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MatcherJobHistoryResourceIT {

    private static final String DEFAULT_RID = "AAAAAAAAAA";
    private static final String UPDATED_RID = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRODUCER_COUNT = 1;
    private static final Integer UPDATED_PRODUCER_COUNT = 2;

    private static final Integer DEFAULT_CONSUMER_REPONSE_COUNT = 1;
    private static final Integer UPDATED_CONSUMER_REPONSE_COUNT = 2;

    private static final Double DEFAULT_HIGH_SCORE = 1D;
    private static final Double UPDATED_HIGH_SCORE = 2D;

    private static final Boolean DEFAULT_FOUND_MATCH = false;
    private static final Boolean UPDATED_FOUND_MATCH = true;

    private static final String DEFAULT_MATCHED_RID = "AAAAAAAAAA";
    private static final String UPDATED_MATCHED_RID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/matcher-job-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MatcherJobHistoryRepository matcherJobHistoryRepository;

    @Autowired
    private MatcherJobHistoryMapper matcherJobHistoryMapper;

    @Autowired
    private MockMvc restMatcherJobHistoryMockMvc;

    private MatcherJobHistory matcherJobHistory;

    private MatcherJobHistory insertedMatcherJobHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MatcherJobHistory createEntity() {
        return new MatcherJobHistory()
            .rid(DEFAULT_RID)
            .producerCount(DEFAULT_PRODUCER_COUNT)
            .consumerReponseCount(DEFAULT_CONSUMER_REPONSE_COUNT)
            .highScore(DEFAULT_HIGH_SCORE)
            .foundMatch(DEFAULT_FOUND_MATCH)
            .matchedRID(DEFAULT_MATCHED_RID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MatcherJobHistory createUpdatedEntity() {
        return new MatcherJobHistory()
            .rid(UPDATED_RID)
            .producerCount(UPDATED_PRODUCER_COUNT)
            .consumerReponseCount(UPDATED_CONSUMER_REPONSE_COUNT)
            .highScore(UPDATED_HIGH_SCORE)
            .foundMatch(UPDATED_FOUND_MATCH)
            .matchedRID(UPDATED_MATCHED_RID);
    }

    @BeforeEach
    public void initTest() {
        matcherJobHistory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMatcherJobHistory != null) {
            matcherJobHistoryRepository.delete(insertedMatcherJobHistory);
            insertedMatcherJobHistory = null;
        }
    }

    @Test
    void createMatcherJobHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MatcherJobHistory
        MatcherJobHistoryDTO matcherJobHistoryDTO = matcherJobHistoryMapper.toDto(matcherJobHistory);
        var returnedMatcherJobHistoryDTO = om.readValue(
            restMatcherJobHistoryMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(matcherJobHistoryDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MatcherJobHistoryDTO.class
        );

        // Validate the MatcherJobHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMatcherJobHistory = matcherJobHistoryMapper.toEntity(returnedMatcherJobHistoryDTO);
        assertMatcherJobHistoryUpdatableFieldsEquals(returnedMatcherJobHistory, getPersistedMatcherJobHistory(returnedMatcherJobHistory));

        insertedMatcherJobHistory = returnedMatcherJobHistory;
    }

    @Test
    void createMatcherJobHistoryWithExistingId() throws Exception {
        // Create the MatcherJobHistory with an existing ID
        matcherJobHistory.setId("existing_id");
        MatcherJobHistoryDTO matcherJobHistoryDTO = matcherJobHistoryMapper.toDto(matcherJobHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMatcherJobHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(matcherJobHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MatcherJobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllMatcherJobHistories() throws Exception {
        // Initialize the database
        insertedMatcherJobHistory = matcherJobHistoryRepository.save(matcherJobHistory);

        // Get all the matcherJobHistoryList
        restMatcherJobHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(matcherJobHistory.getId())))
            .andExpect(jsonPath("$.[*].rid").value(hasItem(DEFAULT_RID)))
            .andExpect(jsonPath("$.[*].producerCount").value(hasItem(DEFAULT_PRODUCER_COUNT)))
            .andExpect(jsonPath("$.[*].consumerReponseCount").value(hasItem(DEFAULT_CONSUMER_REPONSE_COUNT)))
            .andExpect(jsonPath("$.[*].highScore").value(hasItem(DEFAULT_HIGH_SCORE.doubleValue())))
            .andExpect(jsonPath("$.[*].foundMatch").value(hasItem(DEFAULT_FOUND_MATCH.booleanValue())))
            .andExpect(jsonPath("$.[*].matchedRID").value(hasItem(DEFAULT_MATCHED_RID)));
    }

    @Test
    void getMatcherJobHistory() throws Exception {
        // Initialize the database
        insertedMatcherJobHistory = matcherJobHistoryRepository.save(matcherJobHistory);

        // Get the matcherJobHistory
        restMatcherJobHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, matcherJobHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(matcherJobHistory.getId()))
            .andExpect(jsonPath("$.rid").value(DEFAULT_RID))
            .andExpect(jsonPath("$.producerCount").value(DEFAULT_PRODUCER_COUNT))
            .andExpect(jsonPath("$.consumerReponseCount").value(DEFAULT_CONSUMER_REPONSE_COUNT))
            .andExpect(jsonPath("$.highScore").value(DEFAULT_HIGH_SCORE.doubleValue()))
            .andExpect(jsonPath("$.foundMatch").value(DEFAULT_FOUND_MATCH.booleanValue()))
            .andExpect(jsonPath("$.matchedRID").value(DEFAULT_MATCHED_RID));
    }

    @Test
    void getNonExistingMatcherJobHistory() throws Exception {
        // Get the matcherJobHistory
        restMatcherJobHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingMatcherJobHistory() throws Exception {
        // Initialize the database
        insertedMatcherJobHistory = matcherJobHistoryRepository.save(matcherJobHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the matcherJobHistory
        MatcherJobHistory updatedMatcherJobHistory = matcherJobHistoryRepository.findById(matcherJobHistory.getId()).orElseThrow();
        updatedMatcherJobHistory
            .rid(UPDATED_RID)
            .producerCount(UPDATED_PRODUCER_COUNT)
            .consumerReponseCount(UPDATED_CONSUMER_REPONSE_COUNT)
            .highScore(UPDATED_HIGH_SCORE)
            .foundMatch(UPDATED_FOUND_MATCH)
            .matchedRID(UPDATED_MATCHED_RID);
        MatcherJobHistoryDTO matcherJobHistoryDTO = matcherJobHistoryMapper.toDto(updatedMatcherJobHistory);

        restMatcherJobHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, matcherJobHistoryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(matcherJobHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the MatcherJobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMatcherJobHistoryToMatchAllProperties(updatedMatcherJobHistory);
    }

    @Test
    void putNonExistingMatcherJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        matcherJobHistory.setId(UUID.randomUUID().toString());

        // Create the MatcherJobHistory
        MatcherJobHistoryDTO matcherJobHistoryDTO = matcherJobHistoryMapper.toDto(matcherJobHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMatcherJobHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, matcherJobHistoryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(matcherJobHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MatcherJobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMatcherJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        matcherJobHistory.setId(UUID.randomUUID().toString());

        // Create the MatcherJobHistory
        MatcherJobHistoryDTO matcherJobHistoryDTO = matcherJobHistoryMapper.toDto(matcherJobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMatcherJobHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(matcherJobHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MatcherJobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMatcherJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        matcherJobHistory.setId(UUID.randomUUID().toString());

        // Create the MatcherJobHistory
        MatcherJobHistoryDTO matcherJobHistoryDTO = matcherJobHistoryMapper.toDto(matcherJobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMatcherJobHistoryMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(matcherJobHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MatcherJobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMatcherJobHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedMatcherJobHistory = matcherJobHistoryRepository.save(matcherJobHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the matcherJobHistory using partial update
        MatcherJobHistory partialUpdatedMatcherJobHistory = new MatcherJobHistory();
        partialUpdatedMatcherJobHistory.setId(matcherJobHistory.getId());

        partialUpdatedMatcherJobHistory
            .producerCount(UPDATED_PRODUCER_COUNT)
            .foundMatch(UPDATED_FOUND_MATCH)
            .matchedRID(UPDATED_MATCHED_RID);

        restMatcherJobHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMatcherJobHistory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMatcherJobHistory))
            )
            .andExpect(status().isOk());

        // Validate the MatcherJobHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMatcherJobHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMatcherJobHistory, matcherJobHistory),
            getPersistedMatcherJobHistory(matcherJobHistory)
        );
    }

    @Test
    void fullUpdateMatcherJobHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedMatcherJobHistory = matcherJobHistoryRepository.save(matcherJobHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the matcherJobHistory using partial update
        MatcherJobHistory partialUpdatedMatcherJobHistory = new MatcherJobHistory();
        partialUpdatedMatcherJobHistory.setId(matcherJobHistory.getId());

        partialUpdatedMatcherJobHistory
            .rid(UPDATED_RID)
            .producerCount(UPDATED_PRODUCER_COUNT)
            .consumerReponseCount(UPDATED_CONSUMER_REPONSE_COUNT)
            .highScore(UPDATED_HIGH_SCORE)
            .foundMatch(UPDATED_FOUND_MATCH)
            .matchedRID(UPDATED_MATCHED_RID);

        restMatcherJobHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMatcherJobHistory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMatcherJobHistory))
            )
            .andExpect(status().isOk());

        // Validate the MatcherJobHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMatcherJobHistoryUpdatableFieldsEquals(
            partialUpdatedMatcherJobHistory,
            getPersistedMatcherJobHistory(partialUpdatedMatcherJobHistory)
        );
    }

    @Test
    void patchNonExistingMatcherJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        matcherJobHistory.setId(UUID.randomUUID().toString());

        // Create the MatcherJobHistory
        MatcherJobHistoryDTO matcherJobHistoryDTO = matcherJobHistoryMapper.toDto(matcherJobHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMatcherJobHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, matcherJobHistoryDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(matcherJobHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MatcherJobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMatcherJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        matcherJobHistory.setId(UUID.randomUUID().toString());

        // Create the MatcherJobHistory
        MatcherJobHistoryDTO matcherJobHistoryDTO = matcherJobHistoryMapper.toDto(matcherJobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMatcherJobHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(matcherJobHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MatcherJobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMatcherJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        matcherJobHistory.setId(UUID.randomUUID().toString());

        // Create the MatcherJobHistory
        MatcherJobHistoryDTO matcherJobHistoryDTO = matcherJobHistoryMapper.toDto(matcherJobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMatcherJobHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(matcherJobHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MatcherJobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMatcherJobHistory() throws Exception {
        // Initialize the database
        insertedMatcherJobHistory = matcherJobHistoryRepository.save(matcherJobHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the matcherJobHistory
        restMatcherJobHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, matcherJobHistory.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return matcherJobHistoryRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected MatcherJobHistory getPersistedMatcherJobHistory(MatcherJobHistory matcherJobHistory) {
        return matcherJobHistoryRepository.findById(matcherJobHistory.getId()).orElseThrow();
    }

    protected void assertPersistedMatcherJobHistoryToMatchAllProperties(MatcherJobHistory expectedMatcherJobHistory) {
        assertMatcherJobHistoryAllPropertiesEquals(expectedMatcherJobHistory, getPersistedMatcherJobHistory(expectedMatcherJobHistory));
    }

    protected void assertPersistedMatcherJobHistoryToMatchUpdatableProperties(MatcherJobHistory expectedMatcherJobHistory) {
        assertMatcherJobHistoryAllUpdatablePropertiesEquals(
            expectedMatcherJobHistory,
            getPersistedMatcherJobHistory(expectedMatcherJobHistory)
        );
    }
}
