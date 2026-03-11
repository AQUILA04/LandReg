package com.optimize.land.repository;

import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.land.model.entity.InformalGroup;

public interface InformalGroupRepository extends GenericRepository<InformalGroup, Long> {

    boolean existsByGroupNameAndAddressAndPhoneNumber(String groupName, String address, String phoneNumber);
}
