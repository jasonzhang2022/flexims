package com.flexdms.flexims.query;

import com.flexdms.flexims.util.DescribedEnum;

public enum Operator implements DescribedEnum {

	/*
	 * for primtive value: lt, le, eq, gt, ge, ne, oneof(in in sql),
	 * notoneof(notin in sql), between, notbetween, like, notlike, oneof,
	 * notoneof expect a collection of simple value.
	 * 
	 * like, notlike, eq, ne are for string,
	 * 
	 * size judgement for collection: size, sizeGt, sizeLt, empty, notempty
	 * 
	 * 
	 * for relation: collection: size judgement, oneof, notoneof, ne, eq, for
	 * single value" oneof, notoneof, eq, ne, notnull, isnull
	 * 
	 * Tracedown
	 */

	lt("<"), le("<="), eq("="), gt(">"), ge(">="), ne("!="), oneof("contains"), // for
																				// direct
																				// field
	notoneof("not contain"), // for simple field
	between("between"), notbetween("not between"), checked("checked"), unchecked("unchecked"), like("like"), notlike("not like"), notnull("has value"), isnull(
			"no value"),

	size("number of elements"), sizeGt("number of elements more than"), sizeLt("number of elements less than"), empty("empty"), notempty("not empty"),

	tracedown("trace down");
	private final String op;

	private Operator(String op) {
		this.op = op;
	}

	@Override
	public String getSymbol() {
		return op;
	}

	public static Operator fromSymbol(String op) {
		for (Operator o : Operator.values()) {
			if (o.getSymbol().equalsIgnoreCase(op)) {
				return o;
			}
		}
		return null;
	}

	public boolean isSizeOperator() {
		if (this == size || this == sizeGt || this == sizeLt || this == empty || this == notempty) {
			return true;
		}
		return false;
	}

	public static int requiredValues(Operator op) {
		switch (op) {
		case lt:
		case le:
		case eq:
		case gt:
		case ge:
		case ne:
		case like:
		case notlike:
		case tracedown:
		case size:
		case sizeGt:
		case sizeLt:
			return 1;
		case checked:
		case unchecked:
		case notnull:
		case isnull:
		case empty:
			return 0;
		case between:
		case notbetween:
			return 2;
		default:
			return 3;
		}
	}
}
