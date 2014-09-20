angular.module("flexdms.auth").controller("passwordCtrl", function($scope,fxAuthService, $state, $timeout, $http){
	$scope.userid=flexdms.fxuser.id;
	$scope.showErrorMsg=function(msg){
		$scope.errorMsg=msg;
		$timeout(function(){
			$scope.errorMsg="";
		}, 2500);
	};
	$scope.changePassword=function(){
		if ($scope.password!==$scope.password1){
			$scope.showErrorMsg("Two passwords do not match!");
			return;
		}
		$http.post(flexdms.authserviceurl+"/changepassword", {
			'LoginInfo': {
				'oldPassword': $scope.oldPassword,
				'password': $scope.password,
				
			}
		}).then(function(xhr){
				var rsMsg=xhr.data.appMsg;
				if (rsMsg.statuscode!=0){
					$scope.showErrorMsg(rsMsg.msg);
					return rsMsg;
				}
				$state.go("viewinst", {typename: 'FxUser',  id: $scope.userid});
				
		});
	};
})
.config(function($stateProvider, $urlRouterProvider){
	$stateProvider.state('changepassword', {
	    url: "/changepassword",
	    templateUrl: "template/login/password.html",
	    controller:"passwordCtrl"
	  });
	
	$urlRouterProvider.when("/account", function ($state, $http, Inst, instCache) {
		 $http.get(flexdms.authserviceurl+"/accountSettings").then(function(xhr){
				var inst=Inst.newInst("FxUserSettings", xhr.data.FxUserSettings);
				instCache.updateInst("FxUserSettings", inst);
				$state.go("viewinst", {'typename':'FxUserSettings', 'id': inst.id});
				return inst;
			});
	});
	
});
flexdms.config.addConfig("viewer", "FxUser", "actions", function(){
	var ret=angular.copy(flexdms.config.getConfig("viewer", 'default', 'actions'));
	ret.push("<a type='button' class='btn btn-default' title='password' href='#/changepassword'>Change Password</a>");
	return ret;
});
flexdms.config.addConfig("viewer", "FxUserSettings", "actions", function(){
	var ret=angular.copy(flexdms.config.getConfig("viewer", 'default', 'actions'));
	ret.push("<a type='button' class='btn btn-default' title='password' href='#/changepassword'>Change Password</a>");
	return ret;
});

