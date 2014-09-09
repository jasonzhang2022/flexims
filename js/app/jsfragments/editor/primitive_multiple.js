
/*
 * 1. work with ngForm . Give sub form a name so sub form can be added to parent form as a field.
 * 2. If property has a collection of value, and the multiple inputs are needed for a property, 
 * enforce the required restriction for the property
 */
angular.module("instDirective").directive("fxSimpleMultiple",function($compile,$templateCache){
	return {
		priority:50, //make this before form
		require:"form",
		restrict: 'A',
		controller: function($scope, $element, $attrs){
			
			var propname=$scope.propobj.getName();
			//give subform a name.
			$attrs.$set("ngForm", propname);
			//initialize values.
			$scope.values=[];
			if (angular.isDefined($scope.inst[propname]) && !$scope.inst[propname]!==null){
				angular.forEach($scope.inst[propname], function(v){
	    			$scope.values.push({'value':v});
	    		});
			}

	    	$scope.addItem=function(){
	    		$scope.values.push({'value':''});
	    		$scope.field.fakelist.$dirty=true;
	    	};
	    	$scope.removeItem=function(index){
	    		$scope.values.splice(index, 1);
	    		$scope.field.fakelist.$dirty=true;
	    	};
	    	
	    	/**
	    	 * transfer values to inst
	    	 * and check required constraint
	    	 */
	    	
	    		
	    	
	    	$scope.$watch("values", function(){
	    		//$scope.inst[propname]
	    		
	    		//trasnfer value from multiple to inst
	    		var newvs=new Array();
	    		angular.forEach($scope.values, function(v){
	    			if (v.value ) {
	    				newvs.push(v.value);
	    			}
	    		});
	    		$scope.inst[propname]=newvs;
	    		//checked required
	    		if($scope.propobj.isRequired()) {
	    			if (newvs.length==0){
	    				$scope.field.fakelist.$invalid=true;
	    				$scope.field.fakelist.$valid=false;
	    				$scope.field.fakelist.$error.required=true;
	    				$scope.field.$setValidity('required', false, $scope.field.fakelist);
	    			} else{
	    				$scope.field.fakelist.$invalid=false;
	    				$scope.field.fakelist.$valid=true;
	    				$scope.field.fakelist.$error.required=false;
	    				$scope.field.$setValidity('required', true, $scope.field.fakelist);
	    			}
	    		}
	    	}, true);
	    	
	    	
		}, 
		link: function(scope, element, attrs, form){
			//subform is treated a field. This field is required by PropEditValidator.
	    	scope.field=form;
	    	//add a fake control
	    	var fakeControl={
	    		'$invalid':	false,
	    		'$name':'fakelist',
	    		'$valid':true,
	    		'$dirty': false,
	    		'$error': {'required':false}
	    	};
	    	form.$addControl(fakeControl);
	    	scope.$parent.field=form;
		}
	};
});
