package com.optimize.land.model.mapper;

import com.optimize.common.entities.util.Converter;
import com.optimize.land.model.dto.*;
import com.optimize.land.model.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ActorMapper {

    //@Mapping(target = "identificationDocPhoto", expression = "java(toImageBytes(personDto.getIdentificationDocPhoto()))")
    @Mapping(target = "identificationDoc", expression = "java(toIdentificationDoc(personDto.getIdentificationDoc()))")
    Person toPerson(PersonDto personDto);
    //@Mapping(target = "identificationDocPhoto", expression = "java(toBase64String(person.getIdentificationDocPhoto()))")
    @Mapping(target = "identificationDoc", expression = "java(toIdentificationDocDto(person.getIdentificationDoc()))")
    PersonDto toPersonDto(Person person);

    @Mapping(target = "mandatePhoto", expression = "java(toImageBytes(informalGroupDto.getMandatePhoto()))")
    InformalGroup toInformalGroup(InformalGroupDto informalGroupDto);
    @Mapping(target = "mandatePhoto", expression = "java(toBase64String(informalGroup.getMandatePhoto()))")
    InformalGroupDto toInformalGroup(InformalGroup informalGroup);


    //@Mapping(target = "identificationDoc", expression = "java(toIdentificationDoc())")
    PrivateLegalEntity toPrivateLegalEntity(PrivateLegalEntityDto privateLegalEntityDto);
    PrivateLegalEntityDto toPrivateLegalEntityDto(PrivateLegalEntity privateLegalEntity);

    PublicLegalEntity toPublicLegalEntity(PublicLegalEntityDto publicLegalEntityDto);
    PublicLegalEntityDto toPublicLegalEntityDto(PublicLegalEntity publicLegalEntity);

    Registration toRegistration(ActorDto actorDto);
    Actor registrationToActor(Registration registration);

    @Mapping(target = "fingerprintImage", expression = "java(toImageBytes(fingerprintStoreDto.getFingerprintImage()))")
    @Mapping(target = "fingerName", expression = "java(fingerprintStoreDto.fingerNameFromString())")
    @Mapping(target = "handType", expression = "java(fingerprintStoreDto.getHandTypeFromString())")
    FingerprintStore toFingerprintStore(FingerprintStoreDto fingerprintStoreDto);
    @Mapping(target = "fingerprintImage", expression = "java(toBase64String(fingerprintStore.getFingerprintImage()))")
    FingerprintStoreDto toFingerprintStoreDto(FingerprintStore fingerprintStore);
    Set<FingerprintStore> toSetFingerprintStore(Set<FingerprintStoreDto> fingerprintStoreDtoSet);
    Set<FingerprintStoreDto> toSetFingerprintStoreDto(Set<FingerprintStore> fingerprintStores);

    @Mapping(target = "identificationDocPhoto", expression = "java(toImageBytes(identificationDocDto.getIdentificationDocPhoto()))")
    IdentificationDoc toIdentificationDoc(IdentificationDocDto identificationDocDto);
    @Mapping(target = "identificationDocPhoto", expression = "java(toBase64String(identificationDoc.getIdentificationDocPhoto()))")
    IdentificationDocDto toIdentificationDocDto(IdentificationDoc identificationDoc);

    default byte[] toImageBytes(String base64String) {
        return Converter.convertToByteImage(base64String);
    }

    default String toBase64String(byte[] bytes) {
        return Converter.convertToBase64Image(bytes);
    }
}
