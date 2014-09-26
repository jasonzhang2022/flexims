angular.module("instDirective").directive("fxInstActions", function($compile){
	return {
		restrict: 'A',
		link : function($scope, $element, $attrs, instCache){
			var actions=flexdms.config.getConfig($attrs.fxInstActions, $scope.typename, "actions");
			if (actions!==null) {
				actions=actions.call();
			} else  {
				actions=flexdms.config.getConfig($attrs.fxInstActions, 'default', "actions");
			}
			
			$element.append(actions.join(""));
			$element.find("button").addClass("navbar-btn");
			$element.find("a").addClass("navbar-btn");
			
			$compile($element.contents())($scope);
		}
	};
});
