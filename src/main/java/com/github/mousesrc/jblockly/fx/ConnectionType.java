package com.github.mousesrc.jblockly.fx;

public enum ConnectionType {
	NONE,
	TOP,
	LEFT;
	
	public boolean isConnectable(){
		return this != NONE;
	}
}
