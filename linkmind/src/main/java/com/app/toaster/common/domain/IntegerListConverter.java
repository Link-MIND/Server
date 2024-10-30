package com.app.toaster.common.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;

@Converter
public class IntegerListConverter implements AttributeConverter<ArrayList<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(ArrayList<Integer> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return String.join(",", attribute.stream().map(String::valueOf).toArray(String[]::new));
    }

    @Override
    public ArrayList<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        String[] values = dbData.split(",");
        ArrayList<Integer> result = new ArrayList<>();
        for (String value : values) {
            result.add(Integer.valueOf(value));
        }
        return result;
    }
}