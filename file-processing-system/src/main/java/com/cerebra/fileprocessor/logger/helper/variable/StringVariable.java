package com.cerebra.fileprocessor.logger.helper.variable;

public enum StringVariable {

	YAML_TYPE("application/yaml"), JSON_TYPE("application/json"), CSV_TYPE("text/csv"), UTF_8("UTF-8"),
	UNDER_SCORE("_"), DASH("-"), SINGLE_SPACE(" "), EMPTY(""), FORWARD_SLASH("/"), SEMI_COLON(";"), COLON(":"),
	FULL_STOP("."), COMMA(","), DOUBLE_QUOTE("\""), LEFT_SQUARE_BRACKET("["), RIGHT_SQUARE_BRACKET("]"),
	NEW_LINE("\\n"), PIPE("|"), GOOGLE_CLOUD_STORAGE_PROTOCOL("gs://"), ASTERISK("*"), INT_02_FORMAT("%02d"),
	INT_03_FORMAT("%03d"), ARRAY("ARRAY");

	private String value;

	StringVariable(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}
}
