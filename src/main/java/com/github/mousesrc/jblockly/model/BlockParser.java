package com.github.mousesrc.jblockly.model;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BlockParser {

	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Block.class, new BlockSerializer())
			.registerTypeAdapter(BlockRow.class, new BlockRowSerializer())
			.create();
	
	public static Block fromJson(String json) {
		return GSON.fromJson(json, Block.class);
	}
	
	public static Block fromJson(Reader reader) {
		return GSON.fromJson(reader, Block.class);
	}
	
	public static String toJson(Block block) {
		return GSON.toJson(block);
	}
	
	public static void toJson(Block block, Appendable writer) {
		GSON.toJson(block, writer);
	}
	
	public static class BlockSerializer implements JsonSerializer<Block>, JsonDeserializer<Block> {

		@Override
		public Block deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			Block block = new Block();
			block.setName(object.get("name").getAsString());
			block.getProperties().putAll(context.deserialize(object.get("properties"), Map.class));
			block.getRowToNames().inverse().putAll(context.deserialize(object.get("rows"), Map.class));
			return block;
		}

		@Override
		public JsonElement serialize(Block src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject object = new JsonObject();
			object.addProperty("name", src.getName().orElse(null));
			object.add("properties", context.serialize(src.getProperties()));
			object.add("rows", context.serialize(src.getRowToNames().inverse()));
			return object;
		}
		
	}
	
	public static class BlockRowSerializer implements JsonSerializer<BlockRow>, JsonDeserializer<BlockRow> {

		@Override
		public BlockRow deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			BlockRow row = new BlockRow();
			row.setBlock(context.deserialize(object.get("block"), Block.class));
			row.getData().putAll(context.deserialize(object.get("data"), Map.class));
			return row;
		}

		@Override
		public JsonElement serialize(BlockRow src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject object = new JsonObject();
			object.add("data", context.serialize(src.getData()));
			object.add("block", context.serialize(src.getBlock().orElse(null)));
			return object;
		}

	}
}
