package com.github.mousesrc.jblockly.fx;

import java.util.List;

import com.github.mousesrc.jblockly.fx.FXBlockRow.Type;
import com.github.mousesrc.jblockly.fx.util.FXHelper;
import com.github.mousesrc.jblockly.fx.util.SVGBuilder;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class FXBlockSkin extends SkinBase<FXBlock> {

	private SVGPath renderSVGPath;

	private boolean performingLayout;
	private double[] tempArray;

	public FXBlockSkin(FXBlock control) {
		super(control);

		consumeMouseEvents(false); // Make block drag avaliable.
		
		initRenderSVG();
		initComponentsListener();
	}

	private void initRenderSVG() {
		renderSVGPath = new SVGPath();
		getChildren().add(renderSVGPath);
		getSkinnable().setDragSVGPath(renderSVGPath);
		renderSVGPath.setFill(Color.WHITE);
		renderSVGPath.setStroke(Color.BLACK);
	}

	private void initComponentsListener() {
		getChildren().addAll(getSkinnable().getFXRows());
		getFXRows().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while (c.next()) {
					getChildren().addAll(c.getAddedSubList());
					getChildren().removeAll(c.getRemoved());
				}
			}

		});
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		final double left = getConnectionType() == ConnectionType.LEFT ? FXBlockConstant.LEFT_WIDTH : 0;
		double width = 0;
		for (FXBlockRow row : getFXRows()) {
			double rowWidth = computeChildPrefAreaWidth(row);
			if (rowWidth > width)
				width = rowWidth;
		}
		return left + width;
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		double height = 0;
		for (FXBlockRow row : getFXRows()) 
			height += computeChildPrefAreaHeight(row);
		return height;
	}

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		if (performingLayout)
			return;
		performingLayout = true;
		
		double left = getConnectionType() == ConnectionType.LEFT ? FXBlockConstant.LEFT_WIDTH : 0;
		double top = 0;
		
		ObservableList<FXBlockRow> rows = getFXRows();
		
		for (int i = 0, size = rows.size(); i < size; i++) {
			FXBlockRow row = rows.get(i);
			double width = snapSize(row.prefWidth(-1)), height = snapSize(row.prefHeight(-1));
			layoutInArea(row, left, top, width, height, -1, HPos.LEFT, VPos.TOP);
			top += height;
		}
		
		render(rows);

		layoutInArea(renderSVGPath, 0, 0, renderSVGPath.prefWidth(-1), renderSVGPath.prefHeight(-1), -1, HPos.LEFT,
				VPos.TOP);

		performingLayout = false;
	}
	
	protected void render(List<FXBlockRow> rows) {
		double[] rowComponentWidths = getRowRenderWidths(rows);
		int index = 0;
		double correntMaxWidth = 0;
		for (int i = 0, size = rows.size(); i < size; i++) {
			FXBlockRow row = rows.get(i);
			
			if (rowComponentWidths[i] > correntMaxWidth)
				correntMaxWidth = rowComponentWidths[i];

			if (row.getType() == Type.BRANCH) {
				alignRowWidth(rows, index, i, correntMaxWidth);
				index = i + 1;
				correntMaxWidth = rowComponentWidths[i];
			}
		}
		alignRowWidth(rows, index, rows.size() - 1, correntMaxWidth);
		
		SVGBuilder svgBuilder = new SVGBuilder();
		FXBlockRenderer.renderBegin(getSkinnable(), svgBuilder);
		for (FXBlockRow row : rows)
			FXBlockRenderer.renderRow(row, svgBuilder);
		FXBlockRenderer.renderEnd(getSkinnable(), svgBuilder);
		
		renderSVGPath.setContent(svgBuilder.toString());
		System.out.println(renderSVGPath.getContent());
	}
	
	private void alignRowWidth(List<FXBlockRow> rows, int from, int to, double alignedWidth) {
		for (int i = from; i <= to; i++) 
			rows.get(i).setAlignedWidth(alignedWidth);
	}
	
	private double[] getRowRenderWidths(List<FXBlockRow> rows) {
		double[] temp = getTempArray(rows.size());
		for (int i = 0, size = rows.size(); i < size; i++)
			temp[i] = rows.get(i).computeRenderWidth();
		return temp;
	}
	
	private double[] getTempArray(int size) {
		if (tempArray == null) {
			tempArray = new double[size];
		} else if (tempArray.length < size) {
			tempArray = new double[Math.max(tempArray.length * 3, size)];
		}
		return tempArray;
	}
	
	protected ObservableList<FXBlockRow> getFXRows() {
		return getSkinnable().getFXRows();
	}

	protected ConnectionType getConnectionType() {
		return getSkinnable().getConnectionType();
	}
	
	protected SVGPath getRenderSVGPath(){
		return renderSVGPath;
	}
	
	private double computeChildPrefAreaWidth(Node child) {
		return snapSize(FXHelper.boundedSize(child.minWidth(-1), child.prefWidth(-1), child.maxWidth(-1)));
	}

	private double computeChildPrefAreaHeight(Node child) {
		return snapSize(FXHelper.boundedSize(child.minHeight(-1), child.prefHeight(-1), child.maxHeight(-1)));
	}
}
