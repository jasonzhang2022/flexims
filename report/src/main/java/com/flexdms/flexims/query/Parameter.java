package com.flexdms.flexims.query;

public interface Parameter {

	String getName();

	String getDescription();

	Object getValue();

	Object getDefaultValue();

	void setValue(Object value);

	// value suitable to build query
	Object getValueForQuery();

	String getPropertyName();

}
