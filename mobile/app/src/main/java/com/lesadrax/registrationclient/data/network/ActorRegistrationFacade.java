package com.lesadrax.registrationclient.data.network;

import android.content.Context;

import com.google.gson.Gson;
import com.lesadrax.registrationclient.data.config.ActorRegistrationConfig;
import com.lesadrax.registrationclient.data.mapper.ActorRegistrationMapper;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.dto.ActorRegistrationRequest;
import com.lesadrax.registrationclient.data.model.dto.ActorRegistrationRequestV2;
import com.lesadrax.registrationclient.data.model.dto.FingerprintFilePart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Point d'entrée unique pour la création et la mise à jour d'acteurs.
 * Bascule automatiquement entre l'API v1 (JSON base64) et v2 (multipart) selon la configuration.
 */
public class ActorRegistrationFacade {

    private static final Gson GSON = new Gson();
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final ActorRegistrationConfig config;

    public ActorRegistrationFacade(Context context) {
        this.config = new ActorRegistrationConfig(context);
    }

    public boolean isV2Enabled() {
        return config.isV2Enabled();
    }

    /**
     * Crée un acteur via v1 ou v2 selon la configuration active.
     */
    public Call<Void> createActor(ApiService apiService, Actor actor, String synchroBatchNumber) {
        if (config.isV2Enabled()) {
            return createActorV2(apiService, actor, synchroBatchNumber);
        }
        return createActorV1(apiService, actor, synchroBatchNumber);
    }

    /**
     * Mise à jour : reste sur v1 tant que le backend n'expose pas de PUT v2.
     */
    public Call<java.util.LinkedHashMap<String, Object>> updateActor(
        ApiService apiService,
        long actorId,
        Actor actor
    ) {
        ActorRegistrationRequest request = ActorRegistrationMapper.mapToV1(actor, "");
        return apiService.updateObject(String.valueOf(actorId), request);
    }

    public ActorRegistrationRequest toV1Request(Actor actor, String synchroBatchNumber) {
        return ActorRegistrationMapper.mapToV1(actor, synchroBatchNumber);
    }

    public ActorRegistrationRequestV2 toV2Request(Actor actor, String synchroBatchNumber) {
        return ActorRegistrationMapper.mapToV2(actor, synchroBatchNumber);
    }

    private Call<Void> createActorV1(ApiService apiService, Actor actor, String synchroBatchNumber) {
        ActorRegistrationRequest request = ActorRegistrationMapper.mapToV1(actor, synchroBatchNumber);
        return apiService.createActor(request);
    }

    private Call<Void> createActorV2(ApiService apiService, Actor actor, String synchroBatchNumber) {
        ActorRegistrationRequestV2 request = ActorRegistrationMapper.mapToV2(actor, synchroBatchNumber);
        RequestBody actorPart = RequestBody.create(JSON_MEDIA_TYPE, GSON.toJson(request));
        List<MultipartBody.Part> fingerprintParts = toMultipartParts(actor);
        return apiService.createActorV2(actorPart, fingerprintParts);
    }

    private List<MultipartBody.Part> toMultipartParts(Actor actor) {
        List<FingerprintFilePart> fileParts = ActorRegistrationMapper.extractFingerprintFileParts(
            actor.getFormValues(),
            actor
        );
        List<MultipartBody.Part> parts = new ArrayList<>();
        if (fileParts == null) {
            return parts;
        }

        for (FingerprintFilePart filePart : fileParts) {
            File file = new File(filePart.getFilePath());
            String mimeType = guessMimeType(file.getName());
            RequestBody fileBody = RequestBody.create(MediaType.parse(mimeType), file);
            parts.add(MultipartBody.Part.createFormData("fingerprints", file.getName(), fileBody));
        }
        return parts;
    }

    private static String guessMimeType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".wsq")) {
            return "application/octet-stream";
        }
        return "image/jpeg";
    }
}
