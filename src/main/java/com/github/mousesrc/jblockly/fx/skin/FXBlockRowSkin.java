package com.github.mousesrc.jblockly.fx.skin;

import java.util.List;

import com.github.mousesrc.jblockly.fx.FXBlock;
import com.github.mousesrc.jblockly.fx.FXBlockRow;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;

public class FXBlockRowSkin extends SkinBase<FXBlockRow>{

	public FXBlockRowSkin(FXBlockRow control) {
		super(control);
		
		initComponentsListener();
		initBlockListener();
	}
	
	private void initComponentsListener(){
		getChildren().addAll(getSkinnable().getComponents());
		getSkinnable().getComponents().addListener(new ListChangeListener<Node>(){

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
	
	private boolean removingBlock = false;
	private void initBlockListener(){
		getChildren().add(getFXBlock());
		getSkinnable().blockProperty().addListener((observable,oldValue,newValue)->{
			if(oldValue != null && !removingBlock) {
				removingBlock = true;
				getChildren().remove(oldValue);
				removingBlock = false;
			}
			if(newValue != null)
				getChildren().add(newValue);
		});
		getChildren().addListener(new ListChangeListener<Node>(){

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while(c.next())
					if(c.getRemoved().contains(getFXBlock()) && !removingBlock){
						removingBlock = true;
						getSkinnable().setBlock(null);
						removingBlock = false;
					}
			}
			
		});
	}
	
	private FXBlock getFXBlock(){
		return getSkinnable().getFXBlock();
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
