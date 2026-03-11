package com.optimize.kopesa.afis.master.service;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import com.optimize.kopesa.afis.master.domain.FingerprintStore;
import com.optimize.kopesa.afis.master.domain.enumeration.ActorType;
import com.optimize.kopesa.afis.master.repository.FingerprintStoreRepository;
import com.optimize.kopesa.afis.master.service.dto.BioAuthDto;
import com.optimize.kopesa.afis.master.service.dto.BioAuthResponse;
import com.optimize.kopesa.afis.master.service.dto.FingerprintStoreDTO;
import com.optimize.kopesa.afis.master.service.mapper.FingerprintStoreMapper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.DatatypeConverter;
import org.apache.commons.imaging.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.optimize.kopesa.afis.master.domain.FingerprintStore}.
 */
@Service
public class FingerprintStoreService {

    private static final Logger LOG = LoggerFactory.getLogger(FingerprintStoreService.class);

    private final FingerprintStoreRepository fingerprintStoreRepository;

    private final FingerprintStoreMapper fingerprintStoreMapper;
    @Value(value = "${afis-service.fingerprint-folder}")
    private String fingerprintFolder;

    public FingerprintStoreService(FingerprintStoreRepository fingerprintStoreRepository, FingerprintStoreMapper fingerprintStoreMapper) {
        this.fingerprintStoreRepository = fingerprintStoreRepository;
        this.fingerprintStoreMapper = fingerprintStoreMapper;
    }

    /**
     * Save a fingerprintStore.
     *
     * @param fingerprintStoreDTO the entity to save.
     * @return the persisted entity.
     */
    public FingerprintStoreDTO save(FingerprintStoreDTO fingerprintStoreDTO) {
        LOG.debug("Request to save FingerprintStore : {}", fingerprintStoreDTO);
        FingerprintStore fingerprintStore = fingerprintStoreMapper.toEntity(fingerprintStoreDTO);
        fingerprintStore.validData();
        fingerprintStore = fingerprintStoreRepository.save(fingerprintStore);
        fingerprintStore.validData();
        return fingerprintStoreMapper.toDto(fingerprintStore);
    }

    /**
     * Update a fingerprintStore.
     *
     * @param fingerprintStoreDTO the entity to save.
     * @return the persisted entity.
     */
    public FingerprintStoreDTO update(FingerprintStoreDTO fingerprintStoreDTO) {
        LOG.debug("Request to update FingerprintStore : {}", fingerprintStoreDTO);
        FingerprintStore fingerprintStore = fingerprintStoreMapper.toEntity(fingerprintStoreDTO);
        fingerprintStore = fingerprintStoreRepository.save(fingerprintStore);
        return fingerprintStoreMapper.toDto(fingerprintStore);
    }

