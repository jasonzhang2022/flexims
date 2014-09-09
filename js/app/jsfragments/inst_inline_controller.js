
/**
 * Used to display relation to turn a instance id into an instance
 */
angular.module("instDirective").controller("inlineInstController",function($scope, instCache, Inst){
	$scope.$watch("propvalue", function(propvalue){
		if (!propvalue){
			return;
		}
		if (angular.isObject($scope.propvalue)){
			$scope.propinst=Inst.newInst($scope.propobj.getTypeObject().value, $scope.propvalue);
		} else {
			$scope.propinst=instCache.getInst($scope.propobj.getTypeObject().value, $scope.propvalue);
		}
	});
});
