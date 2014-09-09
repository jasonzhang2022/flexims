/**
 * Modify input to give correct 
 * ngModel: so ngModel bind works.
 * name: for the control can bound to form.
 * have a field(ngModelController) in scope so its validation state can be used
 */
angular.module("instDirective").directive("fxPropInput",function($compile,$templateCache, fxTemplates){
	return {
		priority:10000, 
		require:"?ngModel",
		restrict: 'A',
		controller :function($scope, $element, $attrs){
			var prop=$scope.propobj;
		    
	    	var propname=prop.getName();
	    	var inputname=propname;
	    	
	    	//for DIV, we do not how to handle it
	    	if ($element[0].tagName.toUpperCase()=='SELECT' || $element[0].tagName.toUpperCase()=="DIV"){
	    		$attrs.$set("ngModel", "inst['"+propname+"']");
	    		$attrs.$set("name", inputname);
	    		if (prop.isElementCollection()){
	    			$attrs.$set("multiple", true);
	    		}
	    	} else if (prop.isElementCollection()  && !fxTemplates.useSingleFieldForMultiple(prop)){
	    		inputname= propname+$scope.$index;
	    		$attrs.$set("name", inputname);
	    		$attrs.$set("ngModel", "singlevalue.value");
	    	} else {
	    		$attrs.$set("ngModel", "inst['"+propname+"']");
	    		$attrs.$set("name", inputname);
	    	}
	    	$scope.inputname=inputname;
	    	
	    	  if($element[0].tagName.toUpperCase()=='INPUT'){
		    	if (!$scope.showlabel){
		    		$attrs.$set("placeholder", prop.getDisplayText());
		    	}
		    	if (prop.getTypeObject().isDateOnly() || prop.getTypeObject().isTimestamp()){
		    		if (prop.getTypeObject().isDateOnly()){
		    			$attrs.$set("datepickerPopup", flexdms.dateFormat);
		    		} else{
		    			$attrs.$set("datepickerPopup", flexdms.dateTimeFormat);
		    		}
		    		$scope.showdatepicker=false;
		    		$scope.openDatePicker=function($event){
		    			 $event.preventDefault();
		    			 $event.stopPropagation();
		    			 $scope.showdatepicker = true;
		    		};
		    	}
	    	}
	    	  $scope.showInvalid=function(field){
					 if (angular.isDefined(field)){
						 return field.$dirty && field.$invalid;	 
					 }
					 return false;
				};
		},
	    link: function(scope, iElement, iAttrs,transcludeFn) {
	    	//expose form field so that validation state can be showned
	    	if (!angular.isDefined(scope.$parent.field)){
	    		//parent could have field.
	    		//for primitive with multiple value, the field in parent is the subform
	    		scope.$parent.field=iElement.controller("ngModel");
	    	}
	    	
    		scope.field=iElement.controller("ngModel");
	    },
		
	};
});
