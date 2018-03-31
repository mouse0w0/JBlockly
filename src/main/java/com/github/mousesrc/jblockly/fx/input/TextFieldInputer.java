package com.github.mousesrc.jblockly.fx.input;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

public class TextFieldInputer extends TextField implements Inputer<String>{

	public final StringProperty nameProperty(){
		if(name == null)
			name = new SimpleStringProperty(this, "name");
		return name;
	}
	private StringProperty name;
	public final String getName() {return name == null ? null : nameProperty().get();}
	public final void setName(String name) {nameProperty().set(name);}
	
	@Override
	public String getValue() {
		return getText();
	}

	@Override
	public void setValue(String value) {
		setText(value);
	}
	
	public TextFieldInputer() {
	}

	public TextFieldInputer(String text) {
		super(text);
	}
}
