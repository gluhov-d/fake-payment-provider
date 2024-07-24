package com.github.gluhov.fakepaymentprovider.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class JsonExpirationDateDeserializer extends JsonDeserializer<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String date = p.getText();
        YearMonth yearMonth = YearMonth.parse(date, formatter);
        return yearMonth.atEndOfMonth();
    }
}