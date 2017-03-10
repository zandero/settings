package com.zandero.settings;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class SettingFileParserTest {

	@Test
	public void testLoad() throws Exception {

		SettingFileParser parser = new SettingFileParser();

		URL resource = this.getClass().getResource("/settings.file");

		Settings settings = parser.load(resource.getFile());
		assertEquals(6, settings.size());

		assertEquals(1, settings.getInt("a"));

		assertEquals("value", settings.getString("test"));
		assertEquals("some string this is", settings.getString("normalString"));
		assertEquals("[very, very long \"string\"]  ", settings.getString("string"));

		assertEquals(true, settings.getBool("boolean"));

		String[] list = settings.getStrings("list");
		assertEquals(3, list.length);
		assertEquals("has", list[0]);
		assertEquals("some", list[1]);
		assertEquals("items", list[2]);

	}

	@Test
	public void testArguments() {
		SettingFileParser parser = new SettingFileParser();

		URL resource = this.getClass().getResource("/settings.file");

		String[] arguments = parser.arguments(resource.getFile());
		assertEquals(6, arguments.length);
		assertEquals("-a=1", arguments[0]);
		assertEquals("--boolean=true", arguments[1]);
		assertEquals("--test=value", arguments[2]);
		assertEquals("--string=[very, very long \"string\"]  ", arguments[3]);
		assertEquals("--normalString=some string this is", arguments[4]);
		assertEquals("--list=has,some,items", arguments[5]);
	}
}