package com.zandero.settings;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class SettingsTest {

	Settings settings = new Settings();

	@Before
	public void startUp() {
		settings.clear();
	}

	@Test
	public void getStringTest() {

		settings.put("test", "value");

		assertEquals("value", settings.get("test"));
		assertEquals("value", settings.getString("test"));
	}

	@Test
	public void notFoundTest() {

		try {
			settings.getString("test");
			fail();
		}
		catch (IllegalArgumentException e) {
			assertEquals("Setting: 'test', not found!", e.getMessage());
		}
	}
}