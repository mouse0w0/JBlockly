package com.github.mousesrc.jblockly.fx.skin;

import java.util.List;

import com.github.mousesrc.jblockly.fx.ConnectionType;
import com.github.mousesrc.jblockly.fx.FXBlock;
import com.github.mousesrc.jblockly.fx.FXBlockConstant;
import com.github.mousesrc.jblockly.fx.FXBlockRow;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.shape.SVGPath;

public class FXBlockSkin extends SkinBase<FXBlock>{
	
	private final SVGPath renderSVGPath = new SVGPath();

	public FXBlockSkin(FXBlock control) {
		super(control);
		
		initComponentsListener();
		init();
	}
	
	private void init(){
		getChildren().add(renderSVGPath);
	}
	
	private void initComponentsListener(){
		getChildren().addAll(getSkinnable().getFXRows());
		getFXRows().addListener(new ListChangeListener<Node>(){

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while(c.next()){
					List<? extends Node> add = c.getAddedSubList();
					for(int i = 0, from = c.getFrom(), size = add.size();i < size;i++)
						getChildren().add(i + from, c.getAddedSubList().get(i));
					
					getChildren().removeAll(c.getRemoved());
				}
			}
			
		});
	}
	
	private ObservableList<FXBlockRow> getFXRows() {
		return getSkinnable().getFXRows();
	}
	
	private ConnectionType getConnectionType(){
		return getSkinnable().getConnectionType();
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		final double left = getConnectionType() == ConnectionType.LEFT ? FXBlockConstant.LEFT_WIDTH : 0;
		return left + getFXRows().stream().mapToDouble(row -> row.prefWidth(-1)).max().orElse(0);
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return getFXRows().stream().mapToDouble(row -> row.prefHeight(-1)).sum();
	}

	private boolean performingLayout;

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		if (performingLayout)
			return;
		performingLayout = true;

		double left = getConnectionType() == ConnectionType.LEFT ? FXBlockConstant.LEFT_WIDTH : 0;
		double top = 0;
		
		for (FXBlockRow row : getFXRows()) {
			double width = row.prefWidth(-1), height = row.prefHeight(-1);
			layoutInArea(row, left, top, width, height, -1, HPos.LEFT, VPos.TOP);
			top += height;
		}
		
		layoutInArea(renderSVGPath, 0, 0, renderSVGPath.prefWidth(-1), renderSVGPath.prefHeight(-1), -1, HPos.LEFT, VPos.TOP);

		performingLayout = false;
	}
}
