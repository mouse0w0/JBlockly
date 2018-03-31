package com.github.mousesrc.jblockly.fx.input;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class ComboBoxInputer<T> extends ComboBox<T> implements Inputer<T>{
	
	public final StringProperty nameProperty(){
		if(name == null)
			name = new SimpleStringProperty(this, "name");
		return name;
	}
	private StringProperty name;
	public final String getName() {return name == null ? null : nameProperty().get();}
	public final void setName(String name) {nameProperty().set(name);}

	public ComboBoxInputer() {
	}

	public ComboBoxInputer(ObservableList<T> items) {
		super(items);
	}
}