    /**
     * Partially update a fingerprintStore.
     *
     * @param fingerprintStoreDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FingerprintStoreDTO> partialUpdate(FingerprintStoreDTO fingerprintStoreDTO) {
        LOG.debug("Request to partially update FingerprintStore : {}", fingerprintStoreDTO);

        return fingerprintStoreRepository
            .findById(fingerprintStoreDTO.getId())
            .map(existingFingerprintStore -> {
                fingerprintStoreMapper.partialUpdate(existingFingerprintStore, fingerprintStoreDTO);

                return existingFingerprintStore;
            })
            .map(fingerprintStoreRepository::save)
            .map(fingerprintStoreMapper::toDto);
    }

    /**
     * Get all the fingerprintStores.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<FingerprintStoreDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all FingerprintStores");
        return fingerprintStoreRepository.findAll(pageable).map(fingerprintStoreMapper::toDto);
    }

    /**
     * Get one fingerprintStore by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<FingerprintStoreDTO> findOne(String id) {
        LOG.debug("Request to get FingerprintStore : {}", id);
        return fingerprintStoreRepository.findById(id).map(fingerprintStoreMapper::toDto);
    }

    /**
     * Delete the fingerprintStore by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete FingerprintStore : {}", id);
        fingerprintStoreRepository.deleteById(id);
    }

    public String saveEntityFingerprint(Set<FingerprintStoreDTO> dtoSet) {
        dtoSet.forEach(f -> {
            f.setType(ActorType.ENTITY);
//            byte[] data = f.getFingerprintImage();
//            try {
//                f.setFingerprintImage(rebuiltImage(data, getPath()));
//            } catch (ImageWriteException e) {
//                throw new RuntimeException(e);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (ImageReadException e) {
//                throw new RuntimeException(e);
//            }
            save(f);
        });
        return "success";
    }

    public BioAuthResponse bioAuth(BioAuthDto bioAuthDto) throws ImageWriteException, IOException, ImageReadException {
        LOG.info("                                                                                        ");
        LOG.info("|============================================ BEGIN BIO AUTH ===============================|");
        LOG.info("|                                                                                           |");
        LOG.info("|                                                                                           |");
        LOG.info("STARTING BIO AUTH {}", bioAuthDto);
        FingerprintTemplate fingerprintTemplate = getFingerprintTemplate(DatatypeConverter.parseBase64Binary(bioAuthDto.getFingerprint()));
        FingerprintMatcher fingerprintMatcher = Optional.ofNullable(fingerprintTemplate)
            .map(FingerprintMatcher::new).orElseThrow();
        List<FingerprintStore> fingerprintStores = fingerprintStoreRepository.findByRid(bioAuthDto.getRid());
        LOG.info("===> NOMBRE TOTAL DE FINGERPRINT TROUVER POUR L'ACTEUR {} EST {}", bioAuthDto.getRid(), fingerprintStores.size());
        double score;
        double finalScore = 0d;
        for (FingerprintStore fingerprintStore : fingerprintStores) {
            LOG.info("FINGERPRINT DE {} : {}", bioAuthDto.getRid(), fingerprintStore);
            LOG.info("===> Fingerprint Image LENGTH {}", fingerprintStore.getFingerprintImage().length);
            score = fingerprintMatcher.match(getFingerprintTemplate(fingerprintStore.getFingerprintImage()));
            LOG .info("|========> SCORE : {}  <================|", score);
            if (score > finalScore) {
                finalScore = score;
            }
            if (finalScore > 40) {
                LOG .info(" |========>FINAL SCORE > 65 : {} <================|", finalScore);
                break;
            }
            LOG .info("|========>FINAL SCORE : {} <================|", finalScore);
        }
        LOG.info("|                                                                                           |");
        LOG.info("|                                                                                           |");
        LOG.info("|============================================ ENDED BIO AUTH ===============================|");
        LOG.info("                                                                                        ");
        LOG.info("                                                                                        ");
        return finalScore > 40 ? BioAuthResponse.MATCH : BioAuthResponse.FINGERPRINT_NOT_MATCH;
    }

    private FingerprintTemplate getFingerprintTemplate(byte[] fingerprintByte) throws ImageWriteException, IOException, ImageReadException {
        if (Objects.isNull(fingerprintByte) || fingerprintByte.length == 0) {
            return null;
        }

        try {
            return new FingerprintTemplate(
                new FingerprintImage(
                    fingerprintByte,
                    new FingerprintImageOptions()
                        .dpi(500)));
        } catch (IllegalArgumentException ex) {
            byte[] newImageByte = rebuiltImage(fingerprintByte, getPath());
            return getFingerprintTemplate(newImageByte);
        }

    }

    public static ImageInfo processFingerImage(@NotNull byte[] base64Bytes, String filename) throws IOException, ImageReadException, ImageWriteException {
        return MasterMatcherService.processFingerImage(base64Bytes, filename);
    }

    public byte[] rebuiltImage(@NotNull byte[] base64Bytes, String filename) throws ImageWriteException, IOException, ImageReadException {
        processFingerImage(base64Bytes, filename);
        return Files.readAllBytes(Paths.get(filename));
    }

    public static void folderUtil(String folder) {
        MasterMatcherService.folderUtil(folder);
    }

    public String getPath() {
        File fingerFolder = new File(fingerprintFolder);
        folderUtil(fingerprintFolder);
        return buildImagePath(fingerFolder, "Auth"+ RandomStringUtils.randomNumeric(5), "auth"+RandomStringUtils.randomNumeric(5), ".jpg");
    }

    public String buildImagePath(File folder, String residentUIN, String fingerName, String extension) {
        return folder.getAbsolutePath() + File.separator + residentUIN + "_" + fingerName.trim() + extension;
    }


}
