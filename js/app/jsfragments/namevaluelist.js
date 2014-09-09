angular.module("instDirective").directive("fxNameValueList", function($compile,$templateCache){
	return {
		priority:50, //make this before form
		require:"form",
		restrict: 'A',
		controller: function($scope, $element, $attrs){
			var propname=$scope.propobj.getName();
			//not initialized
			if (!$scope.inst[propname]){
				$scope.inst[propname]={entry:new Array()};
			}
			//give subform a name.
			$attrs.$set("ngForm", propname);
			

	    	$scope.addItem=function(){
	    		if (!$scope.inst[propname]){
					$scope.inst[propname]={entry:new Array()};
				}
	    		if (!angular.isDefined($scope.inst[propname].entry)){
	    			$scope.inst[propname]={entry:new Array()};
	    		}
	    		$scope.inst[propname].entry.push({key:"", value:""});
	    		$scope.field.$setDirty();
	    	};
	    	$scope.removeItem=function(index){
	    		$scope.inst[propname].entry.splice(index, 1);
	    		$scope.field.$setDirty();
	    	};
	    	
	    	/**
	    	 * transfer values to inst
	    	 * and check required constraint
	    	 */
	    	$scope.$watch("inst[propname].entry", function(){
	    		if ($scope.propobj.isRequired()&& inst[propname].entry==0){
	    			//this should be a sub form
	    			$scope.field.$setValidity('required', false);
	    		} else{
	    			$scope.field.$setValidity('required', true);
	    		}
	    	}, true);
	    	
		}, 
		link: function(scope, element, attrs, form){
	    	scope.field=form;
		}
	};
});


angular.module("instDirective").directive("fxNameValueInput", function($compile,$templateCache, fxTemplates){
	return {
		priority:10000, 
		require:"ngModel",
		restrict: 'A',
		controller :function($scope, $element, $attrs){
			var prop=$scope.propobj;
	    	var propname=prop.getName();
	    	var inputname=propname;
	    	inputname= propname+"_"+$scope.$index+"_"+$attrs.fxNameValueInput;
	    	
    		$attrs.$set("name", inputname);
	    	
	    	
		},
	    link: function(scope, iElement, iAttrs, form, transcludeFn) {
	    	//a field is required, PropEditDecorator expects it.
	    	var form=iElement.controller("form");
	    	
	    	if (form!=null ) {
	    		if (iAttrs.fxNameValueInput=='key'){
	    			scope.field=form[iAttrs.name];
	    		} else {
	    			scope.field1=form[iAttrs.name];
	    		}
	    		
	    	}
	    },
		
	};
});

