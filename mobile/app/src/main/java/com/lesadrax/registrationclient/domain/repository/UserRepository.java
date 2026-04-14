package com.lesadrax.registrationclient.domain.repository;

import com.lesadrax.registrationclient.data.model.UserEntity;

public interface UserRepository {
    UserEntity login(String username, String password);
}
