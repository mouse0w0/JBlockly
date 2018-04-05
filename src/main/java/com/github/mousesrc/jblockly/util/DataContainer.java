package com.github.mousesrc.jblockly.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DataContainer {

	private Map<String, Object> datas;

	public Map<String, Object> getDatas() {
		if (datas == null)
			datas = new HashMap<>();
		return datas;
	}

	public Set<String> getKeys() {
		return getDatas().keySet();
	}

	public Optional<Object> get(String key) {
		return Optional.ofNullable(getDatas().get(key));
	}

	@SuppressWarnings("unchecked")
	public <V> Optional<V> get(String key, Class<V> type) {
		Object value = getDatas().get(key);
		return value != null && type.isAssignableFrom(value.getClass()) ? Optional.of((V) value) : Optional.empty();
	}

	public void add(String key, Object value) {
		getDatas().put(key, value);
	}

	public void remove(String key) {
		getDatas().remove(key);
	}

	public boolean has(String key) {
		return getDatas().containsKey(key);
	}

	public boolean isEmpty() {
		return datas == null || getDatas().isEmpty();
	}

	public static class DataContainerSerializer
			implements JsonSerializer<DataContainer>, JsonDeserializer<DataContainer> {

		@Override
		public DataContainer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			DataContainer dataContainer = new DataContainer();
			if (json.isJsonNull())
				return dataContainer;

			JsonObject object = json.getAsJsonObject();
			Map<String, Object> datas = dataContainer.getDatas();
			for (Entry<String, JsonElement> entry : object.entrySet()) {
				JsonObject jValue = entry.getValue().getAsJsonObject();

				try {
					datas.put(entry.getKey(),
							context.deserialize(jValue.get("value"), Class.forName(jValue.get("type").getAsString())));
				} catch (ClassNotFoundException ignored) {
				}
			}
			return dataContainer;
		}

		@Override
		public JsonElement serialize(DataContainer src, Type typeOfSrc, JsonSerializationContext context) {
			if (src.isEmpty())
				return JsonNull.INSTANCE;

			JsonObject object = new JsonObject();
			for (Entry<String, Object> entry : src.getDatas().entrySet()) {
				Object value = entry.getValue();
				if (value == null)
					continue;

				JsonObject jValue = new JsonObject();
				jValue.addProperty("type", value.getClass().getName());
				jValue.add("value", context.serialize(value));

				object.add(entry.getKey(), jValue);
			}
			return object;
		}

	}
}
