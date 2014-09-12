/**
 * @ngdoc directive
 * @name instDirective.directive:fxInitInstanceFromQueryParams
 * @restrict A
 * @priority -100
 * @description
 * 	retrieve value from query parameters in URL and set the value to inst in current scope.
 */
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
