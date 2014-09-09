
/**
 * Produce tooltip for edit
 */
flexdms.editTooltip= function(prop, fxTemplates) {
	var msgs = new Array();
	if (prop.getTooltip() != null) {
		msgs.push(prop.getTooltip());
	}
	if (prop.getTypeObject().isStringType()){
//		if (prop.getMinLen() != null) {
//			msgs.push("Minimal number of characters:"
//					+ prop.getMinLen());
//		}
//		if (prop.getMaxLen() != null) {
//			msgs.push("Maximal number of characters:"
//					+ prop.getMaxLen());
//		}
		if (prop.getPatExp() != null) {
			msgs.push("Text Pattern:" + prop.getPattern());
		}
	}
	
	if (prop.getTypeObject().isNumber()){
//		if (prop.getMinValue() != null) {
//			msgs.push("Minimal Value:" + prop.getMinValue());
//		}
//		if (prop.getMaxValue() != null) {
//			msgs.push("Maximal Value:" + prop.getMaxValue());
//		}
	}
	
	if (fxTemplates.useSingleFieldForMultiple(prop)
			&& prop.isElementCollection() && !prop.isEmbeddedElementCollection()) {
		msgs.push("Separate multiple values by ,");
	}
	return msgs;
};
/**
 * Tooltip for view label
 */
flexdms.viewTooltip= function(prop) {
	var msgs = new Array();
	if (prop.getTooltip() != null) {
		msgs.push(prop.getTooltip());
	}
	return msgs;
};
/**
 * How format the tooltip
 */
flexdms.htmlTtooltip=function(msgs){
	
	if (msgs.length==0){
		return false;
	} else if (msgs.length==1) {
		return msgs[0];
	} else {
		var msg="<ul>";
		angular.forEach(msgs, function(m){
			msg+="<li>"+m+"</li>";
		});
		msg+="</ul>";
		return msg;
	}
};