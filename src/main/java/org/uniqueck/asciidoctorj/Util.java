package org.uniqueck.asciidoctorj;

import org.apache.commons.lang3.StringUtils;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.extension.Processor;
import org.asciidoctor.log.LogRecord;
import org.asciidoctor.log.Severity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Util {

	private Util() {
		//
	}

	public static String getStringAttributeValueOrDefault(final Map<String, Object> attributes, final String key, final String defaultValue) {
		final String result = getStringAttributeValue(attributes, key);
		return (result != null) ? result : defaultValue;
	}

	public static String getStringAttributeValue(final Map<String, Object> attributes, final String key) {
		if (attributes == null || StringUtils.isBlank(key)) {
			return null;
		}

		final Object value = attributes.get(StringUtils.trim(key));
		if (value != null) {
			if (value instanceof String) {
				return (String) value;
			} else {
				return value.toString();
			}
		}

		return null;
	}

}
