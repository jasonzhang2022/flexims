package com.flexdms.flexims.query;

public class OrderField {

	public String propname;

	// give a default value in case end user does not provide one.
	// otherwise, we could have an error when ConditionQuery tries to read the
	// OrderField
	public OrderDirection direction = OrderDirection.ASC;

	public OrderField() {
	}

	public OrderField(String propname, OrderDirection direction) {
		super();
		this.propname = propname;
		this.direction = direction;
	}

	public String getPropname() {
		return propname;
	}

	public void setPropname(String propname) {
		this.propname = propname;
	}

	public OrderDirection getDirection() {
		return direction;
	}

	public void setDirection(OrderDirection direction) {
		this.direction = direction;
	}

}
