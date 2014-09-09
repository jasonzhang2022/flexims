

/**
 * abstract parent control. 
 * Type information is available for type-related activity such as add property add relation.
 */
typeApp.controller("typeCtrl", function($scope, Type, $stateParams){
    $scope.typename=$stateParams.typename;
    $scope.types=flexdms.types;
    $scope.typetop=flexdms.findType($stateParams.typename);
    $scope.typetop.cleanCache(); //make sure we do not stale cache
    $scope.type=$scope.typetop.getTypeJson();
  
});
