package com.github.mousesrc.jblockly.fx.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class BlockRowEvent extends Event {

	 public static final EventType<Event> ANY = new EventType<>(Event.ANY, "BLOCK_ROW");
	 
	public BlockRowEvent(EventTarget source, EventType<? extends Event> eventType) {
		super(source, source, eventType);
	}

}
