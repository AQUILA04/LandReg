package com.optimize.common.entities.mapper;

import com.optimize.common.entities.entity.BaseEntity;

import java.util.List;
import java.util.Set;

public interface BaseMapper<E extends BaseEntity<?>, D> {
    E toEntity(D dto);
    D toDto(E entity);
    Set<E> toEntitySet(Set<D> dto);
    Set<D> toDtoSet(Set<E> entities);
    List<E> toEntityList(List<D> dto);
    List<D> toDtoList(List<E> entities);

}
