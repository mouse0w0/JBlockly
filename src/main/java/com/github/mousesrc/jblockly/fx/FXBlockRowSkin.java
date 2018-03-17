package com.github.mousesrc.jblockly.fx;

import java.util.List;

import com.github.mousesrc.jblockly.fx.FXBlockRow.Type;
import com.github.mousesrc.jblockly.fx.util.FXHelper;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class FXBlockRowSkin extends SkinBase<FXBlockRow> {

	private ObservableList<Node> components;
	private boolean removingBlock;
	private boolean performingLayout;
	
	private HBox componentContainer;

	public FXBlockRowSkin(FXBlockRow control) {
		super(control);

		components = control.getComponents();
		
		initComponentContainer();
		initBlockListener();
	}
	
	private final ListChangeListener<Node> componentsListener = new ListChangeListener<Node>() {

		@Override
		public void onChanged(Change<? extends Node> c) {
			while (c.next()) {
				List<? extends Node> add = c.getAddedSubList();
				for (int i = 0, from = c.getFrom(), size = add.size(); i < size; i++)
					componentContainer.getChildren().add(i + from, c.getAddedSubList().get(i));

				componentContainer.getChildren().removeAll(c.getRemoved());
			}
		}

	};
	private final ChangeListener<? super FXBlock> blockChangeListener = (observable, oldValue, newValue) -> {
		if (oldValue != null && !removingBlock) {
			removingBlock = true;
			getChildren().remove(oldValue);
			removingBlock = false;
		}
		if (newValue != null)
			getChildren().add(newValue);
	};
	private final ListChangeListener<Node> childrenListener = new ListChangeListener<Node>() {

		@Override
		public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
			while (c.next())
				if (c.getRemoved().contains(getFXBlock()) && !removingBlock) {
					removingBlock = true;
					getSkinnable().setBlock(null);
					removingBlock = false;
				}
		}

	};

	private void initBlockListener() {
		FXBlock block = getFXBlock();
		if(block != null)
			getChildren().add(getFXBlock());
		
		getSkinnable().blockProperty().addListener(blockChangeListener);
		getChildren().addListener(childrenListener);
	}
	
	private void initComponentContainer() {
		componentContainer = new HBox();
		componentContainer.getStyleClass().setAll("component-container");
		componentContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		componentContainer.paddingProperty().bind(getSkinnable().componentPaddingProperty());
		componentContainer.spacingProperty().bind(getSkinnable().spacingProperty());
		componentContainer.getChildren().setAll(components);
		components.addListener(componentsListener);
		getChildren().add(componentContainer);
	}
	
	protected double computeBlockX() {
		return getType() == Type.INSERT ? getSkinnable().getAlignedWidth() - FXBlockConstant.LEFT_WIDTH
				: getSkinnable().getAlignedWidth();
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		FXBlock block = getFXBlock();
		return block == null ? computeComponentContainerWidth() : block.getLayoutX() + block.getWidth();
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		FXBlock block = getFXBlock();
		return Math.max(computeComponentContainerHeight(), block == null ? 0 : block.getLayoutY() + block.getHeight());
	}

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		if (performingLayout)
			return;
		performingLayout = true;
		
		double top = contentY;
		double left = contentX;

		double width = computeChildPrefAreaWidth(componentContainer, null), height = computeChildPrefAreaHeight(componentContainer, null);
		layoutInArea(componentContainer, left, top, width, height, -1, null, HPos.LEFT, VPos.TOP);
		left += width;

		FXBlock block = getFXBlock();
		if (block != null && block.isManaged()) {
			layoutInArea(block, computeBlockX(), 0, computeChildPrefAreaWidth(block, null),
					computeChildPrefAreaHeight(block, null), -1, HPos.LEFT, VPos.TOP);
		}

		performingLayout = false;
	}

	private double computeChildPrefAreaWidth(Node child, Insets margin) {
		double left = margin != null ? snapSpace(margin.getLeft()) : 0;
		double right = margin != null ? snapSpace(margin.getRight()) : 0;
		return left + snapSize(FXHelper.boundedSize(child.minWidth(-1), child.prefWidth(-1), child.maxWidth(-1))) + right;
	}

	private double computeChildPrefAreaHeight(Node child, Insets margin) {
		double top = margin != null ? snapSpace(margin.getTop()) : 0;
		double bottom = margin != null ? snapSpace(margin.getBottom()) : 0;
		return top + snapSize(FXHelper.boundedSize(child.minHeight(-1), child.prefHeight(-1), child.maxHeight(-1))) + bottom;
	}

	@Override
	public void dispose() {
		components.removeListener(componentsListener);
		getSkinnable().blockProperty().removeListener(blockChangeListener);
		getChildren().removeListener(childrenListener);
		components = null;
		super.dispose();
	}
	
	protected FXBlock getFXBlock() {
		return getSkinnable().getFXBlock();
	}

	protected FXBlock getParentBlock() {
		return getSkinnable().getParentBlock();
	}
	
	protected Type getType(){
		return getSkinnable().getType();
	}
	
	private double computeComponentContainerWidth() {
		double width = computeChildPrefAreaWidth(componentContainer, null);
		getSkinnable().setComponentWidth(width);
		return width;
	}
	
	private double computeComponentContainerHeight() {
		double height = computeChildPrefAreaHeight(componentContainer, null);
		getSkinnable().setComponentHeight(height);
		return height;
	}
}
