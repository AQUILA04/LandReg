package com.optimize.land.model.mapper;

import com.optimize.common.entities.mapper.BaseMapper;
import com.optimize.common.entities.util.Converter;
import com.optimize.land.model.dto.BorderingDto;
import com.optimize.land.model.dto.CheckListOperationDto;
import com.optimize.land.model.dto.ConflictDto;
import com.optimize.land.model.dto.FindingDto;
import com.optimize.land.model.entity.Bordering;
import com.optimize.land.model.entity.CheckListOperation;
import com.optimize.land.model.entity.Conflict;
import com.optimize.land.model.entity.Finding;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface FindingMapper extends BaseMapper<Finding, FindingDto> {

    CheckListOperation toCheckListOperation(CheckListOperationDto checkListOperationDto);
    CheckListOperationDto toCheckListOperationDto(CheckListOperation checkListOperation);

    Bordering toBordering(BorderingDto borderingDto);
    BorderingDto toBorderingDto(Bordering bordering);
    Set<Bordering> toBorderingSet(Set<BorderingDto> borderingSet);
    Set<BorderingDto> toBorderingDtoSet(Set<Bordering> borderingSet);

    @Mapping(target = "photoOfProof", expression = "java(toImageBytes(conflictDto.getPhotoOfProof()))")
    @Mapping(target = "settlementProofPhoto", expression = "java(toImageBytes(conflictDto.getSettlementProofPhoto()))")
    @Mapping(target = "photoPreuveAcquisition", expression = "java(toImageBytes(conflictDto.getPhotoPreuveAcquisition()))")
    @Mapping(target = "photoTemoignage", expression = "java(toImageBytes(conflictDto.getPhotoTemoignage()))")
    @Mapping(target = "photoFicheTemoignage", expression = "java(toImageBytes(conflictDto.getPhotoFicheTemoignage()))")
    Conflict toConflict(ConflictDto conflictDto);
    @Mapping(target = "photoOfProof", expression = "java(toBase64String(conflict.getPhotoOfProof()))")
    @Mapping(target = "settlementProofPhoto", expression = "java(toBase64String(conflict.getSettlementProofPhoto()))")
    @Mapping(target = "photoPreuveAcquisition", expression = "java(toBase64String(conflict.getPhotoPreuveAcquisition()))")
    @Mapping(target = "photoTemoignage", expression = "java(toBase64String(conflict.getPhotoTemoignage()))")
    @Mapping(target = "photoFicheTemoignage", expression = "java(toBase64String(conflict.getPhotoFicheTemoignage()))")
    ConflictDto toConflictDto(Conflict conflict);


    default byte[] toImageBytes(String base64String) {
        return Converter.convertToByteImage(base64String);
    }

    default String toBase64String(byte[] bytes) {
        return Converter.convertToBase64Image(bytes);
    }


}
