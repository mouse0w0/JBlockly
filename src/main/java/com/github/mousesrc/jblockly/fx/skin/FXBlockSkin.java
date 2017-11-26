package com.github.mousesrc.jblockly.fx.skin;

import java.util.List;

import com.github.mousesrc.jblockly.fx.FXBlock;

import javafx.collections.ListChangeListener;
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
		getSkinnable().getFXRows().addListener(new ListChangeListener<Node>(){

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
	
	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		// TODO 自动生成的方法存根
		return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
	}
	
	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		// TODO 自动生成的方法存根
		return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
	}
	
	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		// TODO 自动生成的方法存根
		super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
	}

}
