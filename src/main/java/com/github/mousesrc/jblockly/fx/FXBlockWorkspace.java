package com.github.mousesrc.jblockly.fx;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public class FXBlockWorkspace extends Region implements BlockWorkspaceHolder, Connectable{
	
	private ReadOnlyObjectWrapper<FXBlockWorkspace> workspace = new ReadOnlyObjectWrapper<FXBlockWorkspace>(this);
	public final ReadOnlyObjectProperty<FXBlockWorkspace> workspaceProperty() {return workspace.getReadOnlyProperty();}

	private ReadOnlyObjectWrapper<FXBlock> movingBlock;
	protected final ReadOnlyObjectWrapper<FXBlock> movingBlockPropertyImpl() {
		if(movingBlock == null)
			movingBlock = new ReadOnlyObjectWrapper<>(this, "movingBlock");
		return movingBlock;
	}
	public final ReadOnlyObjectProperty<FXBlock> movingBlockProperty() {
		return movingBlockPropertyImpl().getReadOnlyProperty();
	}
	public final FXBlock getMovingBlockProperty() {
		return movingBlock == null ? null : movingBlock.get();
	}
	protected final void setMovingBlockProperty(FXBlock block) {
		movingBlockPropertyImpl().set(block);
	}
	
	private final ObservableList<FXBlock> blocks = FXCollections.observableArrayList();
	
	private static final String DEFAULT_STYLE_CLASS = "block-workspace";
	
	public FXBlockWorkspace() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
		initBlocksListener();
	}
	
	private boolean removingBlock = false;
	private void initBlocksListener(){
		getChildren().addAll(getBlocks());
		getBlocks().addListener(new ListChangeListener<FXBlock>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FXBlock> c) {
				while(c.next()){
					getChildren().addAll(c.getAddedSubList());
					if(!removingBlock && !c.getRemoved().isEmpty()){
						removingBlock = true;
						getChildren().removeAll(c.getRemoved());
						removingBlock = false;
					}
				}
			}
		});
		getChildren().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while(c.next()){
					if(!removingBlock && !c.getRemoved().isEmpty()){
						removingBlock = true;
						getBlocks().removeAll(c.getRemoved());
						removingBlock = false;
					}
				}
			}
			
		});
	}
	
	public ObservableList<FXBlock> getBlocks(){
		return blocks;
	}
	
	@Override
	public boolean connect(FXBlock block, Bounds bounds) {
		if(bounds == null)
			return false;
		for (FXBlock b : getBlocks())
			if (b.connect(block, bounds))
				return true;
		return false;
	}
	
	@Override
	protected double computePrefWidth(double height) {
		double width = 0;
		for (Node node : getManagedChildren()) {
			double nodeWidth = node.getLayoutX() + node.prefHeight(-1);
			if(nodeWidth > width)
				width = nodeWidth;
		}
		return width;
	}

	@Override
	protected double computePrefHeight(double width) {
		double height = 0;
		for (Node node : getManagedChildren()) {
			double nodeHeight = node.getLayoutY() + node.prefHeight(-1);
			if(nodeHeight > height)
				height = nodeHeight;
		}
		return height;
	}
}
