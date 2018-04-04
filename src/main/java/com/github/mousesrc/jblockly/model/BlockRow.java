package com.github.mousesrc.jblockly.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BlockRow {
	
	private Block block;
	private Map<String, Object> datas;

	public Optional<Block> getBlock() {
		return Optional.ofNullable(block);
	}
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	public boolean hasBlock() {
		return block != null;
	}

	public Map<String, Object> getDatas() {
		if(datas == null)
			datas = new HashMap<>();
		return datas;
	}

	public Set<String> getDataKeys() {
		return getDatas().keySet();
	}

	@SuppressWarnings("unchecked")
	public <V> Optional<V> getData(String key) throws ClassCastException {
		return Optional.ofNullable((V)getDatas().get(key));
	}

	@SuppressWarnings("unchecked")
	public <V> Optional<V> getData(String key, Class<V> type) {
		Object value = getDatas().get(key);
		return value!=null && type.isAssignableFrom(value.getClass()) ? Optional.of((V) value) : Optional.empty();
	}

	public <V> void addData(String key, V value) {
		getDatas().put(key, value);
	}

	public void removeData(String key) {
		getDatas().remove(key);
	}

	public boolean containsData(String key) {
		return getDatas().containsKey(key);
	}

	public boolean hasData() {
		return datas != null && !getDatas().isEmpty();
	}
}
