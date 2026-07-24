package com.lesadrax.registrationclient.data.mapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.RoleEnum;
import com.lesadrax.registrationclient.data.model.dto.ActorRegistrationRequest;
import com.lesadrax.registrationclient.data.model.dto.ActorRegistrationRequestV2;
import com.lesadrax.registrationclient.data.model.dto.FingerprintFilePart;
import com.lesadrax.registrationclient.data.model.dto.FingerprintStoreRequest;
import com.lesadrax.registrationclient.data.model.dto.FingerprintStoreRequestV2;
import com.lesadrax.registrationclient.data.model.dto.IdentificationDocRequest;
import com.lesadrax.registrationclient.data.model.dto.InformalGroupRequest;
import com.lesadrax.registrationclient.data.model.dto.PersonRequest;
import com.lesadrax.registrationclient.data.model.dto.PrivateLegalEntityRequest;
import com.lesadrax.registrationclient.data.model.dto.PublicLegalEntityRequest;
import com.lesadrax.registrationclient.from.model.FormValue;
import com.lesadrax.registrationclient.from.utils.FileUtils;
import com.lesadrax.registrationclient.from.utils.FormDataUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ActorRegistrationMapper {

    private static final Gson GSON = new Gson();

    private ActorRegistrationMapper() {
    }

    public static ActorRegistrationRequest mapToV1(Actor actor, String synchroBatchNumber) {
        if (actor.getFormValues() == null) {
            return null;
        }

        Map<String, FormValue> form = actor.getFormValues();
        Object role = FormDataUtils.getFormValueValue(form, "role");
        Object roleType = FormDataUtils.getFormValueValue(form, "roleType");

        ActorRegistrationRequest request = new ActorRegistrationRequest();

        if (actor.getId() > 0 && synchroBatchNumber.isEmpty()) {
            request.setId(actor.getId());
        }

        attachEntity(request, form, roleType, actor);
        request.setType(resolveActorType(roleType));
        if (role instanceof String) {
            request.setRole((String) role);
        }

        Set<FingerprintStoreRequest> fingerprints = mapFingerprintStoresV1(form, actor);
        if (fingerprints != null) {
            request.setFingerprintStores(fingerprints);
        }

        request.setSynchroBatchNumber(synchroBatchNumber);
        request.setSynchroPacketNumber(UUID.randomUUID().toString());

        if (synchroBatchNumber.isEmpty()) {
            Object uin = FormDataUtils.getFormValueValue(form, "uin");
            if (uin != null) {
                request.setUin(uin.toString());
            }
        }

        return request;
    }

    public static ActorRegistrationRequestV2 mapToV2(Actor actor, String synchroBatchNumber) {
        ActorRegistrationRequest v1 = mapToV1(actor, synchroBatchNumber);
        if (v1 == null) {
            return null;
        }

        ActorRegistrationRequestV2 request = new ActorRegistrationRequestV2();
        request.setId(v1.getId());
        request.setPhysicalPerson(v1.getPhysicalPerson());
        request.setInformalGroup(v1.getInformalGroup());
        request.setPrivateLegalEntity(v1.getPrivateLegalEntity());
        request.setPublicLegalEntity(v1.getPublicLegalEntity());
        request.setUin(v1.getUin());
        request.setSynchroBatchNumber(v1.getSynchroBatchNumber());
        request.setSynchroPacketNumber(v1.getSynchroPacketNumber());
        request.setRole(v1.getRole());
        request.setType(v1.getType());

        List<FingerprintFilePart> fileParts = extractFingerprintFileParts(actor.getFormValues(), actor);
        if (fileParts != null) {
            request.setFingerprintStores(mapFingerprintStoresV2(fileParts));
        }

        return request;
    }

    public static JsonObject toJsonObject(ActorRegistrationRequest request) {
        if (request == null) {
            return null;
        }
        return GSON.fromJson(GSON.toJson(request), JsonObject.class);
    }

    public static List<FingerprintFilePart> extractFingerprintFileParts(Map<String, FormValue> form, Actor actor) {
        if (form == null) {
            return null;
        }

        String firstImage = FormDataUtils.getFormValueDisplay(form, "fingerFirstImage");
        String secondImage = FormDataUtils.getFormValueDisplay(form, "fingerSecondImage");
        String thirdImage = FormDataUtils.getFormValueDisplay(form, "fingerThirdImage");

        if (firstImage == null || secondImage == null || thirdImage == null) {
            return null;
        }

        List<FingerprintFilePart> parts = new ArrayList<>();
        parts.add(new FingerprintFilePart(
            FormDataUtils.getFormValueDisplay(form, "fingerFirstName"),
            firstImage,
            actor.getFinger1ID(),
            0
        ));
        parts.add(new FingerprintFilePart(
            FormDataUtils.getFormValueDisplay(form, "fingerSecondName"),
            secondImage,
            actor.getFinger2ID(),
            1
        ));
        parts.add(new FingerprintFilePart(
            FormDataUtils.getFormValueDisplay(form, "fingerThirdName"),
            thirdImage,
            actor.getFinger3ID(),
            2
        ));
        return parts;
    }

    private static void attachEntity(
        ActorRegistrationRequest request,
        Map<String, FormValue> form,
        Object roleType,
        Actor actor
    ) {
        if (!(roleType instanceof String)) {
            return;
        }

        String roleTypeName = (String) roleType;
        if (RoleEnum.RoleType.PHYSICAL_PERSON.name().equals(roleTypeName)
            || RoleEnum.RoleType.PHYSICAL_PERSON2.name().equals(roleTypeName)) {
            request.setPhysicalPerson(buildPerson(form, actor));
        } else if (RoleEnum.RoleType.PRIVATE_LEGAL_ENTITY.name().equals(roleTypeName)) {
            request.setPrivateLegalEntity(buildPrivateLegalEntity(form, actor));
        } else if (RoleEnum.RoleType.PUBLIC_LEGAL_ENTITY.name().equals(roleTypeName)) {
            request.setPublicLegalEntity(buildPublicLegalEntity(form, actor));
        } else if (RoleEnum.RoleType.INFORMAL_GROUP.name().equals(roleTypeName)) {
            request.setInformalGroup(buildInformalGroup(form, actor));
        }
    }

    private static PersonRequest buildPerson(Map<String, FormValue> form, Actor actor) {
        PersonRequest person = new PersonRequest();
        populateScalarFields(form, person);
        person.setIdentificationDoc(buildIdentificationDoc(form, actor));

        Boolean hasIdDoc = FormValueMapper.getBoolean(form, "hasIDDoc");
        if (Boolean.FALSE.equals(hasIdDoc)) {
            person.setIdentificationDoc(null);
        }
        if (form.containsKey("identificationDocPhoto") && form.get("identificationDocPhoto") == null) {
            person.setIdentificationDoc(null);
        }

        if (actor.getPersonID() != 0) {
            person.setId((long) actor.getPersonID());
        }
        return person;
    }

    private static PrivateLegalEntityRequest buildPrivateLegalEntity(Map<String, FormValue> form, Actor actor) {
        PrivateLegalEntityRequest entity = new PrivateLegalEntityRequest();
        populateScalarFields(form, entity);
        entity.setIdentificationDoc(buildIdentificationDoc(form, actor));

        Boolean hasIdDoc = FormValueMapper.getBoolean(form, "hasIDDoc");
        if (Boolean.FALSE.equals(hasIdDoc)) {
            entity.setIdentificationDoc(null);
        }

        if (actor.getPersonID() != 0) {
            entity.setId((long) actor.getPersonID());
        }
        return entity;
    }

    private static PublicLegalEntityRequest buildPublicLegalEntity(Map<String, FormValue> form, Actor actor) {
        PublicLegalEntityRequest entity = new PublicLegalEntityRequest();
        populateScalarFields(form, entity);
        if (actor.getPersonID() != 0) {
            entity.setId((long) actor.getPersonID());
        }
        return entity;
    }

    private static InformalGroupRequest buildInformalGroup(Map<String, FormValue> form, Actor actor) {
        InformalGroupRequest entity = new InformalGroupRequest();
        populateScalarFields(form, entity);
        String mandatePhoto = FormValueMapper.getString(form, "mandatePhoto");
        if (mandatePhoto != null && !mandatePhoto.isEmpty()) {
            entity.setMandatePhoto(FileUtils.convertFileToBase64WithPrefix(mandatePhoto));
        }
        if (actor.getPersonID() != 0) {
            entity.setId((long) actor.getPersonID());
        }
        return entity;
    }

    private static void populateScalarFields(Map<String, FormValue> form, PersonRequest target) {
        target.setLastname(FormValueMapper.getString(form, "lastname"));
        target.setFirstname(FormValueMapper.getString(form, "firstname"));
        target.setSex(FormValueMapper.getString(form, "sex"));
        target.setMaritalStatus(FormValueMapper.getString(form, "maritalStatus"));
        target.setBirthDate(FormValueMapper.getString(form, "birthDate"));
        target.setPlaceOfBirth(FormValueMapper.getString(form, "placeOfBirth"));
        target.setNationality(FormValueMapper.getString(form, "nationality"));
        target.setProfession(FormValueMapper.getString(form, "profession"));
        target.setOtherProfession(FormValueMapper.getString(form, "otherProfession"));
        target.setAddress(FormValueMapper.getString(form, "address"));
        target.setPrimaryPhone(FormValueMapper.getString(form, "primaryPhone"));
        target.setSecondaryPhone(FormValueMapper.getString(form, "secondaryPhone"));
        target.setEmail(FormValueMapper.getString(form, "email"));
        target.setHasHandicap(FormValueMapper.getBoolean(form, "hasHandicap"));
        target.setSocioCulturalGroup(FormValueMapper.getString(form, "socioCulturalGroup"));
        target.setHandicapType(FormValueMapper.getString(form, "handicapType"));
        target.setOtherHandicapType(FormValueMapper.getString(form, "otherHandicapType"));
        target.setHasIDDoc(FormValueMapper.getBoolean(form, "hasIDDoc"));
        target.setWitnessUIN(FormValueMapper.getString(form, "witnessUIN"));
    }

    private static void populateScalarFields(Map<String, FormValue> form, PrivateLegalEntityRequest target) {
        target.setUin(FormValueMapper.getString(form, "uin"));
        target.setCompanyName(FormValueMapper.getString(form, "companyName"));
        target.setAddress(FormValueMapper.getString(form, "address"));
        target.setPhoneNumber(FormValueMapper.getString(form, "phoneNumber"));
        target.setSecondaryPhoneNumber(FormValueMapper.getString(form, "secondaryPhoneNumber"));
        target.setEmail(FormValueMapper.getString(form, "email"));
        target.setEntityType(FormValueMapper.getString(form, "entityType"));
        target.setMainActivity(FormValueMapper.getString(form, "mainActivity"));
        target.setAcronym(FormValueMapper.getString(form, "acronym"));
        target.setCompanyCreatedDate(FormValueMapper.getString(form, "companyCreatedDate"));
        target.setRepresentativeUIN(FormValueMapper.getString(form, "representativeUIN"));
        target.setRepresentativeFullname(FormValueMapper.getString(form, "representativeFullname"));
    }

    private static void populateScalarFields(Map<String, FormValue> form, PublicLegalEntityRequest target) {
        target.setUin(FormValueMapper.getString(form, "uin"));
        target.setPublicEntityType(FormValueMapper.getString(form, "publicEntityType"));
        target.setPhoneNumber(FormValueMapper.getString(form, "phoneNumber"));
        target.setName(FormValueMapper.getString(form, "name"));
    }

    private static void populateScalarFields(Map<String, FormValue> form, InformalGroupRequest target) {
        target.setUin(FormValueMapper.getString(form, "uin"));
        target.setGroupName(FormValueMapper.getString(form, "groupName"));
        target.setAddress(FormValueMapper.getString(form, "address"));
        target.setPhoneNumber(FormValueMapper.getString(form, "phoneNumber"));
        target.setSecondaryPhoneNumber(FormValueMapper.getString(form, "secondaryPhoneNumber"));
        target.setEmail(FormValueMapper.getString(form, "email"));
        target.setGroupType(FormValueMapper.getString(form, "groupType"));
        target.setRepresentativeUIN(FormValueMapper.getString(form, "representativeUIN"));
        target.setRepresentativeFullname(FormValueMapper.getString(form, "representativeFullname"));
        target.setSecondaryRepresentativeUIN(FormValueMapper.getString(form, "secondaryRepresentativeUIN"));
        target.setSecondaryRepresentativeFullname(FormValueMapper.getString(form, "secondaryRepresentativeFullname"));
        target.setThirdRepresentativeUIN(FormValueMapper.getString(form, "thirdRepresentativeUIN"));
        target.setThirdRepresentativeFullname(FormValueMapper.getString(form, "thirdRepresentativeFullname"));
        target.setMandatePhotoContentType(FormValueMapper.getString(form, "mandatePhotoContentType"));
    }

    private static IdentificationDocRequest buildIdentificationDoc(Map<String, FormValue> form, Actor actor) {
        IdentificationDocRequest doc = new IdentificationDocRequest();
        doc.setIdentificationDocNumber(FormValueMapper.getString(form, "identificationDocNumber"));
        doc.setIdentificationDocType(FormValueMapper.getString(form, "identificationDocType"));
        doc.setOtherIdentificationDocType(FormValueMapper.getString(form, "otherIdentificationDocType"));
        doc.setIdentificationDocPhotoContentType(FormValueMapper.getString(form, "identificationDocPhotoContentType"));

        String photoPath = FormValueMapper.getString(form, "identificationDocPhoto");
        if (photoPath != null && !photoPath.isEmpty()) {
            doc.setIdentificationDocPhoto(FileUtils.convertFileToBase64WithPrefix(photoPath));
        }

        if (actor.getDocID() > 0) {
            doc.setId((long) actor.getDocID());
        }

        if (doc.getIdentificationDocNumber() == null
            && doc.getIdentificationDocType() == null
            && doc.getIdentificationDocPhoto() == null) {
            return null;
        }
        return doc;
    }

    private static Set<FingerprintStoreRequest> mapFingerprintStoresV1(Map<String, FormValue> form, Actor actor) {
        List<FingerprintFilePart> parts = extractFingerprintFileParts(form, actor);
        if (parts == null) {
            return null;
        }

        Set<FingerprintStoreRequest> stores = new LinkedHashSet<>();
        for (FingerprintFilePart part : parts) {
            FingerprintStoreRequest store = new FingerprintStoreRequest();
            store.setFingerStr(part.getFingerStr());
            store.setFingerprintImage(FileUtils.convertFileToBase64WithPrefix(part.getFilePath()));
            if (part.getFingerId() > 0) {
                store.setId(part.getFingerId());
            }
            stores.add(store);
        }
        return stores;
    }

    private static Set<FingerprintStoreRequestV2> mapFingerprintStoresV2(List<FingerprintFilePart> parts) {
        Set<FingerprintStoreRequestV2> stores = new LinkedHashSet<>();
        for (FingerprintFilePart part : parts) {
            FingerprintStoreRequestV2 store = new FingerprintStoreRequestV2();
            store.setFingerStr(part.getFingerStr());
            store.setPartIndex(part.getPartIndex());
            if (part.getFingerId() > 0) {
                store.setId(part.getFingerId());
            }
            stores.add(store);
        }
        return stores;
    }

    private static String resolveActorType(Object roleType) {
        if (!(roleType instanceof String)) {
            return null;
        }
        if (RoleEnum.RoleType.PHYSICAL_PERSON2.name().equals(roleType)) {
            return RoleEnum.RoleType.PHYSICAL_PERSON.name();
        }
        return (String) roleType;
    }
}
