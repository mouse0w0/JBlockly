package com.github.mousesrc.jblockly.fx.util;

import java.util.Map;

public class BlockRegistry {
	
	private Map<String, BlockFactory> registeredBlocks;
	
	public void register(BlockFactory factory) {
		if(registeredBlocks.containsKey(factory.getName()))
			throw new IllegalArgumentException();
		registeredBlocks.put(factory.getName(), factory);
	}
	
	public void unregister(BlockFactory factory) {
		registeredBlocks.remove(factory.getName());
	}
	
	public BlockFactory get(String name) {
		return registeredBlocks.get(name);
	}

}
