package com.exasol.bucketfs.jsonrpc;

import java.io.StringReader;

import com.exasol.bucketfs.jsonrpc.JsonRpcCommandExecutor.JsonRpcPayload;

import jakarta.json.*;
import jakarta.json.bind.*;

/**
 * Converts model classes to JSON and back.
 */
class JsonMapper {
    private final Jsonb jsonb;

    private JsonMapper(final Jsonb jsonb) {
        this.jsonb = jsonb;
    }

    static JsonMapper create() {
        final JsonbConfig config = new JsonbConfig().withFormatting(true);
        return new JsonMapper(JsonbBuilder.create(config));
    }

    String serialize(final JsonRpcPayload payload) {
        return this.jsonb.toJson(payload);
    }

    <T> T deserialize(final String json, final Class<T> type) {
        return this.jsonb.fromJson(json, type);
    }

    JsonObject toJsonObject(final Object parameters) {
        final String json = this.jsonb.toJson(parameters);
        return parseJsonObject(json);
    }

    JsonObject parseJsonObject(final String json) {
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            return reader.readObject();
        }
    }

    <T> T deserialize(final JsonStructure json, final Class<T> type) {
        return deserialize(json.toString(), type);
    }
}
