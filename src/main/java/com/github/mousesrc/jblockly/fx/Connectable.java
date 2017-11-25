package com.github.mousesrc.jblockly.fx;

import javafx.geometry.Point2D;

import javafx.geometry.Bounds;

public interface Connectable {
	
	boolean connect(FXBlock block, Point2D point);
	
	default boolean connect(FXBlock block, Bounds bounds) {
		return false;
	}
}
