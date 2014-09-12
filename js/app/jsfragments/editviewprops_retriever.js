/*
 * @ngdoc function
 * @name flexdms.parseEditViewProps
 * @module Global
 *
 * @description
 * return a list of property object for edit or view.
 *
 * @param {Array.Object|Array.String|String=} Can be a list name separate by ",", an array of name, or an array of property object. If null or undefined, 
 *  default property from type is returned.
 * @param {Object} type type resource Object
 * @param {boolean} edit edit or view
 * @returns  {Array.Object} a list of property object for edit or view.
 */
flexdms.parseEditViewProps=function(propsattr, type, edit){
	var props=new Array();
	if (angular.isDefined(propsattr)){
		if (angular.isString(propsattr)){
			angular.forEach(propsattr.split(/,\s*/), function(propname){
				var propobj=type.getProp(propname.trim());
				if (propobj){
					props.push(propobj);
				}
			});
		} else if (angular.isArray(propsattr) ){
			angular.forEach(propsattr, function(propname){
				var propobj=null;
				if (angular.isString(propname)) {
					propobj=type.getProp(propname.trim());
				} else if (propname instanceof flexdms.Property){
					propobj=propname;
				} else {
					//we do not know what kind of object this is.
				}
				if (propobj){
					props.push(propobj);
				}
			});
		}
	}
	if (props.length!=0){
		return props;
	}
	if (edit) {
		return type.getEditProps();
	} 
	return type.getViewProps();
};
