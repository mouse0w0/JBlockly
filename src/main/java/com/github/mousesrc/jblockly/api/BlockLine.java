package com.github.mousesrc.jblockly.api;

public interface BlockLine {
	
	static enum Type{
		NONE,
		BRANCH,
		INSERT,
		NEXT;
	}
	
	Type getType();
	
}
