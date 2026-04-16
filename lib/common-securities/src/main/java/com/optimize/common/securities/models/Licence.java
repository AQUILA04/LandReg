package com.optimize.common.securities.models;

import com.optimize.common.securities.converters.LocalDateCryptoConverter;
import com.optimize.common.securities.converters.StringCryptoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Licence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Convert(converter = StringCryptoConverter.class)
    @Column(columnDefinition = "TEXT")
    private String activationCode;
    
    @Convert(converter = LocalDateCryptoConverter.class)
    @Column(columnDefinition = "TEXT")
    private LocalDate expirationDate;
    
    private boolean renewable;
    private boolean used = false;
}
