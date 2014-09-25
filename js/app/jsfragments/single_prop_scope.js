/**
 * @ngdoc directive
 * @name instDirective.directive:fxSingleProp
 * @restrict A
 * @priority 599
 * @description
 * 
 * Create a new scope for a single property
 * 
 * @scope
 * 
 * @param {String} fx-single-prop a property name.
 *  
 */
angular.module("instDirective").directive("fxSingleProp", function($compile,$templateCache, fxTemplates){
	return {
		priority:599, //make this after if 
		restrict: 'A',
		scope:true, //use new scope to avoid polluate parent scope.
		controller :function($scope, $element, $attrs){
			var propname=$attrs.fxSingleProp;
			$scope.propobj=$scope.type.getProp(propname);
		},
	};
}).directive("fxSimpleSingleProp", function($compile,$templateCache, fxTemplates){
	return {
		priority:599, //make this after if 
		restrict: 'A',
		scope:false,
		controller :function($scope, $element, $attrs){
			var propname=$attrs.fxSingleProp;
			$scope.propobj=$scope.type.getProp(propname);
		},
	};
});