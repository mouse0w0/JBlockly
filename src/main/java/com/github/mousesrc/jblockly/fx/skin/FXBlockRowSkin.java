package com.github.mousesrc.jblockly.fx.skin;

import java.util.List;

import com.github.mousesrc.jblockly.fx.FXBlock;
import com.github.mousesrc.jblockly.fx.FXBlockRow;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;

public class FXBlockRowSkin extends SkinBase<FXBlockRow>{
	
	private ObservableList<Node> components;
	private ReadOnlyObjectWrapper<Bounds> componentBounds;
	private boolean removingBlock;
	private boolean performingLayout;

	public FXBlockRowSkin(FXBlockRow control) {
		super(control);
		
		components = control.getComponents();
		
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
				updateComponents();
			}
			
		});
	}
	
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
	
	private FXBlock getParentBlock(){
		return getSkinnable().getParentBlock();
	}
	
	public void setComponentBoundsWrapper(ReadOnlyObjectWrapper<Bounds> componentsBounds) {
		this.componentBounds = componentsBounds;
	}
	
	protected double computeComponentWidth(double topInset, double rightInset, double bottomInset, double leftInset) {
		return leftInset
				+ components.stream().mapToDouble(node -> computeChildPrefAreaHeight(node, FXBlockRow.getMargin(node)))
						.max().orElse(0)
				+ (components.size() - 1) * snapSpace(getSkinnable().getSpacing()) + rightInset;
	}

	protected double computeComponentHeight(double topInset, double rightInset, double bottomInset, double leftInset) {
		return topInset + components.stream()
				.mapToDouble(node -> computeChildPrefAreaHeight(node, FXBlockRow.getMargin(node))).max().orElse(0)
				+ bottomInset;
	}

	protected void updateComponents() {
		Insets padding = getSkinnable().getComponentPadding();
		double top = snapSpace(padding.getTop());
		double right = snapSpace(padding.getRight());
		double bottom = snapSpace(padding.getBottom());
		double left = snapSpace(padding.getLeft());
		componentBounds.set(new BoundingBox(0, 0, computeComponentWidth(top, right, bottom, left),
				computeComponentHeight(top, right, bottom, left)));
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
		if (performingLayout)
			return;
		performingLayout = true;
		
		Insets padding = getSkinnable().getComponentPadding();
		double top = snapSpace(padding.getTop());
		double right = snapSpace(padding.getRight());
		double bottom = snapSpace(padding.getBottom());
		double left = snapSpace(padding.getLeft());
		double space = snapSpace(getSkinnable().getSpacing());
		
		for (Node node : components) {
			Insets margin = FXBlockRow.getMargin(node);
			double width = computeChildPrefAreaWidth(node, margin), height = computeChildPrefAreaHeight(node, margin);
			layoutInArea(node, left, top, width, height, -1, margin, HPos.LEFT, VPos.TOP);
			left += width + space;
		}
		
		FXBlock block = getFXBlock();
		layoutInArea(block, getSkinnable().getAlignedWidth(), 0, computeChildPrefAreaWidth(block, null), computeChildPrefAreaHeight(block, null),
				-1, HPos.LEFT, VPos.TOP);

		performingLayout = false;
	}
	
	private double computeChildPrefAreaWidth(Node child, Insets margin) {
        double left = margin != null? snapSpace(margin.getLeft()) : 0;
        double right = margin != null? snapSpace(margin.getRight()) : 0;
        return left + snapSize(child.prefWidth(-1)) + right;
    }

    private double computeChildPrefAreaHeight(Node child, Insets margin) {
        double top = margin != null? snapSpace(margin.getTop()) : 0;
        double bottom = margin != null? snapSpace(margin.getBottom()) : 0;
        return top + snapSize(child.prefHeight(-1)) + bottom;
    }
    
    @Override
    public void dispose() {
    	super.dispose();
    	components = null;
    	componentBounds = null;
    }
}
