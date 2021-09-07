package com.exasol.bucketfs.jsonrpc;

import java.io.StringReader;
import java.util.Map;

import org.eclipse.yasson.YassonConfig;

import jakarta.json.*;
import jakarta.json.bind.*;

public class JsonMapper {

    private final Jsonb jsonb;

    private JsonMapper(final Jsonb jsonb) {
        this.jsonb = jsonb;
    }

    public static JsonMapper create() {
        return createWithFormatting(true);
    }

    public static JsonMapper createNonFormatting() {
        return createWithFormatting(false);
    }

    private static JsonMapper createWithFormatting(final boolean formatting) {
        final Map<Class<?>, Class<?>> userTypeMapping = Map.of();
        final JsonbConfig config = new JsonbConfig()
                .withFormatting(formatting)
                .setProperty(YassonConfig.USER_TYPE_MAPPING, userTypeMapping);
        return new JsonMapper(JsonbBuilder.create(config));
    }

    public String serialize(final JsonRpcPayload payload) {
        return this.jsonb.toJson(payload);
    }

    public <T> T deserialize(final String json, final Class<T> type) {
        return this.jsonb.fromJson(json, type);
    }

    public JsonObject toJsonObject(final Object parameters) {
        final String json = this.jsonb.toJson(parameters);
        return parseJsonObject(json);
    }

    private JsonObject parseJsonObject(final String json) {
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            return reader.readObject();
        }
    }

    public <T> T deserialize(final JsonStructure json, final Class<T> type) {
        return deserialize(json.toString(), type);
    }
}
