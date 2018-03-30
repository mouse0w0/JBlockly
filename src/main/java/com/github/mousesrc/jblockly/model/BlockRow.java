package com.github.mousesrc.jblockly.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BlockRow {
	
	private Block block;
	private Map<String, Object> datas = new HashMap<>();

	public Optional<Block> getBlock() {
		return Optional.ofNullable(block);
	}
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	public boolean hasBlock() {
		return block != null;
	}

	public Set<String> getDataKeys() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public <V> Optional<V> getData(String key) {
		return Optional.ofNullable((V)datas.get(key));
	}

	@SuppressWarnings("unchecked")
	public <V> Optional<V> getData(String key, Class<V> type) {
		Object value = datas.get(key);
		return type.isAssignableFrom(value.getClass()) ? Optional.of((V) value) : Optional.empty();
	}

	public <V> void addData(String key, V value) {
		datas.put(key, value);
	}

	public void removeData(String key) {
		datas.remove(key);
	}

	public boolean containsData(String key) {
		return datas.containsKey(key);
	}

}
