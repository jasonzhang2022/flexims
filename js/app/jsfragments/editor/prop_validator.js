	
/**
 * Decorate input with validation. 
 */
angular.module("instDirective").directive("fxPropValidator",function($compile,$templateCache, fxTemplates){
	
	function resetError(ctrl){
		for(var key in ctrl.$error){
			ctrl.$setValidity(key, true);
		}
	}
	
	function validateSingleValue(prop, viewValue1, ctrl){
		var proptype=prop.getTypeObject();
		
		var viewValue=viewValue1;
		//check type
		var typepat=null;
		var errorkey=null;
		if (proptype.isDateTime()){
			return viewValue;
		}
		if (proptype.isURL()){
			typepat=flexdms.URL_REGEXP;
			errorkey="url";
		}else if (proptype.isInteger()){
			errorkey="integer";
			typepat=flexdms.INTEGER_REGEXP;
		} else if (proptype.isNumber()){
			errorkey="number";
			typepat=flexdms.NUMBER_REGEXP;
		} else if (proptype.isEmail()){
			errorkey="email";
			typepat=flexdms.EMAIL_REGEXP;
		} else if (proptype.isRelation()){
			errorkey="integer";
			typepat=flexdms.INTEGER_REGEXP;
		}
		if (typepat!=null){
			if (!typepat.test(viewValue)) {
				ctrl.$setValidity(errorkey, false);
				return undefined;
			}
		}

		var strpat=prop.getPatExp();
		if (strpat!=null){
			if (!strpat.test(viewValue)) {
				ctrl.$setValidity("pattern", false);
				return undefined;
			}
		}
		var minlength=prop.getMinLen();
		if (minlength!=null){
			var m=minlength-0;
			if (viewValue.length<m) {
				ctrl.$setValidity("minlength", false);
				return undefined;
			}
		}
		var maxlength=prop.getMaxLen();
		if (maxlength!=null){
			var m=maxlength-0;
			if (viewValue.length>m) {
				ctrl.$setValidity("maxlength", false);
				return undefined;
			}
		}
		if (proptype.isNumber()){
			viewValue=proptype.parse(viewValue);
		}
		//number is expect for relation
		if (proptype.isRelation()){
			viewValue=viewValue-0;
		}
		var min=prop.getMinValue();
		if (min!=null){
			var m=min-0;
			if (viewValue<m) {
				ctrl.$setValidity("min", false);
				return undefined;
			}
		}
		var max=prop.getMaxValue();
		if (max!=null){
			var m=max-0;
			if (viewValue>m) {
				ctrl.$setValidity("max", false);
				return undefined;
			}
		}
		return viewValue;
		
	}
	
	function validateSingleModelValue(prop, modelValue1, ctrl){
		var proptype=prop.getTypeObject();
		
		var modelValue=modelValue1;
		//check type
		var typepat=null;
		var errorkey=null;
		
		
		//does the modelValue has correct type.
//		if (proptype.isDateTime()){
//			if (!(modelValue instanceof Date)){
//				ctrl.$setValidity("date", false);
//				return undefined;
//			}
//			return modelValue;
//		}
		//number type
		if (proptype.isNumber() && !angular.isNumber(modelValue)){
			if (proptype.isInteger()){
				ctrl.$setValidity("integer", false);
				return undefined;
			} 
			ctrl.$setValidity("number", false);
			return undefined;
		}
		
		if (proptype.isRelation()&& !angular.isNumber(modelValue)) {
			ctrl.$setValidity("integer", false);
			return undefined;
		}
		
		if (proptype.isURL()){
			typepat=flexdms.URL_REGEXP;
			errorkey="url";
		}else if (proptype.isInteger()){
			errorkey="integer";
			typepat=flexdms.INTEGER_REGEXP;
		} else if (proptype.isNumber()){
			errorkey="number";
			typepat=flexdms.NUMBER_REGEXP;
		} else if (proptype.isEmail()){
			errorkey="email";
			typepat=flexdms.EMAIL_REGEXP;
		} else if (proptype.isRelation()){
			errorkey="integer";
			typepat=flexdms.INTEGER_REGEXP;
		}
		if (typepat!=null){
			if (!typepat.test(modelValue)) {
				ctrl.$setValidity(errorkey, false);
				return undefined;
			}
		}
		var strpat=prop.getPatExp();
		if (strpat!=null){
			if (!strpat.test(modelValue)) {
				ctrl.$setValidity("pattern", false);
				return undefined;
			}
		}
		var strvalue=modelValue+"";
		var minlength=prop.getMinLen();
		if (minlength!=null){
			var m=minlength-0;
			
			if (strvalue.length<m) {
				ctrl.$setValidity("minlength", false);
				return undefined;
			}
		}
		var maxlength=prop.getMaxLen();
		if (maxlength!=null){
			var m=maxlength-0;
			if (strvalue.length>m) {
				ctrl.$setValidity("maxlength", false);
				return undefined;
			}
		}
	
		var min=prop.getMinValue();
		if (min!=null){
			var m=min-0;
			if (modelValue<m) {
				ctrl.$setValidity("min", false);
				return undefined;
			}
		}
		var max=prop.getMaxValue();
		if (max!=null){
			var m=max-0;
			if (modelValue>m) {
				ctrl.$setValidity("max", false);
				return undefined;
			}
		}
		
		//should we return a string?
		return modelValue;
		
	}
	
	function isEmptyValue(a){
		return !angular.isDefined(a)||a===null||a===''?true:false;
	}
	function isEmptyArray(a){
		
		if (!angular.isDefined(a)||a===null){
			return true;
		}
		if (!angular.isArray(a)){
			return false;
		}
		
		if (a.length==0){
			return true;
		}
		var hasValue=false;
		for (var i=0; i<a.length; i++){
			if (angular.isDefined(a[i]) && a[i]!==null ){
				hasValue=true;
				break;
			}
		}
			
		return !hasValue;
	}

	return {
		require:"ngModel",
		restrict: 'A',
	    link: function(scope, iElement, iAttrs, ctrl, transcludeFn) {
	    	ctrl.$parsers.push(function(viewValue1) {
	    		var viewValue=viewValue1;
	    		resetError(ctrl);
	    		var prop=scope.propobj;
	    		
	    		if ( (ctrl.$isEmpty(viewValue) || 
	    						(typeof(viewValue)=='string' && viewValue.trim().length==0))){
	    			if (prop.isRequired() &&(prop.isBasic() ||fxTemplates.useSingleFieldForMultiple(prop) )){
	    				ctrl.$setValidity("required", false);
	    				return undefined;
	    			} 
	    			return null;
    				
	    			
	    		}
	    		if (typeof(viewValue)=='string'){
	    			viewValue=viewValue.trim();
	    		}
	    		if ( prop.isBasic() //a single primitive value
	    				||(scope.propobj.getAllowedValues()==null && prop.isElementCollection() && !fxTemplates.useSingleFieldForMultiple(prop)) //element collection, but a field for each single item
	    				|| (scope.propobj.getAllowedValues()==null && prop.isRelation() && !prop.isCollection()) //relation, but only accepts one item.
	    		){
	    			return validateSingleValue(prop, viewValue, ctrl);
	    		}
	    		
	    		var vs=viewValue;
	    		if (typeof(viewValue)=='string'){
	    			vs=viewValue.split(/,\s*/);
	    		}
	    		
	    		var ret=new Array();
	    		for (var i=0; i<vs.length; i++){
	    			if (ctrl.$isEmpty(vs[i])){
	    				continue;
	    			}
	    			var v=validateSingleValue(prop, vs[i], ctrl);
	    			if (!angular.isDefined(v)){
	    				return undefined;
	    			} 
	    			ret.push(v);
	    		}
	    		if (ret.length==0 && prop.isRequired()){
	    			ctrl.$setValidity("required", false);
    				return undefined;
	    		}
	    		return ret;
			});
	    	ctrl.$formatters.push(function(modelValue){
	    		resetError(ctrl);
	    		var prop=scope.propobj;
	    		
	    		//check required
	    		if (prop.isRequired()){
	    			
	    			if (prop.isBasic() && isEmptyValue(modelValue)) {
	    				//basic a single element
	    				ctrl.$setValidity("required", false);
	    				return undefined;
	    				
	    			} else if (prop.isElementCollection() && !prop.getTypeObject().isEmbedded()){
	    				
	    				if (prop.getAllowedValues()!=null && isEmptyArray(modelValue)){
	    					//Select element.
		    				ctrl.$setValidity("required", false);
		    				return undefined;
	    				} else if (fxTemplates.useSingleFieldForMultiple(prop) && isEmptyArray(modelValue)){
	    					//basic collection
		    				ctrl.$setValidity("required", false);
		    				return undefined;
	    				} else {
	    					//if the element use multiple field such as short string,
	    					//the required validation is done at primitive multiple directive
	    				}
	    			} else if (prop.isRelation()){
	    				if (isEmptyValue(modelValue) || isEmptyArray(modelValue)){
	    					//basic collection
		    				ctrl.$setValidity("required", false);
		    				return undefined;
	    				}
	    			} else {
	    				//for embedded, we do not validate
	    				//for embedded collection, it is validated at embedded multiple directive
	    			}
    			} 
	    		
	    		//if not required, we have empty value, skip the rest
	    		if (isEmptyValue(modelValue) ){
	    			return modelValue;
	    		}
	    		
	    		if (isEmptyArray(modelValue)){
	    			return "";
	    		}
	    		//modelValue is single value
	    		if ( prop.isBasic() 
	    				||(!prop.getTypeObject().isEmbedded() && prop.isElementCollection() && !fxTemplates.useSingleFieldForMultiple(prop)) //element collection, but a field for each single item
	    				|| (prop.isRelation() && !prop.isCollection())){
	    			return validateSingleModelValue(prop, modelValue, ctrl);
	    		}
	    		if (prop.isElementCollection() && !prop.getTypeObject().isEmbedded() &&fxTemplates.useSingleFieldForMultiple(prop)){
	    			for (var i=0; i<modelValue.length; i++){
	    				var ret=validateSingleModelValue(prop, modelValue[i], ctrl);
	    				if (!angular.isDefined(ret)){
	    					return ret;
	    				}
	    			}
	    			return modelValue.join(",");
	    		}
	    		
	    		//collection of relation
	    		if (prop.isRelation()){
	    			for (var i=0; i<modelValue.length; i++){
	    				var ret=validateSingleModelValue(prop, modelValue[i], ctrl);
	    				if (!angular.isDefined(ret)){
	    					return ret;
	    				}
	    			}
	    			return modelValue.join(",");
	    		}
	    		
	    		//what is this case?
	    		throw "An unknown case for value validation from modelValue";
	    	});
	    	
	    },
		
	};
});
