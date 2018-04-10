package com.github.mousesrc.jblockly;

import com.github.mousesrc.jblockly.fx.ConnectionType;
import com.github.mousesrc.jblockly.fx.FXBlock;
import com.github.mousesrc.jblockly.fx.FXBlockRow;
import com.github.mousesrc.jblockly.fx.FXBlockWorkspace;
import com.github.mousesrc.jblockly.fx.input.TextFieldInputer;
import com.github.mousesrc.jblockly.fx.FXBlockRow.Type;
import com.github.mousesrc.jblockly.fx.util.BlockProvider;
import com.github.mousesrc.jblockly.fx.util.BlockProviderBase;
import com.github.mousesrc.jblockly.fx.util.BlockRegistry;
import com.github.mousesrc.jblockly.fx.util.WorkspaceStorage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class WorkspaceStorageTest extends Application{
	
	public static String storage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane borderPane = new BorderPane();
		
		BlockRegistry registry = initRegisty();
		
		//Workspaces
		FXBlockWorkspace workspace1 = new FXBlockWorkspace();
		workspace1.getBlockRegistry().setParent(registry);
		workspace1.getBlocks().addAll(
				methodBlock.create(),
				ifBlock.create(),
				printBlock.create(),
				returnBlock.create(),
				defineVarBlock.create(),
				varBlock.create(),
				setBlock.create(),
				equalBlock.create(),
				stringObjectBlock.create());
		
		FXBlockWorkspace workspace2 = new FXBlockWorkspace();
		workspace2.getBlockRegistry().setParent(registry);
		
		SplitPane splitPane = new SplitPane(workspace1, workspace2);
		
		//ToolBar
		ToolBar toolBar = new ToolBar();
		
		Button save = new Button("Save");
		save.setOnAction(event->{
			storage = WorkspaceStorage.saveToJson(workspace1);
			System.out.println(storage);
		});
		
		Button load = new Button("Load");
		load.setOnAction(event->WorkspaceStorage.loadFromJson(workspace2, storage));
		
		toolBar.getItems().addAll(save, load);
		
		borderPane.setTop(toolBar);
		borderPane.setCenter(splitPane);
		
		Scene scene = new Scene(borderPane);
		primaryStage.setScene(scene);
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.setTitle("JBlockly Demo");
		primaryStage.show();
	}
	
	private static BlockRegistry initRegisty() {
		BlockRegistry registry = new BlockRegistry();
		registry.register(methodBlock);
		registry.register(ifBlock);
		registry.register(printBlock);
		registry.register(returnBlock);
		registry.register(defineVarBlock);
		registry.register(varBlock);
		registry.register(setBlock);
		registry.register(equalBlock);
		registry.register(stringObjectBlock);
		return registry;
	}
	
	private static final BlockProvider methodBlock = new BlockProviderBase("method") {
		
		@Override
		public FXBlock create() {
			FXBlock block = preCreate();
			
			FXBlockRow head = new FXBlockRow();
			head.setName("head");
			
			TextFieldInputer methodName = new TextFieldInputer();
			methodName.setName("methodName");
			
			head.getComponents().addAll(new Label("方法"),methodName);
			
			FXBlockRow body = new FXBlockRow();
			body.setName("body");
			body.setType(Type.BRANCH);
			
			FXBlockRow end = new FXBlockRow();
			
			block.getFXRows().addAll(head, body, end);
			return block; 
		}
	};
	
	private static final BlockProvider ifBlock = new BlockProviderBase("if") {
		
		@Override
		public FXBlock create() {
			FXBlock block = preCreate();
			block.setConnectionType(ConnectionType.TOP);
			
			FXBlockRow head = new FXBlockRow();
			head.setName("head");
			head.setType(Type.INSERT);
			
			head.getComponents().addAll(new Label("如果"));
			
			FXBlockRow body = new FXBlockRow();
			body.setName("body");
			body.setType(Type.BRANCH);
			
			FXBlockRow none = new FXBlockRow();
			
			FXBlockRow next = new FXBlockRow();
			next.setName("next");
			next.setType(Type.NEXT);
			
			block.getFXRows().addAll(head, body, none, next);
			return block; 
		}
	};
	
	private static final BlockProvider printBlock = new BlockProviderBase("print") {
		
		@Override
		public FXBlock create() {
			FXBlock block = preCreate();
			block.setConnectionType(ConnectionType.TOP);
			
			FXBlockRow head = new FXBlockRow();
			head.setName("head");
			
			TextFieldInputer print = new TextFieldInputer();
			print.setName("print");
			
			head.getComponents().addAll(new Label("输出"), print);
			
			FXBlockRow next = new FXBlockRow();
			next.setName("next");
			next.setType(Type.NEXT);
			
			block.getFXRows().addAll(head, next);
			return block; 
		}
	};
	
	private static final BlockProvider returnBlock = new BlockProviderBase("return") {
		
		@Override
		public FXBlock create() {
			FXBlock block = preCreate();
			block.setConnectionType(ConnectionType.TOP);
			
			FXBlockRow head = new FXBlockRow();
			
			head.getComponents().addAll(new Label("返回"));
			
			block.getFXRows().addAll(head);
			return block; 
		}
	};
	
	private static final BlockProvider defineVarBlock = new BlockProviderBase("defineVar") {
		
		@Override
		public FXBlock create() {
			FXBlock block = preCreate();
			block.setConnectionType(ConnectionType.TOP);
			
			FXBlockRow head = new FXBlockRow();
			head.setName("head");
			head.setType(Type.INSERT);
			
			TextFieldInputer varName = new TextFieldInputer();
			varName.setName("varName");
			
			head.getComponents().addAll(new Label("变量"), varName);
			
			FXBlockRow next = new FXBlockRow();
			next.setName("next");
			next.setType(Type.NEXT);
			
			block.getFXRows().addAll(head, next);
			return block; 
		}
	};
	
	private static final BlockProvider varBlock = new BlockProviderBase("var") {
		
		@Override
		public FXBlock create() {
			FXBlock block = preCreate();
			block.setConnectionType(ConnectionType.LEFT);
			
			FXBlockRow head = new FXBlockRow();
			head.setName("head");
			head.setType(Type.INSERT);
			
			TextFieldInputer varName = new TextFieldInputer();
			varName.setName("varName");
			
			head.getComponents().addAll(new Label("变量"), varName);
			
			block.getFXRows().addAll(head);
			return block; 
		}
	};
	
	private static final BlockProvider setBlock = new BlockProviderBase("set") {
		
		@Override
		public FXBlock create() {
			FXBlock block = preCreate();
			block.setConnectionType(ConnectionType.LEFT);
			
			FXBlockRow head = new FXBlockRow();
			head.setName("head");
			head.setType(Type.INSERT);
			
			head.getComponents().addAll(new Label("="));
			
			block.getFXRows().addAll(head);
			return block; 
		}
	};
	
	private static final BlockProvider equalBlock = new BlockProviderBase("equal") {
		
		@Override
		public FXBlock create() {
			FXBlock block = preCreate();
			block.setConnectionType(ConnectionType.LEFT);
			
			FXBlockRow head = new FXBlockRow();
			head.setName("head");
			head.setType(Type.INSERT);
			
			head.getComponents().addAll(new Label("=="));
			
			block.getFXRows().addAll(head);
			return block; 
		}
	};
	
	private static final BlockProvider stringObjectBlock = new BlockProviderBase("stringObject") {
		
		@Override
		public FXBlock create() {
			FXBlock block = preCreate();
			block.setConnectionType(ConnectionType.LEFT);
			
			FXBlockRow head = new FXBlockRow();
			head.setName("head");
			
			TextFieldInputer value = new TextFieldInputer();
			value.setName("value");
			
			head.getComponents().addAll(new Label("\""),value,new Label("\""));
			
			block.getFXRows().addAll(head);
			return block; 
		}
	};
}
