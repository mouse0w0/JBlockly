package com.github.mousesrc.jblockly.fx;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public interface FXBlockConstant {

	// Block
	double LEFT_WIDTH = 5;
	double LEFT_OFFSET_Y = 10;
	double LEFT_HEIGHT = 10;

	double TOP_WIDTH = 10;
	double TOP_HEIGHT = 5;
	double TOP_OFFSET_X = 10;

	// Block Row
	double BLOCK_SLOT_WIDTH = 5;
	double BLOCK_SLOT_HEIGHT = 30;
	
	double INSERT_ROW_WIDTH = LEFT_WIDTH;
	double INSERT_ROW_HEIGHT = LEFT_OFFSET_Y + LEFT_HEIGHT + 10;
	
	double NEXT_ROW_WIDTH = TOP_OFFSET_X + TOP_WIDTH;
	double NEXT_ROW_HEIGHT = TOP_HEIGHT;

	Bounds DEFAULT_SLOT_BOUNDS = new BoundingBox(0, 0, 5, 25);
	Bounds INSERT_SLOT_BOUNDS = new BoundingBox(0, 0, LEFT_WIDTH, LEFT_OFFSET_Y + LEFT_HEIGHT);
	Bounds NEXT_SLOT_BOUNDS = new BoundingBox(0, 0, TOP_OFFSET_X + TOP_WIDTH, TOP_HEIGHT);

	Bounds INSERT_ROW_CONNECTION_BOUNDS = new BoundingBox(0, LEFT_OFFSET_Y, LEFT_WIDTH, LEFT_HEIGHT);
	Bounds NEXT_ROW_CONNECTION_BOUNDS = new BoundingBox(TOP_OFFSET_X, 0, TOP_WIDTH, TOP_HEIGHT);

	double BLOCK_SLOT_MIN_LINE_WIDTH = 100;
	double BRANCH_SLOT_BOTTOM_HEIGHT = 10;
	double BRANCH_SLOT_CONTAINER_MIN_HEIGHT = 10;
	double NEXT_SLOT_BOTTOM_HEIGHT = 10;
	double BRANCH_MIN_WIDTH = 20;
}
