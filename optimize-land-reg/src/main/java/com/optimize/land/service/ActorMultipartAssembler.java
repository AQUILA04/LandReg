package com.optimize.land.service;

import com.optimize.common.entities.exception.ApplicationException;
import com.optimize.common.entities.exception.CustomValidationException;
import com.optimize.land.model.dto.v2.ActorDtoV2;
import com.optimize.land.model.dto.v2.FingerprintStoreV2Dto;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.model.enumeration.ActorType;
import com.optimize.land.model.mapper.ActorMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ActorMultipartAssembler {

    private final ActorMapper actorMapper;

    public ActorMultipartAssembler(ActorMapper actorMapper) {
        this.actorMapper = actorMapper;
    }

    public Set<FingerprintStore> assembleFingerprintStores(ActorDtoV2 actorDto, List<MultipartFile> fingerprintFiles) {
        Set<FingerprintStoreV2Dto> metadata = actorDto.getFingerprintStores();
        if (metadata.isEmpty()) {
            if (ActorType.PHYSICAL_PERSON.equals(actorDto.getType()) && !fingerprintFiles.isEmpty()) {
                throw new CustomValidationException("Les métadonnées d'empreintes sont obligatoires pour une personne physique.");
            }
            return Set.of();
        }
        if (metadata.size() != fingerprintFiles.size()) {
            throw new CustomValidationException(
                "Le nombre de fichiers d'empreintes (" + fingerprintFiles.size() + ") ne correspond pas aux métadonnées (" + metadata.size() + ")."
            );
        }

        List<FingerprintStoreV2Dto> orderedMetadata = new ArrayList<>(metadata);
        boolean hasExplicitIndex = orderedMetadata.stream().anyMatch(meta -> meta.getPartIndex() != null);
        if (hasExplicitIndex) {
            orderedMetadata.sort(Comparator.comparing(FingerprintStoreV2Dto::getPartIndex));
        }

        Set<FingerprintStore> stores = new HashSet<>();
        for (int i = 0; i < orderedMetadata.size(); i++) {
            stores.add(toFingerprintStore(orderedMetadata.get(i), fingerprintFiles.get(i)));
        }
        return stores;
    }

    private FingerprintStore toFingerprintStore(FingerprintStoreV2Dto metadata, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomValidationException("Chaque empreinte digitale doit contenir un fichier image non vide.");
        }
        try {
            byte[] imageBytes = file.getBytes();
            String contentType = resolveContentType(file);
            return actorMapper.toFingerprintStore(metadata, imageBytes, contentType);
        } catch (IOException e) {
            throw new ApplicationException("Impossible de lire le fichier d'empreinte digitale: " + e.getMessage());
        }
    }

    private String resolveContentType(MultipartFile file) {
        if (StringUtils.hasText(file.getContentType())) {
            return file.getContentType();
        }
        String filename = file.getOriginalFilename();
        if (filename != null) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".png")) {
                return "image/png";
            }
            if (lower.endsWith(".bmp")) {
                return "image/bmp";
            }
            if (lower.endsWith(".wsq")) {
                return "image/wsq";
            }
        }
        return "image/jpeg";
    }
}
