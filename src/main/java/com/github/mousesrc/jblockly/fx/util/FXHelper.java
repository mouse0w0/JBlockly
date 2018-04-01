package com.github.mousesrc.jblockly.fx.util;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public interface FXHelper {
	
	static Bounds addBounds2D(Bounds bounds, double x, double y) {
		return new BoundingBox(bounds.getMinX() + x, bounds.getMinY() + y, bounds.getWidth(), bounds.getHeight());
	}
	
	static Bounds subtractBounds2D(Bounds bounds, double x, double y) {
		return new BoundingBox(bounds.getMinX() - x, bounds.getMinY() - y, bounds.getWidth(), bounds.getHeight());
	}
	
	static double boundedSize(double min, double pref, double max) {
		double a = pref >= min ? pref : min;
		double b = min >= max ? min : max;
		return a <= b ? a : b;
	}
}
