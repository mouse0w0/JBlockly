package com.github.mousesrc.jblockly.fx.util;

import java.util.Map;

public class BlockRegistry {
	
	private BlockRegistry parent;
	private Map<String, BlockFactory> registeredBlocks;
	
	public BlockRegistry() {}
	
	public BlockRegistry(BlockRegistry parent) {
		this.setParent(parent);
	}
	
	public void register(BlockFactory factory) {
		if(get(factory.getName()) != null)
			throw new IllegalArgumentException();
		registeredBlocks.put(factory.getName(), factory);
	}
	
	public void unregister(BlockFactory factory) {
		registeredBlocks.remove(factory.getName());
	}
	
	public BlockFactory get(String name) {
		BlockFactory factory = registeredBlocks.get(name);
		return factory != null ? factory : parent != null ? parent.get(name) : null;
	}

	public BlockRegistry getParent() {
		return parent;
	}

	public void setParent(BlockRegistry parent) {
		this.parent = parent;
	}
}
