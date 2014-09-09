
typeApp.directive("defaultValidator", function($compile){

	return {
		require:"ngModel",
		restrict: 'A',
	    link: function(scope, iElement, iAttrs, ctrl, transcludeFn) {
		    ctrl.$parsers.push(function(viewValue1) {
		    	scope.processExtraProps();
		   	 	var viewValue=viewValue1;
	   	    		scope.resetError(ctrl);
	   	    		var prop=scope.propObj;
	   	    		
	   	    		if (typeof(viewValue)=='string'){
	   	    			viewValue=viewValue.trim();
	   	    		}
	   	    		//embedded does not have default value
	   	    		//element collection is only for primitive
	   	    		if ( !prop.isCollection()){
	   	    			return scope.validateSingleValue(prop, viewValue, ctrl, true);
	   	    		}
	   	    		
	   	    		
	   	    		//when flows come to here, we have an collection of primitive or a collection of relation(integer).
	   	    		var vs=viewValue;
	   	    		if (typeof(viewValue)=='string'){
	   	    			vs=viewValue.split(/,\s*/);
	   	    		}
	   	    		
	   	    		var ret=new Array();
	   	    		for (var i=0; i<vs.length; i++){
	   	    			if (ctrl.$isEmpty(vs[i])){
	   	    				continue;
	   	    			}
	   	    			var v=scope.validateSingleValue(prop, vs[i], ctrl, true);
	   	    			if (!angular.isDefined(v)){
	   	    				return undefined;
	   	    			} 
	   	    			ret.push(v);
	   	    		}
	   	    		//'keep value as string:
	   	    		return ret.join(",");
	   			});

	    }
		
	};
});
