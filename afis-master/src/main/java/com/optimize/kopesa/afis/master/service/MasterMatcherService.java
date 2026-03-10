package com.optimize.kopesa.afis.master.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.optimize.kopesa.afis.master.broker.MasterFeedbackProducer;
import com.optimize.kopesa.afis.master.domain.enumeration.ActorType;
import com.optimize.kopesa.afis.master.repository.FingerprintStoreRepository;
import com.optimize.kopesa.afis.master.repository.ProcessingFingerprintRepository;
import com.optimize.kopesa.afis.master.service.dto.AfisMasterRequest;
import com.optimize.kopesa.afis.master.service.dto.MatcherRequestDTO;
import com.optimize.kopesa.afis.master.service.dto.RegistrationProcessorFeedback;
import com.optimize.kopesa.afis.master.service.mapper.FingerprintStoreMapper;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.imaging.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class MasterMatcherService {
    private final Logger log = LoggerFactory.getLogger(MasterMatcherService.class);
    private final MessageBrokerService brokerService;
    private final FingerprintStoreRepository fingerprintStoreRepository;
    private final MatcherJobHistoryService matcherJobHistoryService;
    private final ProcessingFingerprintRepository processingFingerprintRepository;
    private final FingerprintStoreMapper fingerprintStoreMapper;
    private MasterFeedbackProducer feedbackProducer;
    @Value(value = "${afis-service.fingerprint-folder}")
    private String fingerprintFolder;

    public MasterMatcherService(MessageBrokerService brokerService,
                                FingerprintStoreRepository fingerprintStoreRepository,
                                MatcherJobHistoryService matcherJobHistoryService,
                                ProcessingFingerprintRepository processingFingerprintRepository,
                                FingerprintStoreMapper fingerprintStoreMapper) {
        this.brokerService = brokerService;
        this.fingerprintStoreRepository = fingerprintStoreRepository;
        this.matcherJobHistoryService = matcherJobHistoryService;
        this.processingFingerprintRepository = processingFingerprintRepository;
        this.fingerprintStoreMapper = fingerprintStoreMapper;
    }

    public void dispatchDeduplicationJob (AfisMasterRequest request) throws JsonProcessingException {
        long totalRecords = fingerprintStoreRepository.countByType(ActorType.PERSON);
        log.info("Total records in fingerprint store: {}", totalRecords);
        if (totalRecords > 0) {
            log.info("Total records in fingerprint store: {} processing", totalRecords);
            int batchSize = 18000;
            int numBatches = (int) Math.ceil((double) totalRecords / batchSize);
            for (int i = 0; i < numBatches; i++) {
                brokerService.sendBatchRequest(new MatcherRequestDTO(i, request.getRid(), batchSize, 65d, request.getFingerprintStores()));
            }
            matcherJobHistoryService.dispatchJob(request.getRid(), numBatches);
            request.getFingerprintStores().forEach(fs -> {
                fs.setType(ActorType.PERSON);
                    processingFingerprintRepository.save(fingerprintStoreMapper.toProcessingFingerprint(fs));
            });
        } else {
            log.info("No records in fingerprint store, sending feedback to registration processor");
            request.getFingerprintStores().forEach(fs -> {
                fs.setType(ActorType.PERSON);
                    fingerprintStoreRepository.save(fingerprintStoreMapper.toEntity(fs));
            });
            feedbackProducer.sendFeedbackToRegistrationProcessor(new RegistrationProcessorFeedback(request.getRid(), Boolean.FALSE, null));
        }

    }

    public static ImageInfo processFingerImage(@NotNull byte[] base64Bytes, String filename) throws IOException, ImageReadException, ImageWriteException {
        final BufferedImage inputImage = Imaging.getBufferedImage(base64Bytes);
        ImageInfo imageInfo = Imaging.getImageInfo(base64Bytes);
        imageInfo.getFormat();
        final ImageFormat format = ImageFormats.BMP;
        BufferedImage imageResize = Scalr.resize(inputImage, imageInfo.getWidth(), imageInfo.getHeight());
        Imaging.writeImage(imageResize, new File(filename), format);
        return imageInfo;
    }

    public byte[] rebuiltImage(@NotNull byte[] base64Bytes, String filename) throws ImageWriteException, IOException, ImageReadException {
        processFingerImage(base64Bytes, filename);
        return Files.readAllBytes(Paths.get(filename));
    }

    public static void folderUtil(String folder) {
        File topicFolder = new File(folder);
        if (!topicFolder.exists() && topicFolder.mkdirs()) {
            System.out.println("fingerprint default folder has created successfully at location");
            //log.info("fingerprint default folder has created successfully at location: {}", topicFolder.getAbsolutePath());
        }
    }

    public String getPath() {
        File fingerFolder = new File(fingerprintFolder);
        folderUtil(fingerprintFolder);
        return buildImagePath(fingerFolder, "MATCHING_MASTER"+ RandomStringUtils.randomNumeric(5), "MATCHING"+RandomStringUtils.randomNumeric(5), ".jpg");
    }

    public String buildImagePath(File folder, String residentUIN, String fingerName, String extension) {
        return folder.getAbsolutePath() + File.separator + residentUIN + "_" + fingerName.trim() + extension;
    }

    @Autowired
    public void setFeedbackProducer(MasterFeedbackProducer feedbackProducer) {
        this.feedbackProducer = feedbackProducer;
    }
}
