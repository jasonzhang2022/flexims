/*
Prefix input name with form name so its name is unique in current page
*/
angular.module("instDirective").directive("fxPropInputFormUniqeName", function($compile,$templateCache, fxTemplates){
	return {
		require:"^form",
		priority:999, 
		restrict: 'A',
		controller :function($scope, $element, $attrs){
			var prop=$scope.propobj;
		    
			var formname=$element.controller("form").$name;
	    	var propname=prop.getName();
	    	var inputname=propname;
	    	$attrs.$set("name", formname+"_"+inputname);
		}
	};
});
