package com.github.mousesrc.jblockly.fx;

import static com.github.mousesrc.jblockly.fx.FXBlockConstant.LEFT_HEIGHT;
import static com.github.mousesrc.jblockly.fx.FXBlockConstant.LEFT_OFFSET_Y;
import static com.github.mousesrc.jblockly.fx.FXBlockConstant.LEFT_WIDTH;
import static com.github.mousesrc.jblockly.fx.FXBlockConstant.TOP_HEIGHT;
import static com.github.mousesrc.jblockly.fx.FXBlockConstant.TOP_OFFSET_X;
import static com.github.mousesrc.jblockly.fx.FXBlockConstant.TOP_WIDTH;

import java.util.List;

import com.github.mousesrc.jblockly.fx.util.SVGBuilder;

public interface FXBlockRenderer {
	
	static void renderBegin(FXBlock block, SVGBuilder svgBuilder){
		final List<FXBlockRow> rows = block.getFXRows();
		final double width = rows.isEmpty() ? 0 : rows.get(0).getAlignedWidth();
		switch (block.getConnectionType()) {
		case TOP:
			svgBuilder.m(0, 0)
					.h(TOP_OFFSET_X)
					.v(TOP_HEIGHT)
					.h(TOP_OFFSET_X + TOP_WIDTH)
					.v(0)
					.h(width);
			break;
		case LEFT:
			svgBuilder.m(LEFT_WIDTH,LEFT_OFFSET_Y + LEFT_HEIGHT)
					.h(0)
					.v(LEFT_OFFSET_Y)
					.h(LEFT_WIDTH)
					.v(0)
					.h(width);
			break;
		default:
			svgBuilder.m(0, 0).h(width);
			break;
		}
	}
	
	static void renderEnd(FXBlock block, SVGBuilder svgBuilder){
		svgBuilder.h(block.getConnectionType() == ConnectionType.LEFT ? LEFT_WIDTH : 0).z();
	}

	static void renderRow(FXBlockRow row, SVGBuilder svgBuilder){
		final double x = row.getLayoutX(), y = row.getLayoutY(), alignedRenderWidth = row.getAlignedWidth(),
				componentWidth = row.getComponentWidth(), componentHeight = row.getComponentHeight();
		switch (row.getType()) {
		case INSERT:
			svgBuilder.v(y + FXBlockConstant.LEFT_OFFSET_Y)
					.h(alignedRenderWidth - FXBlockConstant.LEFT_WIDTH)
					.v(y + FXBlockConstant.LEFT_OFFSET_Y + FXBlockConstant.LEFT_HEIGHT)
					.h(alignedRenderWidth);
			break;
		case BRANCH:
			svgBuilder.v(y)
					.h(x + componentWidth + FXBlockConstant.TOP_OFFSET_X + FXBlockConstant.TOP_WIDTH)
					.v(y + FXBlockConstant.TOP_HEIGHT)
					.h(x + componentWidth + FXBlockConstant.TOP_OFFSET_X)
					.v(y)
					.h(x + componentWidth)
					.v(y + Math.max(componentHeight, row.getBlockHeight()))
					.h(row.getNextRowAlignedRenderWidth());
			break;
		case NEXT:
			svgBuilder.v(y)
					.h(x + FXBlockConstant.TOP_OFFSET_X + FXBlockConstant.TOP_WIDTH)
					.v(y + FXBlockConstant.TOP_HEIGHT)
					.h(x + FXBlockConstant.TOP_OFFSET_X)
					.v(y);
			break;
		case NONE:
			svgBuilder.v(y + componentHeight);
			break;
		}
	}
}
