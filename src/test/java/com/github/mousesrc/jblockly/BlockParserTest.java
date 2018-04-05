package com.github.mousesrc.jblockly;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.mousesrc.jblockly.model.Block;
import com.github.mousesrc.jblockly.model.BlockParser;
import com.github.mousesrc.jblockly.model.BlockRow;

public class BlockParserTest {

	@Test
	public void test() {
		Block root = new Block();
		root.setName("root");
		
		BlockRow row1 = new BlockRow();
		root.addRow(row1, "row1");
		
		Block child = new Block();
		row1.setBlock(child);
		
		row1.getData().add("data1", 1);
		
		String json = BlockParser.toJson(root);
		System.out.println(json);
		
		Block deserialized = BlockParser.fromJson(json);
		int data1 = (int)deserialized.getRow("row1").get().getData().get("data1").get();
		
		System.out.println(BlockParser.toJson(deserialized));
	}

}
