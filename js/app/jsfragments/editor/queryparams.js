angular.module("instDirective").directive("fxInitInstanceFromQueryParams",function($compile){
	
	return {
		restrict: 'A',
		priority: -100,
		controller : function($scope, $element, $attrs, $location, Inst){
			var stringInst=flexdms.unflatObject($location.search());
			$scope.inst.$promise.then(function(){
				flexdms.copyFromStringValued($scope.inst, stringInst, Inst);
			});
			
		}
	};
});
