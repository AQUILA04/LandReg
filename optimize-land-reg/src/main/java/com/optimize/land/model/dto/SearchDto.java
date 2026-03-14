package com.optimize.land.model.dto;

import jakarta.validation.constraints.NotNull;

public record SearchDto(@NotNull(message = "Le mot clé de la recherche est obligatoire !") String keyword) {
}
