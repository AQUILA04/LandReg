package com.optimize.kopesa.afis.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import com.optimize.kopesa.afis.service.service.dto.MatcherRequestDTO;
import com.optimize.kopesa.afis.service.service.dto.MatcherResponseDTO;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.imaging.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class MatcherService {

    private final Logger log = LoggerFactory.getLogger(MatcherService.class);
    private final MessageBrokerService brokerService;
    private final FingerprintStoreService fingerprintStoreService;
    @Value(value = "${afis-service.fingerprint-folder}")
    private String fingerprintFolder;

    public MatcherService(MessageBrokerService brokerService, FingerprintStoreService fingerprintStoreService) {
        this.brokerService = brokerService;
        this.fingerprintStoreService = fingerprintStoreService;
    }

    private MatcherResponseDTO findMatch(MatcherRequestDTO matcherRequestDTO) {
        Pageable pageable = PageRequest.of(matcherRequestDTO.getBatchId(), matcherRequestDTO.getBatchSize());
        List<Double> scores = new ArrayList<>();
        List<FingerprintMatcher> matchers = matcherRequestDTO.getFingerprints().stream()
            .map(fs -> {
                try {
                    return Optional.ofNullable(getFingerprintTemplate(fs.getFingerprintImage()))
                        .map(FingerprintMatcher::new).orElse(null);
                } catch (ImageWriteException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ImageReadException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
        return fingerprintStoreService.getByTypePerson(pageable).stream()
            .parallel()
            .map(candidate -> {
                matchers.forEach(matcher -> scores.add(Optional.ofNullable(matcher).map(m -> {
                    try {
                        return m.match(getFingerprintTemplate(candidate.getFingerprintImage()));
                    } catch (ImageWriteException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ImageReadException e) {
                        throw new RuntimeException(e);
                    }
                }).orElse(0d)));
                return new AbstractMap.SimpleEntry<>(candidate.getRid(), scores.stream().mapToDouble(Double::doubleValue).max().getAsDouble());
            })
            .filter(entry -> entry.getValue() >= matcherRequestDTO.getThreshold())
            .max(Map.Entry.comparingByValue())
            .map(entry -> new MatcherResponseDTO(matcherRequestDTO.getRid(), matcherRequestDTO.getBatchId(), entry.getValue(), Boolean.TRUE, entry.getKey()))
            .orElse(new MatcherResponseDTO(matcherRequestDTO.getRid(), matcherRequestDTO.getBatchId(), 0D, Boolean.FALSE, ""));
    }

    @KafkaListener(topics = "afis-matcher-topic", groupId = "afis-master", containerFactory = "kafkaListenerContainerFactory")
    public void processBatchRequest(String message) throws JsonProcessingException {
        log.info("RECEIVING MATCHING REQUEST: {}", message.substring(0, 255));
        MatcherRequestDTO matcherRequestDTO = new ObjectMapper().readValue(message, MatcherRequestDTO.class);
        MatcherResponseDTO result = findMatch(matcherRequestDTO);
        log.info("MATCHING REQUEST FINISHED: {}", result);
        // Envoie du résultat au service principal
        brokerService.sendResult(result);
        log.info("MATCHING REQUEST SENT: {}", result);
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
        return buildImagePath(fingerFolder, "MATCHING_SERVICE"+ RandomStringUtils.randomNumeric(5), "MS"+RandomStringUtils.randomNumeric(5), ".jpg");
    }

    public String buildImagePath(File folder, String rid, String fingerName, String extension) {
        return folder.getAbsolutePath() + File.separator + rid + "_" + fingerName.trim() + extension;
    }
}
