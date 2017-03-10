package com.zandero.settings;

import com.zandero.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 */
public class SettingFileParser {

	/**
	 * Loads name value pairs directly from given file ... adding them as settings
	 *
	 * @param file to get settings from
	 * @return list of settings
	 */
	public Settings load(String file) {
		try {

			Scanner s = new Scanner(new File(file));

			ArrayList<String> list = new ArrayList<>();

			while (s.hasNextLine()) {
				list.add(s.nextLine());
			}

			s.close();

			return parse(list);

		}
		catch (FileNotFoundException e) {

			// TODO
			e.printStackTrace();
		}

		return null;
	}


	public String[] arguments(String file) {

		Settings settings = load(file);

		// convert to list of arguments ...
		List<String> output = new ArrayList<>();
		for (String key : settings.keySet()) {

			String prefix = "-";
			if (key.length() > 1) {
				prefix = "--";
			}

			output.add(prefix + key + "=" + settings.asString(key));
		}

		return output.toArray(new String[output.size()]);
	}


	/**
	 * Expects name = value in each line
	 * ignores lines starting with '#' or '//'
	 *
	 * @param list of strings
	 */
	private Settings parse(ArrayList<String> list) {

		Settings.Builder builder = new Settings.Builder();

		if (list != null && list.size() > 0) {
			list.forEach(line -> {
				line = StringUtils.trimToNull(line);
				if (line != null && !isComment(line)) {
					String[] items = line.split("=");

					if (items.length == 2) {
						String name = items[0];
						String value = items[1];

						parseAndAdd(builder, name, value);
					}
				}
			});
		}

		return builder.build();
	}

	private void parseAndAdd(Settings.Builder builder, String name, String value) {

		if (StringUtils.isNullOrEmptyTrimmed(name) ||
			StringUtils.isNullOrEmptyTrimmed(value)) {
			return;
		}

		name = name.trim();
		value = value.trim();

		Integer integer = asInteger(value);
		if (integer != null) {
			builder.add(name, integer);
			return;
		}

		Boolean bool = asBoolean(value);
		if (bool != null) {
			builder.add(name, bool);
			return;
		}

		String[] list = asList(value);
		if (list != null) {
			builder.add(name, list);
			return;
		}

		// last resort ... ad as String
		builder.add(name, asString(value));
	}

	private String asString(String value) {

		// unquote if given in quotes
		if (value.length() > 2 && value.startsWith("\"") && value.endsWith("\"")) {
			return value.substring(1, value.length() - 1);
		}

		// return as is
		return value;
	}

	private Boolean asBoolean(String value) {

		if ("true".equals(value.toLowerCase()) || "false".equals(value.toLowerCase())) {
			return "true".equals(value.toLowerCase());
		}
		return null;
	}

	private Integer asInteger(String value) {

		try {
			return Integer.valueOf(value);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	protected String[] asList(String value) {

		if (value.length() > 2 && value.startsWith("[") && value.endsWith("]")) {

			value = value.substring(1, value.length() - 1);
			String[] items = value.split(",");

			// clean up items ... trim them
			List<String> output = new ArrayList<>();
			for (String item : items) {
				String itemValue = StringUtils.trimToNull(item);

				if (itemValue != null)
					output.add(itemValue);
			}

			return output.toArray(new String[output.size()]);
		}

		return null;
	}

	private boolean isComment(String line) {
		return line != null && (line.startsWith("#") || line.startsWith("//"));
	}
}
