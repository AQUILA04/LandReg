package com.optimize.kopesa.afis.master.web.rest;

import static com.optimize.kopesa.afis.master.domain.FingerprintStoreAsserts.*;
import static com.optimize.kopesa.afis.master.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.kopesa.afis.master.IntegrationTest;
import com.optimize.kopesa.afis.master.domain.FingerprintStore;
import com.optimize.kopesa.afis.master.domain.enumeration.Finger;
import com.optimize.kopesa.afis.master.domain.enumeration.HandType;
import com.optimize.kopesa.afis.master.repository.FingerprintStoreRepository;
import com.optimize.kopesa.afis.master.service.dto.FingerprintStoreDTO;
import com.optimize.kopesa.afis.master.service.mapper.FingerprintStoreMapper;
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
 * Integration tests for the {@link FingerprintStoreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FingerprintStoreResourceIT {

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

    private static final String ENTITY_API_URL = "/api/fingerprint-stores";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FingerprintStoreRepository fingerprintStoreRepository;

    @Autowired
    private FingerprintStoreMapper fingerprintStoreMapper;

    @Autowired
    private MockMvc restFingerprintStoreMockMvc;

    private FingerprintStore fingerprintStore;

    private FingerprintStore insertedFingerprintStore;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FingerprintStore createEntity() {
        return new FingerprintStore()
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
    public static FingerprintStore createUpdatedEntity() {
        return new FingerprintStore()
            .rid(UPDATED_RID)
            .handType(UPDATED_HAND_TYPE)
            .fingerName(UPDATED_FINGER_NAME)
            .fingerprintImage(UPDATED_FINGERPRINT_IMAGE)
            .fingerprintImageContentType(UPDATED_FINGERPRINT_IMAGE_CONTENT_TYPE);
    }

    @BeforeEach
    public void initTest() {
        fingerprintStore = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedFingerprintStore != null) {
            fingerprintStoreRepository.delete(insertedFingerprintStore);
            insertedFingerprintStore = null;
        }
    }

    @Test
    void createFingerprintStore() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FingerprintStore
        FingerprintStoreDTO fingerprintStoreDTO = fingerprintStoreMapper.toDto(fingerprintStore);
        var returnedFingerprintStoreDTO = om.readValue(
            restFingerprintStoreMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(fingerprintStoreDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FingerprintStoreDTO.class
        );

        // Validate the FingerprintStore in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFingerprintStore = fingerprintStoreMapper.toEntity(returnedFingerprintStoreDTO);
        assertFingerprintStoreUpdatableFieldsEquals(returnedFingerprintStore, getPersistedFingerprintStore(returnedFingerprintStore));

        insertedFingerprintStore = returnedFingerprintStore;
    }

    @Test
    void createFingerprintStoreWithExistingId() throws Exception {
        // Create the FingerprintStore with an existing ID
        fingerprintStore.setId("existing_id");
        FingerprintStoreDTO fingerprintStoreDTO = fingerprintStoreMapper.toDto(fingerprintStore);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFingerprintStoreMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fingerprintStoreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FingerprintStore in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkRidIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fingerprintStore.setRid(null);

        // Create the FingerprintStore, which fails.
        FingerprintStoreDTO fingerprintStoreDTO = fingerprintStoreMapper.toDto(fingerprintStore);

        restFingerprintStoreMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fingerprintStoreDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllFingerprintStores() throws Exception {
        // Initialize the database
        insertedFingerprintStore = fingerprintStoreRepository.save(fingerprintStore);

        // Get all the fingerprintStoreList
        restFingerprintStoreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fingerprintStore.getId())))
            .andExpect(jsonPath("$.[*].rid").value(hasItem(DEFAULT_RID)))
            .andExpect(jsonPath("$.[*].handType").value(hasItem(DEFAULT_HAND_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fingerName").value(hasItem(DEFAULT_FINGER_NAME.toString())))
            .andExpect(jsonPath("$.[*].fingerprintImageContentType").value(hasItem(DEFAULT_FINGERPRINT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fingerprintImage").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_FINGERPRINT_IMAGE))));
    }

    @Test
    void getFingerprintStore() throws Exception {
        // Initialize the database
        insertedFingerprintStore = fingerprintStoreRepository.save(fingerprintStore);

        // Get the fingerprintStore
        restFingerprintStoreMockMvc
            .perform(get(ENTITY_API_URL_ID, fingerprintStore.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fingerprintStore.getId()))
            .andExpect(jsonPath("$.rid").value(DEFAULT_RID))
            .andExpect(jsonPath("$.handType").value(DEFAULT_HAND_TYPE.toString()))
            .andExpect(jsonPath("$.fingerName").value(DEFAULT_FINGER_NAME.toString()))
            .andExpect(jsonPath("$.fingerprintImageContentType").value(DEFAULT_FINGERPRINT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.fingerprintImage").value(Base64.getEncoder().encodeToString(DEFAULT_FINGERPRINT_IMAGE)));
    }

    @Test
    void getNonExistingFingerprintStore() throws Exception {
        // Get the fingerprintStore
        restFingerprintStoreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingFingerprintStore() throws Exception {
        // Initialize the database
        insertedFingerprintStore = fingerprintStoreRepository.save(fingerprintStore);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fingerprintStore
        FingerprintStore updatedFingerprintStore = fingerprintStoreRepository.findById(fingerprintStore.getId()).orElseThrow();
        updatedFingerprintStore
            .rid(UPDATED_RID)
            .handType(UPDATED_HAND_TYPE)
            .fingerName(UPDATED_FINGER_NAME)
            .fingerprintImage(UPDATED_FINGERPRINT_IMAGE)
            .fingerprintImageContentType(UPDATED_FINGERPRINT_IMAGE_CONTENT_TYPE);
        FingerprintStoreDTO fingerprintStoreDTO = fingerprintStoreMapper.toDto(updatedFingerprintStore);

        restFingerprintStoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fingerprintStoreDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fingerprintStoreDTO))
            )
            .andExpect(status().isOk());

        // Validate the FingerprintStore in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFingerprintStoreToMatchAllProperties(updatedFingerprintStore);
    }

    @Test
    void putNonExistingFingerprintStore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fingerprintStore.setId(UUID.randomUUID().toString());

        // Create the FingerprintStore
        FingerprintStoreDTO fingerprintStoreDTO = fingerprintStoreMapper.toDto(fingerprintStore);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFingerprintStoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fingerprintStoreDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fingerprintStoreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FingerprintStore in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFingerprintStore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fingerprintStore.setId(UUID.randomUUID().toString());

        // Create the FingerprintStore
        FingerprintStoreDTO fingerprintStoreDTO = fingerprintStoreMapper.toDto(fingerprintStore);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFingerprintStoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fingerprintStoreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FingerprintStore in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFingerprintStore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fingerprintStore.setId(UUID.randomUUID().toString());

        // Create the FingerprintStore
        FingerprintStoreDTO fingerprintStoreDTO = fingerprintStoreMapper.toDto(fingerprintStore);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFingerprintStoreMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fingerprintStoreDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FingerprintStore in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFingerprintStoreWithPatch() throws Exception {
        // Initialize the database
        insertedFingerprintStore = fingerprintStoreRepository.save(fingerprintStore);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fingerprintStore using partial update
        FingerprintStore partialUpdatedFingerprintStore = new FingerprintStore();
        partialUpdatedFingerprintStore.setId(fingerprintStore.getId());

        partialUpdatedFingerprintStore
            .fingerName(UPDATED_FINGER_NAME)
            .fingerprintImage(UPDATED_FINGERPRINT_IMAGE)
            .fingerprintImageContentType(UPDATED_FINGERPRINT_IMAGE_CONTENT_TYPE);

        restFingerprintStoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFingerprintStore.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFingerprintStore))
            )
            .andExpect(status().isOk());

        // Validate the FingerprintStore in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFingerprintStoreUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFingerprintStore, fingerprintStore),
            getPersistedFingerprintStore(fingerprintStore)
        );
    }

    @Test
    void fullUpdateFingerprintStoreWithPatch() throws Exception {
        // Initialize the database
        insertedFingerprintStore = fingerprintStoreRepository.save(fingerprintStore);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fingerprintStore using partial update
        FingerprintStore partialUpdatedFingerprintStore = new FingerprintStore();
        partialUpdatedFingerprintStore.setId(fingerprintStore.getId());

        partialUpdatedFingerprintStore
            .rid(UPDATED_RID)
            .handType(UPDATED_HAND_TYPE)
            .fingerName(UPDATED_FINGER_NAME)
            .fingerprintImage(UPDATED_FINGERPRINT_IMAGE)
            .fingerprintImageContentType(UPDATED_FINGERPRINT_IMAGE_CONTENT_TYPE);

        restFingerprintStoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFingerprintStore.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFingerprintStore))
            )
            .andExpect(status().isOk());

        // Validate the FingerprintStore in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFingerprintStoreUpdatableFieldsEquals(
            partialUpdatedFingerprintStore,
            getPersistedFingerprintStore(partialUpdatedFingerprintStore)
        );
    }

    @Test
    void patchNonExistingFingerprintStore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fingerprintStore.setId(UUID.randomUUID().toString());

        // Create the FingerprintStore
        FingerprintStoreDTO fingerprintStoreDTO = fingerprintStoreMapper.toDto(fingerprintStore);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFingerprintStoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fingerprintStoreDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fingerprintStoreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FingerprintStore in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFingerprintStore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fingerprintStore.setId(UUID.randomUUID().toString());

        // Create the FingerprintStore
        FingerprintStoreDTO fingerprintStoreDTO = fingerprintStoreMapper.toDto(fingerprintStore);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFingerprintStoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fingerprintStoreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FingerprintStore in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFingerprintStore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fingerprintStore.setId(UUID.randomUUID().toString());

        // Create the FingerprintStore
        FingerprintStoreDTO fingerprintStoreDTO = fingerprintStoreMapper.toDto(fingerprintStore);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFingerprintStoreMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fingerprintStoreDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FingerprintStore in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFingerprintStore() throws Exception {
        // Initialize the database
        insertedFingerprintStore = fingerprintStoreRepository.save(fingerprintStore);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the fingerprintStore
        restFingerprintStoreMockMvc
            .perform(delete(ENTITY_API_URL_ID, fingerprintStore.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return fingerprintStoreRepository.count();
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

    protected FingerprintStore getPersistedFingerprintStore(FingerprintStore fingerprintStore) {
        return fingerprintStoreRepository.findById(fingerprintStore.getId()).orElseThrow();
    }

    protected void assertPersistedFingerprintStoreToMatchAllProperties(FingerprintStore expectedFingerprintStore) {
        assertFingerprintStoreAllPropertiesEquals(expectedFingerprintStore, getPersistedFingerprintStore(expectedFingerprintStore));
    }

    protected void assertPersistedFingerprintStoreToMatchUpdatableProperties(FingerprintStore expectedFingerprintStore) {
        assertFingerprintStoreAllUpdatablePropertiesEquals(
            expectedFingerprintStore,
            getPersistedFingerprintStore(expectedFingerprintStore)
        );
    }
}
