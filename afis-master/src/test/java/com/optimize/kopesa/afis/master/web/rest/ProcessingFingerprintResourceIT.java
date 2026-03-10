package com.optimize.kopesa.afis.master.web.rest;

import static com.optimize.kopesa.afis.master.domain.ProcessingFingerprintAsserts.*;
import static com.optimize.kopesa.afis.master.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.kopesa.afis.master.IntegrationTest;
import com.optimize.kopesa.afis.master.domain.ProcessingFingerprint;
import com.optimize.kopesa.afis.master.domain.enumeration.Finger;
import com.optimize.kopesa.afis.master.domain.enumeration.HandType;
import com.optimize.kopesa.afis.master.repository.ProcessingFingerprintRepository;
import com.optimize.kopesa.afis.master.service.dto.ProcessingFingerprintDTO;
import com.optimize.kopesa.afis.master.service.mapper.ProcessingFingerprintMapper;
import java.util.Base64;
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
 * Integration tests for the {@link ProcessingFingerprintResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProcessingFingerprintResourceIT {

    private static final String DEFAULT_RID = "AAAAAAAAAA";
    private static final String UPDATED_RID = "BBBBBBBBBB";

    private static final HandType DEFAULT_HAND_TYPE = HandType.LEFT;
    private static final HandType UPDATED_HAND_TYPE = HandType.RIGHT;

    private static final Finger DEFAULT_FINGER_NAME = Finger.THUMB;
    private static final Finger UPDATED_FINGER_NAME = Finger.INDEX;

    private static final byte[] DEFAULT_FINGERPRINT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FINGERPRINT_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FINGERPRINT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FINGERPRINT_IMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/processing-fingerprints";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProcessingFingerprintRepository processingFingerprintRepository;

    @Autowired
    private ProcessingFingerprintMapper processingFingerprintMapper;

    @Autowired
    private MockMvc restProcessingFingerprintMockMvc;

    private ProcessingFingerprint processingFingerprint;

    private ProcessingFingerprint insertedProcessingFingerprint;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProcessingFingerprint createEntity() {
        return new ProcessingFingerprint()
            .rid(DEFAULT_RID)
            .handType(DEFAULT_HAND_TYPE)
            .fingerName(DEFAULT_FINGER_NAME)
            .fingerprintImage(DEFAULT_FINGERPRINT_IMAGE)
            .fingerprintImageContentType(DEFAULT_FINGERPRINT_IMAGE_CONTENT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProcessingFingerprint createUpdatedEntity() {
        return new ProcessingFingerprint()
            .rid(UPDATED_RID)
            .handType(UPDATED_HAND_TYPE)
            .fingerName(UPDATED_FINGER_NAME)
            .fingerprintImage(UPDATED_FINGERPRINT_IMAGE)
            .fingerprintImageContentType(UPDATED_FINGERPRINT_IMAGE_CONTENT_TYPE);
    }

    @BeforeEach
    public void initTest() {
        processingFingerprint = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProcessingFingerprint != null) {
            processingFingerprintRepository.delete(insertedProcessingFingerprint);
            insertedProcessingFingerprint = null;
        }
    }

    @Test
    void createProcessingFingerprint() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProcessingFingerprint
        ProcessingFingerprintDTO processingFingerprintDTO = processingFingerprintMapper.toDto(processingFingerprint);
        var returnedProcessingFingerprintDTO = om.readValue(
            restProcessingFingerprintMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(processingFingerprintDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProcessingFingerprintDTO.class
        );

        // Validate the ProcessingFingerprint in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProcessingFingerprint = processingFingerprintMapper.toEntity(returnedProcessingFingerprintDTO);
        assertProcessingFingerprintUpdatableFieldsEquals(
            returnedProcessingFingerprint,
            getPersistedProcessingFingerprint(returnedProcessingFingerprint)
        );

        insertedProcessingFingerprint = returnedProcessingFingerprint;
    }

    @Test
    void createProcessingFingerprintWithExistingId() throws Exception {
        // Create the ProcessingFingerprint with an existing ID
        processingFingerprint.setId("existing_id");
        ProcessingFingerprintDTO processingFingerprintDTO = processingFingerprintMapper.toDto(processingFingerprint);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProcessingFingerprintMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(processingFingerprintDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProcessingFingerprint in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkRidIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        processingFingerprint.setRid(null);

        // Create the ProcessingFingerprint, which fails.
        ProcessingFingerprintDTO processingFingerprintDTO = processingFingerprintMapper.toDto(processingFingerprint);

        restProcessingFingerprintMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(processingFingerprintDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProcessingFingerprints() throws Exception {
        // Initialize the database
        insertedProcessingFingerprint = processingFingerprintRepository.save(processingFingerprint);

        // Get all the processingFingerprintList
        restProcessingFingerprintMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(processingFingerprint.getId())))
            .andExpect(jsonPath("$.[*].rid").value(hasItem(DEFAULT_RID)))
            .andExpect(jsonPath("$.[*].handType").value(hasItem(DEFAULT_HAND_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fingerName").value(hasItem(DEFAULT_FINGER_NAME.toString())))
            .andExpect(jsonPath("$.[*].fingerprintImageContentType").value(hasItem(DEFAULT_FINGERPRINT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fingerprintImage").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_FINGERPRINT_IMAGE))));
    }

    @Test
    void getProcessingFingerprint() throws Exception {
        // Initialize the database
        insertedProcessingFingerprint = processingFingerprintRepository.save(processingFingerprint);

        // Get the processingFingerprint
        restProcessingFingerprintMockMvc
            .perform(get(ENTITY_API_URL_ID, processingFingerprint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(processingFingerprint.getId()))
            .andExpect(jsonPath("$.rid").value(DEFAULT_RID))
            .andExpect(jsonPath("$.handType").value(DEFAULT_HAND_TYPE.toString()))
            .andExpect(jsonPath("$.fingerName").value(DEFAULT_FINGER_NAME.toString()))
            .andExpect(jsonPath("$.fingerprintImageContentType").value(DEFAULT_FINGERPRINT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.fingerprintImage").value(Base64.getEncoder().encodeToString(DEFAULT_FINGERPRINT_IMAGE)));
    }

    @Test
    void getNonExistingProcessingFingerprint() throws Exception {
        // Get the processingFingerprint
        restProcessingFingerprintMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingProcessingFingerprint() throws Exception {
        // Initialize the database
        insertedProcessingFingerprint = processingFingerprintRepository.save(processingFingerprint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the processingFingerprint
        ProcessingFingerprint updatedProcessingFingerprint = processingFingerprintRepository
            .findById(processingFingerprint.getId())
            .orElseThrow();
        updatedProcessingFingerprint
            .rid(UPDATED_RID)
            .handType(UPDATED_HAND_TYPE)
            .fingerName(UPDATED_FINGER_NAME)
            .fingerprintImage(UPDATED_FINGERPRINT_IMAGE)
            .fingerprintImageContentType(UPDATED_FINGERPRINT_IMAGE_CONTENT_TYPE);
        ProcessingFingerprintDTO processingFingerprintDTO = processingFingerprintMapper.toDto(updatedProcessingFingerprint);

        restProcessingFingerprintMockMvc
            .perform(
                put(ENTITY_API_URL_ID, processingFingerprintDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(processingFingerprintDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProcessingFingerprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProcessingFingerprintToMatchAllProperties(updatedProcessingFingerprint);
    }

    @Test
    void putNonExistingProcessingFingerprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        processingFingerprint.setId(UUID.randomUUID().toString());

        // Create the ProcessingFingerprint
        ProcessingFingerprintDTO processingFingerprintDTO = processingFingerprintMapper.toDto(processingFingerprint);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProcessingFingerprintMockMvc
            .perform(
                put(ENTITY_API_URL_ID, processingFingerprintDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(processingFingerprintDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProcessingFingerprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProcessingFingerprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        processingFingerprint.setId(UUID.randomUUID().toString());

        // Create the ProcessingFingerprint
        ProcessingFingerprintDTO processingFingerprintDTO = processingFingerprintMapper.toDto(processingFingerprint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProcessingFingerprintMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(processingFingerprintDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProcessingFingerprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProcessingFingerprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        processingFingerprint.setId(UUID.randomUUID().toString());

        // Create the ProcessingFingerprint
        ProcessingFingerprintDTO processingFingerprintDTO = processingFingerprintMapper.toDto(processingFingerprint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProcessingFingerprintMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(processingFingerprintDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProcessingFingerprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProcessingFingerprintWithPatch() throws Exception {
        // Initialize the database
        insertedProcessingFingerprint = processingFingerprintRepository.save(processingFingerprint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the processingFingerprint using partial update
        ProcessingFingerprint partialUpdatedProcessingFingerprint = new ProcessingFingerprint();
        partialUpdatedProcessingFingerprint.setId(processingFingerprint.getId());

        partialUpdatedProcessingFingerprint.rid(UPDATED_RID).handType(UPDATED_HAND_TYPE);

        restProcessingFingerprintMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProcessingFingerprint.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProcessingFingerprint))
            )
            .andExpect(status().isOk());

        // Validate the ProcessingFingerprint in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProcessingFingerprintUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProcessingFingerprint, processingFingerprint),
            getPersistedProcessingFingerprint(processingFingerprint)
        );
    }

    @Test
    void fullUpdateProcessingFingerprintWithPatch() throws Exception {
        // Initialize the database
        insertedProcessingFingerprint = processingFingerprintRepository.save(processingFingerprint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the processingFingerprint using partial update
        ProcessingFingerprint partialUpdatedProcessingFingerprint = new ProcessingFingerprint();
        partialUpdatedProcessingFingerprint.setId(processingFingerprint.getId());

        partialUpdatedProcessingFingerprint
            .rid(UPDATED_RID)
            .handType(UPDATED_HAND_TYPE)
            .fingerName(UPDATED_FINGER_NAME)
            .fingerprintImage(UPDATED_FINGERPRINT_IMAGE)
            .fingerprintImageContentType(UPDATED_FINGERPRINT_IMAGE_CONTENT_TYPE);

        restProcessingFingerprintMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProcessingFingerprint.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProcessingFingerprint))
            )
            .andExpect(status().isOk());

        // Validate the ProcessingFingerprint in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProcessingFingerprintUpdatableFieldsEquals(
            partialUpdatedProcessingFingerprint,
            getPersistedProcessingFingerprint(partialUpdatedProcessingFingerprint)
        );
    }

    @Test
    void patchNonExistingProcessingFingerprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        processingFingerprint.setId(UUID.randomUUID().toString());

        // Create the ProcessingFingerprint
        ProcessingFingerprintDTO processingFingerprintDTO = processingFingerprintMapper.toDto(processingFingerprint);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProcessingFingerprintMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, processingFingerprintDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(processingFingerprintDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProcessingFingerprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProcessingFingerprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        processingFingerprint.setId(UUID.randomUUID().toString());

        // Create the ProcessingFingerprint
        ProcessingFingerprintDTO processingFingerprintDTO = processingFingerprintMapper.toDto(processingFingerprint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProcessingFingerprintMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(processingFingerprintDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProcessingFingerprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProcessingFingerprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        processingFingerprint.setId(UUID.randomUUID().toString());

        // Create the ProcessingFingerprint
        ProcessingFingerprintDTO processingFingerprintDTO = processingFingerprintMapper.toDto(processingFingerprint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProcessingFingerprintMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(processingFingerprintDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProcessingFingerprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProcessingFingerprint() throws Exception {
        // Initialize the database
        insertedProcessingFingerprint = processingFingerprintRepository.save(processingFingerprint);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the processingFingerprint
        restProcessingFingerprintMockMvc
            .perform(delete(ENTITY_API_URL_ID, processingFingerprint.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return processingFingerprintRepository.count();
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

    protected ProcessingFingerprint getPersistedProcessingFingerprint(ProcessingFingerprint processingFingerprint) {
        return processingFingerprintRepository.findById(processingFingerprint.getId()).orElseThrow();
    }

    protected void assertPersistedProcessingFingerprintToMatchAllProperties(ProcessingFingerprint expectedProcessingFingerprint) {
        assertProcessingFingerprintAllPropertiesEquals(
            expectedProcessingFingerprint,
            getPersistedProcessingFingerprint(expectedProcessingFingerprint)
        );
    }

    protected void assertPersistedProcessingFingerprintToMatchUpdatableProperties(ProcessingFingerprint expectedProcessingFingerprint) {
        assertProcessingFingerprintAllUpdatablePropertiesEquals(
            expectedProcessingFingerprint,
            getPersistedProcessingFingerprint(expectedProcessingFingerprint)
        );
    }
}
