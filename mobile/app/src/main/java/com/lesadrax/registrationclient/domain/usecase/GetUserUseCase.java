package com.lesadrax.registrationclient.domain.usecase;

import com.lesadrax.registrationclient.data.model.UserEntity;
import com.lesadrax.registrationclient.domain.repository.UserRepository;

public class GetUserUseCase {
    private UserRepository userRepository;

    public GetUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity execute(String username, String password) {
        return userRepository.login(username, password);
    }
}
