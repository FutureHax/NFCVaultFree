package com.t3hh4xx0r.nfcvault;

import java.io.Serializable;

import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class Password implements Serializable {

	@Override
	public String toString() {
		return "Password [dataStack=" + dataStack + ", dataValue=" + dataValue
				+ ", dataTitle=" + dataTitle + "]";
	}

	public String getDataStack() {
		return dataStack;
	}

	public void setDataStack(String dataStack) {
		this.dataStack = dataStack;
	}

	public String getDataValue() {
		return dataValue;
	}

	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}

	public String getDataTitle() {
		return dataTitle;
	}

	public void setDataTitle(String dataTitle) {
		this.dataTitle = dataTitle;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = -187141204874302467L;

	public Password(String stack, String value, String title) {
		this.dataStack = stack;
		this.dataValue = value;
		this.dataTitle = title;
	}

	String dataStack;
	String dataValue;
	String dataTitle;
	
	String parseId;

	public String getParseId() {
		return parseId;
	}

	public void setParseId(String parseId) {
		this.parseId = parseId;
	}

	public ParseObject toParsePassword() {
		ParseObject o = new ParseObject("Password");
		o.put("data_value", dataValue);
		o.put("data_stack", dataStack);
		o.put("data_title", dataTitle);
		o.put("key_owner", ParseUser.getCurrentUser().getEmail());
		o.setACL(new ParseACL(ParseUser.getCurrentUser()));
		return o;
	}
}
