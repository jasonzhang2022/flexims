
typeApp.directive("allowedValidator", function($compile){

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
	    		return scope.validateSingleValue(prop, viewValue, ctrl, false);
	    	});   
	    	scope.field=ctrl;
	    }
		
	};
});
