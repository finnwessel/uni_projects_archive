package de.hsflensburg.dataservice.configuration.json.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//https://github.com/Yoh0xFF/java-spring-security-example
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

  private final DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;

  @Override
  public LocalDate deserialize(JsonParser p, DeserializationContext context) throws IOException {
    return LocalDate.parse(p.getValueAsString(), fmt);
  }
}
