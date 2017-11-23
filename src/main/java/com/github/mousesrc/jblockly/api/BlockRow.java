package com.github.mousesrc.jblockly.api;

import java.util.Optional;
import java.util.Set;

public interface BlockRow {

	Optional<Block> getBlock();
	
	Set<BlockInput<?>> getInputers();
}
