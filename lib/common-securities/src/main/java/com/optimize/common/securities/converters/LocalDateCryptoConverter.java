package com.optimize.common.securities.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Converter
public class LocalDateCryptoConverter implements AttributeConverter<LocalDate, String> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final StringCryptoConverter stringCryptoConverter = new StringCryptoConverter();

    @Override
    public String convertToDatabaseColumn(LocalDate attribute) {
        if (attribute == null) {
            return null;
        }
        String dateString = attribute.format(formatter);
        return stringCryptoConverter.convertToDatabaseColumn(dateString);
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String decryptedString = stringCryptoConverter.convertToEntityAttribute(dbData);
        try {
            return LocalDate.parse(decryptedString, formatter);
        } catch (Exception e) {
            return null;
        }
    }
}
