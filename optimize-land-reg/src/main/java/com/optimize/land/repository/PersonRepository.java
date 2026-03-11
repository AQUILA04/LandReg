package com.optimize.land.repository;

import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.land.model.entity.Person;
import com.optimize.land.model.enumeration.MaritalStatus;
import com.optimize.land.model.enumeration.Sex;

import java.time.LocalDate;

public interface PersonRepository extends GenericRepository<Person, Long> {

    boolean existsByLastnameAndFirstnameAndSexAndMaritalStatusAndBirthDateAndPlaceOfBirthAndNationalityAndProfessionAndAddressAndPrimaryPhoneAndEmail(
            String lastname, String firstname, Sex sex, MaritalStatus maritalStatus, LocalDate birthDate,
            String placeOfBirth, String nationality, String profession, String address, String primaryPhone, String email);
}
