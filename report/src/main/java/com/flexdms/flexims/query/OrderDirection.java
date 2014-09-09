package com.flexdms.flexims.query;

import com.flexdms.flexims.util.DescribedEnum;

public enum OrderDirection implements DescribedEnum {
	ASC("ascending"), DESC("descending");
	private final String symbol;

	private OrderDirection(String op) {
		this.symbol = op;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	public static OrderDirection fromSymbol(String op) {
		if (op.equalsIgnoreCase(ASC.symbol)) {
			return ASC;
		}
		return DESC;
	}
}
