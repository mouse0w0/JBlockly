package com.github.mousesrc.jblockly.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class Block {

	private String name;
	private final BiMap<BlockRow, String> rowToNames = HashBiMap.create();
	private final Map<String, Object> properties = new HashMap<>();
	
	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public BiMap<BlockRow, String> getRowToNames() {
		return rowToNames;
	}

	public Set<BlockRow> getRows() {
		return rowToNames.keySet();
	}

	public Set<String> getRowNames() {
		return rowToNames.values();
	}

	public Optional<BlockRow> getRow(String name) {
		return Optional.ofNullable(rowToNames.inverse().get(name));
	}
	
	public Optional<String> getRowName(BlockRow row) {
		return Optional.ofNullable(rowToNames.get(row));
	}

	public void setRowName(BlockRow row, String name) {
		rowToNames.forcePut(row, name);
	}

	public void addRow(BlockRow row, String name) {
		setRowName(row, name);
	}

	public void removeRow(BlockRow row) {
		rowToNames.remove(row);
	}

	public void removeRow(String name) {
		rowToNames.inverse().remove(name);
	}

	public boolean containsRow(BlockRow row) {
		return rowToNames.containsKey(row);
	}

	public boolean containsRow(String name) {
		return rowToNames.containsValue(name);
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}

	public Set<String> getPropertyKeys() {
		return getProperties().keySet();
	}

	@SuppressWarnings("unchecked")
	public <V> Optional<V> getProperty(String key) {
		return Optional.ofNullable((V)getProperties().get(key));
	}

	@SuppressWarnings("unchecked")
	public <V> Optional<V> getProperty(String key, Class<V> type) {
		Object value = getProperties().get(key);
		return type.isAssignableFrom(value.getClass()) ? Optional.of((V) value) : Optional.empty();
	}

	public <V> void addProperty(String key, V value) {
		getProperties().put(key, value);
	}

	public void removeProperty(String key) {
		getProperties().remove(key);
	}

	public boolean containsProperty(String key) {
		return getProperties().containsKey(key);
	}
}
