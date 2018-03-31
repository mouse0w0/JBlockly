package com.github.mousesrc.jblockly.fx.util;

import java.util.Map;

public class BlockRegistry {
	
	public static final BlockRegistry GLOBAL_BLOCK_REGISTY = new BlockRegistry(null);
	
	private BlockRegistry parent;
	private Map<String, BlockProvider> registeredBlocks;
	
	public BlockRegistry() {
		this(GLOBAL_BLOCK_REGISTY);
	}
	
	public BlockRegistry(BlockRegistry parent) {
		this.setParent(parent);
	}
	
	public void register(BlockProvider factory) {
		if(get(factory.getName()) != null)
			throw new IllegalArgumentException();
		registeredBlocks.put(factory.getName(), factory);
	}
	
	public void unregister(BlockProvider factory) {
		registeredBlocks.remove(factory.getName());
	}
	
	public BlockProvider get(String name) {
		BlockProvider factory = registeredBlocks.get(name);
		return factory != null ? factory : parent != null ? parent.get(name) : null;
	}

	public BlockRegistry getParent() {
		return parent;
	}

	public void setParent(BlockRegistry parent) {
		this.parent = parent;
	}
}
