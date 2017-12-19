package com.zandero.settings;

import com.zandero.utils.Assert;
import com.zandero.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Base class to derive settings classes from
 * Glorified HashMap with value checking
 */
public class Settings extends HashMap<String, Object> {

	private static final long serialVersionUID = 3853507321943751725L;

	/**
	 * Get setting as String
	 *
	 * @param name setting key
	 * @return setting value as String
	 *
	 * @throws IllegalArgumentException is setting is not present
	 */
	public String getString(String name) {

		Object value = get(name);
		if (value instanceof String ||
			value instanceof Boolean ||
			value instanceof Integer) {
			return value.toString();
		}

		if (value instanceof String[]) {
			String[] list = getStrings(name);
			return StringUtils.join(list, ",");
		}

		return get(name).toString();
	}

	/**
	 * Searches for setting by name
	 *
	 * @param name to search for
	 * @return found setting as String or null if not found
	 */
	public String findString(String name) {

		try {
			return getString(name);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Get setting as integer
	 *
	 * @param name setting key
	 * @return setting value as integer
	 *
	 * @throws IllegalArgumentException is setting is not present or can not be converted to an integer
	 */
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

	/**
	 * Finds setting as Integer
	 *
	 * @param name setting key
	 * @return setting value as integer or null if not found (or in invalid format)
	 */
	public Integer findInt(String name) {

		try {
			return getInt(name);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Returns setting as array of Strings
	 * @param name of setting
	 * @return Array of Strings
	 * @throws IllegalArgumentException in case setting is not an array
	 */
	public String[] getStrings(String name) {

		Object value = super.get(name);
		if (value instanceof String[]) {
			return (String[]) value;
		}

		throw new IllegalArgumentException("Setting: '" + name + "', can't be converted to string array: '" + value + "'!");
	}

	/**
	 * Returns boolean flag
	 * @param name of flag
	 * @return true or false
	 * @throws IllegalArgumentException in case setting is not a boolean setting
	 */
	public boolean getBool(String name) {

		Object value = super.get(name);

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof String) {
			String txt = (String)value;
			txt = txt.trim().toLowerCase();

			if ("yes".equals(txt) || "y".equals(txt) || "1".equals(txt) || "true".equals(txt) || "t".equals(txt)) {
				return true;
			}

			if ("no".equals(txt) || "n".equals(txt) || "0".equals(txt) || "false".equals(txt) || "f".equals(txt)) {
				return false;
			}
		}

		if (value == null) {
			throw new IllegalArgumentException("Setting: '" + name + "', not found!");
		}

		throw new IllegalArgumentException("Setting: '" + name + "', can't be converted to boolean: '" + value + "'!");
	}

	public Boolean findBool(String name) {

		try {
			return getBool(name);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Gets setting or throws an IllegalArgumentException if not found
	 *
	 * @param name of setting to search for
	 * @return found value
	 *
	 * @throws IllegalArgumentException in case setting is not present
	 */
	private Object get(String name) {

		Object value = find(name);
		if (value == null) {
			throw new IllegalArgumentException("Setting: '" + name + "', not found!");
		}

		return value;
	}

	/**
	 * Finds setting if any
	 *
	 * @param name of setting to search for
	 * @return found value or null if not found
	 */
	public Object find(String name) {
		// doesn't throw exception if setting not found ... simply returns null
		return super.get(name);
	}

	/**
	 * Returns setting a list of objects
	 * @param name setting name
	 * @param type type of object
	 * @param <T> type
	 * @return list of found setting
	 * @throws IllegalArgumentException in case settings could not be converted to given type
	 */
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

		throw new IllegalArgumentException("Setting: '" + name + "', can't be converted to List<" + type.getName() + ">: '" + value + "'!");
	}

	/**
	 * Builder to set up a setting collection
	 */
	public static class Builder {

		private final Settings settings;

		private Logger log = Logger.getLogger(Builder.class.getName());

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
