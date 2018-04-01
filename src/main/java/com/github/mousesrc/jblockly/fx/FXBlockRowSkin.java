package com.github.mousesrc.jblockly.fx;

import java.util.List;

import com.github.mousesrc.jblockly.fx.FXBlockRow.Type;
import com.github.mousesrc.jblockly.fx.util.FXHelper;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;

public class FXBlockRowSkin extends SkinBase<FXBlockRow> {

	private ObservableList<Node> components;
	private boolean removingBlock;
	private boolean performingLayout;
	
	private HBox componentContainer;

	public FXBlockRowSkin(FXBlockRow control) {
		super(control);

		components = control.getComponents();
		
		consumeMouseEvents(false); // Make block drag available.
		
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
			final FXBlock block = getFXBlock();
			while (c.next())
				if (block != null && c.getRemoved().contains(block) && !removingBlock) {
					removingBlock = true;
					getSkinnable().setBlock(null);
					removingBlock = false;
				}
		}
	};
	private final InvalidationListener typeListener = (observable)->updateComponentContainer();

	private void initBlockListener() {
		FXBlock block = getFXBlock();
		if(block != null)
			getChildren().add(block);
		
		getSkinnable().blockProperty().addListener(blockChangeListener);
		getChildren().addListener(childrenListener);
	}
	
	private void initComponentContainer() {
		componentContainer = new HBox();
		componentContainer.getStyleClass().setAll("component-container");
		componentContainer.paddingProperty().bind(getSkinnable().componentPaddingProperty());
		componentContainer.spacingProperty().bind(getSkinnable().spacingProperty());
		componentContainer.getChildren().setAll(components);
		components.addListener(componentsListener);
		getSkinnable().typeProperty().addListener(typeListener);
		updateComponentContainer();
		getChildren().add(componentContainer);
	}
	
	private void updateComponentContainer() {
		switch (getSkinnable().getType()) {
		case NONE:
		case INSERT:
			componentContainer.setMinSize(FXBlockConstant.BLOCK_ROW_MIN_WIDTH, FXBlockConstant.BLOCK_ROW_MIN_HEIGHT);
			break;
		case NEXT:
			componentContainer.setMinSize(0, FXBlockConstant.NEXT_ROW_MIN_HEIGHT);
			break;
		case BRANCH:
			componentContainer.setMinSize(0, FXBlockConstant.BRANCH_ROW_COMPONENT_MIN_HEIGHT);
			break;
		}
	}
	
	protected double computeBlockX() {
		switch (getType()) {
		case INSERT:
			return getParentBlock().getConnectionType() == ConnectionType.LEFT
					? getSkinnable().getAlignedWidth() - FXBlockConstant.LEFT_WIDTH * 2
					: getSkinnable().getAlignedWidth() - FXBlockConstant.LEFT_WIDTH;
		case BRANCH:
			return getSkinnable().getComponentWidth();
		default:
			return 0;
		}
	}
	
	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
	}
	
	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
	}
	
	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		FXBlock block = getFXBlock();
		return block == null ? computeComponentContainerWidth() : block.getLayoutX() + computeChildPrefAreaWidth(block);
	}

	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
	}
	
	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
	}
	
	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		FXBlock block = getFXBlock();
		return Math.max(computeComponentContainerHeight(), block == null ? 0 : computeChildPrefAreaHeight(block));
	}

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		if (performingLayout)
			return;
		performingLayout = true;
		
		double top = contentY;
		double left = contentX;

		final double width = computeComponentContainerWidth(), height = computeComponentContainerHeight();
		layoutInArea(componentContainer, left, top, width, height, -1, null, HPos.LEFT, VPos.TOP);
		left += width;

		FXBlock block = getFXBlock();
		if (block != null && block.isManaged()) {
			layoutInArea(block, computeBlockX(), 0, block.prefWidth(-1), block.prefHeight(-1), -1, HPos.LEFT, VPos.TOP);
		}

		performingLayout = false;
	}

	private double computeChildPrefAreaWidth(Node child) {
		return snapSize(FXHelper.boundedSize(child.minWidth(-1), child.prefWidth(-1), child.maxWidth(-1)));
	}

	private double computeChildPrefAreaHeight(Node child) {
		return snapSize(FXHelper.boundedSize(child.minHeight(-1), child.prefHeight(-1), child.maxHeight(-1)));
	}

	@Override
	public void dispose() {
		components.removeListener(componentsListener);
		getSkinnable().blockProperty().removeListener(blockChangeListener);
		getChildren().removeListener(childrenListener);
		componentContainer.paddingProperty().unbind();
		componentContainer.spacingProperty().unbind();
		getSkinnable().typeProperty().removeListener(typeListener);
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
		double width = computeChildPrefAreaWidth(componentContainer);
		getSkinnable().setComponentWidth(width);
		return width;
	}
	
	private double computeComponentContainerHeight() {
		double height = computeChildPrefAreaHeight(componentContainer);
		getSkinnable().setComponentHeight(height);
		return height;
	}
}
