
/**
 * set up name for ngFrom: Each multi-embedded property needs a subform.
 * 
 * For each instance, a new child form under this subform is needed.
 * 
 * addItem/removeItem: to add and remove new child embedded instance
 * check required enforcement on this embedded property.
 */
angular.module("instDirective").directive("fxEmbeddedMultiple", function(){
	return {
		priority:50, //make this before form
		require:"form",
		restrict: 'A',
		controller: function($scope, $element, $attrs, Inst){
			
			var propname=$scope.propobj.getName();
			//give subform a name.
			$attrs.$set("ngForm", propname);
			//initialize values.
			if (!angular.isDefined($scope.inst[propname]) ||$scope.inst[propname]==null){
				$scope.inst[propname]=new Array();
			} 
			

			angular.forEach($scope.inst[propname], function(inst){
				inst[flexdms.parentinst]=$scope.inst;
				inst[flexdms.insttype]=$scope.propobj.getTypeObject().value;
			});
			
	    	$scope.addItem=function(){
	    		var newinst=Inst.newInst($scope.propobj.getTypeObject().value);
	    		$scope.inst[propname].push(newinst);
	    		//get a hold of parent inst;
	    		//use $prefix so that it will not be serialized by angular hs
	    		newinst[flexdms.parentinst]=$scope.inst;
	    		$scope.field.fakelist.$dirty=true;
	    	};
	    	$scope.removeItem=function(index){
	    		$scope.inst[propname].splice(index, 1);
	    		$scope.field.fakelist.$dirty=true;
	    	};
	    	
	    	/**
	    	 * transfer values to inst
	    	 * and check required constraint
	    	 */
	    	if ($scope.propobj.isRequired()){
	    		$scope.$watchCollection("inst[propobj.getName()]", function(newvalue){
		    		if (newvalue.length==0){
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
		    	});
	    	}
	    	
	    	
		}, 
		link: function(scope, element, attrs, form){
	    	scope.field=form;
	    	var fakeControl={
		    		'$invalid':	false,
		    		'$name':'fakelist',
		    		'$valid':true,
		    		'$dirty': false,
		    		'$error': {'required':false}
		    	};
		    	form.$addControl(fakeControl);
		    if (!angular.isDefined(scope.$parent.field)){
		    	scope.$parent.field=form;
		    }
		}
	};
});
