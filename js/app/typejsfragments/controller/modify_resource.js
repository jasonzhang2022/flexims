typeApp.controller("modifyRecourceCtrl", function($scope, $stateParams, $http, $state){
	  
	  $http.get(appctx.modelerprefix+"/rs/res/web/"+$stateParams.url).success(function(data){
		  if (data){
			  $scope.content=data;
		  } else {
			  $scope.content="";
		  }
	  });
	  
	  //http://stackoverflow.com/questions/11442632/how-can-i-make-angular-js-post-data-as-form-data-instead-of-a-request-payload
	  $scope.save=function(){
		  $http({
			    method: 'POST',
			    url: appctx.modelerprefix+"/rs/res/web/upload",
			    data: $.param({"templatename": $stateParams.url, "content": $scope.content}),
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			    fxAlertNoContent:false
			}).then(function(){
				$state.go("types");
			});
	  };
	  $scope.remove=function(){
		  $http({
			    method: 'DELETE',
			    url: appctx.modelerprefix+"/rs/res/web/delete/"+$stateParams.url,
			    fxAlertNoContent:false
			}).then(function(){
				$state.go("types");
			});
	  };
});
