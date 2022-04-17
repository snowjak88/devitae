package org.snowjak.devitae.data.conversion;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.devitae.data.entities.Scope;
import org.snowjak.devitae.data.repositories.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ScopeSerializationSupport {

    @JsonComponent
    public static class DeserializerByName extends JsonDeserializer<Scope> {

        @Autowired
        private ScopeRepository scopeRepository;

        @Override
        public Scope deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            return scopeRepository.findByName(p.getValueAsString());
        }
    }

    @JsonComponent
    public class SerializerByName extends JsonSerializer<Scope> {

        @Override
        public void serialize(Scope value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getName());
        }
    }
}
