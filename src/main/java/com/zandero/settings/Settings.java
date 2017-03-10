package com.zandero.settings;

import com.zandero.utils.Assert;
import com.zandero.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class to derive settings classes from
 */
public class Settings extends HashMap<String, Object> {

	private static final long serialVersionUID = 3853507321943751725L;

	public String getString(String name) {

		return get(name).toString();
	}

	public String asString(String name) {

		Object value = get(name);
		if (value instanceof String ||
			value instanceof Boolean ||
			value instanceof Integer) {
			return getString(name);
		}

		if (value instanceof String[]) {
			String[] list = getStrings(name);
			return StringUtils.join(list, ",");
		}

		return get(name).toString();
	}

	public String findString(String name) {

		return (String) find(name);
	}

	public int getInt(String name) {

		Object value = super.get(name);

		if (value instanceof Integer) {
			return (Integer) value;
		}

		if (value == null) {
			throw new IllegalArgumentException("Setting: '" + name + "', not found!");
		}

		try {
			return Integer.parseInt(value.toString());
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Setting: '" + name + "', can't be converted to integer: '" + value + "'!");
		}
	}

	public Integer findInt(String name) {

		try {
			return getInt(name);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}

	public String[] getStrings(String name) {

		Object value = super.get(name);
		if (value instanceof String[]) {
			return (String[]) value;
		}

		throw new IllegalArgumentException("Setting: '" + name + "', can't be converted to string array: '" + value + "'!");
	}

	public boolean getBool(String name) {

		Object value = super.get(name);
		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value == null) {
			throw new IllegalArgumentException("Setting: '" + name + "', not found!");
		}

		throw new IllegalArgumentException("Setting: '" + name + "', can't be converted to boolean: '" + value + "'!");
	}

	private Object get(String name) {

		Object value = super.get(name);
		if (value == null) {
			throw new IllegalArgumentException("Setting: '" + name + "', not found!");
		}

		return value;
	}

	public Object find(String name) {
		// doesn't throw exception if setting not found ... simply returns null
		return super.get(name);
	}

	public <T> List<T> getList(String name, Class<T> type) {

		Object value = super.get(name);

		// make sure correct objects are returned
		if (value instanceof List) {

			ArrayList<T> output = new ArrayList<>();

			List list = (List) value;
			for (Object item : list) {
				output.add(type.cast(item));
			}

			return output;
		}

		throw new IllegalArgumentException("Setting: '" + name + "', can't be converted to Map<String, Integer>: '" + value + "'!");
	}

	public static class Builder {

		private static final Logger log = LoggerFactory.getLogger(Builder.class);

		private final Settings settings;

		public Builder() {

			settings = new Settings();
		}

		public Builder(Settings existing) {

			settings = existing;
		}

		public Builder add(String name, String value) {

			check(name, value);

			put(name, value);
			return this;
		}

		public Builder add(String name, int value) {

			check(name, value);

			put(name, value);
			return this;
		}

		public Builder add(String name, String[] list) {

			check(name, list);
			Assert.isTrue(list.length > 0, "Can't add empty list!");

			put(name, list);
			return this;
		}

		public Builder add(String name, List<?> list) {

			check(name, list);
			Assert.isTrue(list.size() > 0, "Can't add empty list!");

			put(name, list);
			return this;
		}

		public Builder add(String name, boolean value) {

			check(name, value);
			put(name, value);
			return this;
		}

		public Settings build() {

			return settings;
		}

		private void check(String name, Object value) {

			Assert.notNullOrEmptyTrimmed(name, "Missing name!");
			Assert.notNull(value, "Missing value!");
		}

		private void put(String name, Object value) {

			if (settings.find(name) != null) {
				log.info("Overriding setting: " + name + ", with: " + value);
			}

			settings.put(name.trim(), value);
		}

		/**
		 * take settings from newSettings and override all existing if any
		 *
		 * @param newSettings map of settings
		 */
		public void override(Settings newSettings) {

			if (newSettings != null) {

				for (String name : newSettings.keySet()) {
					Object value = newSettings.get(name);

					if (value != null) {
						settings.put(name, value);
					}
				}
			}
		}

		public boolean isEmpty() {

			return settings.isEmpty();
		}

		public boolean contains(String name) {

			return settings.containsKey(name);
		}

		public Object get(String name) {

			return settings.get(name);
		}
	}
}
