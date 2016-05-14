package org.eufraten.trelloreporter.xls;

import org.apache.commons.lang3.StringUtils;

public class DataRow {
	private String label;
	private String data;

	public DataRow(String label, String data) {
		this.label = label;
		this.data = data;
	}

	float getDataLineCount() {
		if (StringUtils.isBlank(data)) {
			return 1;
		}

		int lines = data.split("\r\n|\r|\n").length;
		return lines;
	}

	public String getLabel() {
		if (StringUtils.isBlank(label)) {
			return "";
		}
		return label + ":";
	}

	public String getData() {
		return data;
	}

}