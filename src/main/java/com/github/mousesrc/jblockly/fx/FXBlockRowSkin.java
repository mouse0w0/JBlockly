package com.github.mousesrc.jblockly.fx;

import java.util.List;

import com.github.mousesrc.jblockly.fx.FXBlockRow.Type;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;

public class FXBlockRowSkin extends SkinBase<FXBlockRow> {

	private ObservableList<Node> components;
	private ReadOnlyObjectWrapper<Bounds> componentBounds;
	private boolean removingBlock;
	private boolean performingLayout;

	protected FXBlockRowSkin(FXBlockRow control) {
		super(control);

		components = control.getComponents();
		componentBounds = control.componentBoundsPropertyImpl();

		initComponentsListener();
		initBlockListener();
	}
	
	private final ListChangeListener<Node> componentListener = new ListChangeListener<Node>() {

		@Override
		public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
			while (c.next()) {
				List<? extends Node> add = c.getAddedSubList();
				for (int i = 0, from = c.getFrom(), size = add.size(); i < size; i++)
					getChildren().add(i + from, c.getAddedSubList().get(i));

				getChildren().removeAll(c.getRemoved());
			}
			updateComponents();
		}

	};

	private void initComponentsListener() {
		getChildren().addAll(getSkinnable().getComponents());
		components.addListener(componentListener);
	}
	
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

	private FXBlock getFXBlock() {
		return getSkinnable().getFXBlock();
	}

	private FXBlock getParentBlock() {
		return getSkinnable().getParentBlock();
	}
	
	private Type getType(){
		return getSkinnable().getType();
	}
	
	protected double computeBlockX(){
		return getType() == Type.INSERT ? getSkinnable().getAlignedRenderWidth() - FXBlockConstant.LEFT_WIDTH : componentBounds.get().getWidth();
	}

	protected double computeComponentWidth(double topInset, double rightInset, double bottomInset, double leftInset) {
		if(components.isEmpty())
			return 0;
		
		double width = 0;
		for (Node node : components) {
			width += computeChildPrefAreaWidth(node, FXBlockRow.getMargin(node));
		}
		return leftInset + width + (components.size() - 1) * snapSpace(getSkinnable().getSpacing()) + rightInset;
	}

	protected double computeComponentHeight(double topInset, double rightInset, double bottomInset, double leftInset) {
		double height = 0;
		for (Node node : components) {
			double nodeHeight = computeChildPrefAreaHeight(node, FXBlockRow.getMargin(node));
			if (nodeHeight > height)
				height = nodeHeight;
		}
		return topInset + height + bottomInset;
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
		FXBlock block = getFXBlock();
		return block == null ? componentBounds.get().getWidth() : block.getLayoutX() + block.getWidth();
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		FXBlock block = getFXBlock();
		return Math.max(componentBounds.get().getHeight(), block == null ? 0 : block.getLayoutY() + block.getHeight());
	}

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		if (performingLayout)
			return;
		performingLayout = true;

		Insets padding = getSkinnable().getComponentPadding();
		double top = snapSpace(padding.getTop());
		double left = snapSpace(padding.getLeft());
		double space = snapSpace(getSkinnable().getSpacing());

		for (Node node : components) {
			Insets margin = FXBlockRow.getMargin(node);
			double width = computeChildPrefAreaWidth(node, margin), height = computeChildPrefAreaHeight(node, margin);
			layoutInArea(node, left, top, width, height, -1, margin, HPos.LEFT, VPos.TOP);
			left += width + space;
		}

		FXBlock block = getFXBlock();
		if (block != null) {
			layoutInArea(block, computeBlockX(), 0, computeChildPrefAreaWidth(block, null),
					computeChildPrefAreaHeight(block, null), -1, HPos.LEFT, VPos.TOP);
		}

		performingLayout = false;
	}

	private double computeChildPrefAreaWidth(Node child, Insets margin) {
		double left = margin != null ? snapSpace(margin.getLeft()) : 0;
		double right = margin != null ? snapSpace(margin.getRight()) : 0;
		return left + snapSize(child.prefWidth(-1)) + right;
	}

	private double computeChildPrefAreaHeight(Node child, Insets margin) {
		double top = margin != null ? snapSpace(margin.getTop()) : 0;
		double bottom = margin != null ? snapSpace(margin.getBottom()) : 0;
		return top + snapSize(child.prefHeight(-1)) + bottom;
	}

	@Override
	public void dispose() {
		super.dispose();
		components = null;
		componentBounds = null;
		components.removeListener(componentListener);
		getSkinnable().blockProperty().removeListener(blockChangeListener);
		getChildren().removeListener(childrenListener);
	}
}
