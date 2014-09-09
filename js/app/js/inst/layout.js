angular.module("fxlayout", []).service("fxlayout", function(){
	//nothing here.
}).controller("menuController", function($rootScope, $scope, fxlayout, securityInfoService){
	$scope.user=function(){
		return flexdms.fxuser;
	};
	$scope.logout=function(){
		securityInfoService.logout();
	};
	$scope.$watch(function(){
		return flexdms.fxuser;
	}, function(newvalue, oldvalue){
		if (angular.isDefined(newvalue) && !angular.isDefined($scope.isAdmin)){
			securityInfoService.isAdmin(flexdms.fxuser.Name).then(function(result){
				if (result){
					$scope.isAdmin=true;
				} else {
					$scope.isAdmin=false;
				}
			}, function(){
				$scope.isAdmin=false;
			});
		}
		if (angular.isDefined(newvalue) && !angular.isDefined($scope.createActions)){
			securityInfoService.getTypePermissions(flexdms.fxuser.Name, "Create").then(function(result){
				$scope.createActions=result;
			});
		}
	});
}).controller("adminCtrl", function($rootScope, $scope, $http, $window, $location){
	$scope.modifyResource=function(){
		if($scope.pageurl[0]=='/'){
			$window.location.href="type/index.html#/modifyresource"+$scope.pageurl;
		} else {
			$window.location.href="type/index.html#/modifyresource/"+$scope.pageurl;
		}
		
	};
	
});
flexdms.instAppModules.push("fxlayout");
