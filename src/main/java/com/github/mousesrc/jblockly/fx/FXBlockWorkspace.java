package com.github.mousesrc.jblockly.fx;

import java.util.Objects;

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
	public final ReadOnlyObjectProperty<FXBlock> movingBlockProperty() {return movingBlockPropertyImpl().getReadOnlyProperty();}
	public final FXBlock getMovingBlock() {return movingBlock == null ? null : movingBlock.get();}
	protected final void setMovingBlock(FXBlock block) {movingBlockPropertyImpl().set(block);}
	
	private final ObservableList<FXBlock> blocks = FXCollections.observableArrayList();
	
	private static final String DEFAULT_STYLE_CLASS = "block-workspace";
	
	public FXBlockWorkspace() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
		initBlocksListener();
	}
	
	private boolean changingBlock = false;
	private void initBlocksListener(){
		getChildren().addAll(getBlocks());
		getBlocks().addListener(new ListChangeListener<FXBlock>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FXBlock> c) {
				while(c.next()){
					if(!changingBlock){
						changingBlock = true;
						getChildren().addAll(c.getAddedSubList());
						getChildren().removeAll(c.getRemoved());
						changingBlock = false;
					}
				}
			}
		});
		getChildren().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while(c.next()){
					if(!changingBlock){
						changingBlock = true;
						for(Node node:c.getAddedSubList())
							if(node instanceof FXBlock)
								getBlocks().add((FXBlock) node);
						getBlocks().removeAll(c.getRemoved());
						changingBlock = false;
					}
				}
			}
			
		});
	}
	
	public ObservableList<FXBlock> getBlocks(){
		return blocks;
	}
	
	@Override
	public ConnectionResult connect(FXBlock block, Bounds bounds) {
		Objects.requireNonNull(bounds);
		for (FXBlock b : getBlocks()) {
			ConnectionResult result = b.connect(block, bounds);
			if(result != ConnectionResult.FAILURE)
				return result;
		}
		return ConnectionResult.FAILURE;
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
