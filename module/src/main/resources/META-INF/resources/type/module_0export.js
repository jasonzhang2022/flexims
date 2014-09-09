angular.module("flexdms.module",  ['ui.router', 'flexdms.TypeResource'])
.controller("moduleExporter", function($scope, Type, $state, $stateParams, $http, $window){
	
	
	//default value.
	$scope.type=$stateParams.type;
	$scope.report="all";
	$scope.withdata=true;
	$scope.startid=1000;
	$scope.queryStartid=1000;
	$scope.reportStartid=1000;
	$scope.module=$stateParams.type;
	
	var t=flexdms.findType($stateParams.type);
	if (t.getExtraProp("module")){
		$scope.module=t.getExtraProp("module");
	}
	
	
	$scope.download=function(){
		var cluster=flexdms.findType($stateParams.type).cluster();
		var types=[];
		for (var typename in  cluster){
			types.push(typename);
		}
		
		$http.post(appctx.modelerprefix+"/modulers/module/download", {'moduleDefinition' : {
			"types": types.join(","),
			module: $scope.module,
			withdata: $scope.withdata,
			reporttype: $scope.report,
			startid: $scope.startid,
			queryStartid: $scope.queryStartid,
			reportStartid: $scope.reportStartid
			
		}}, {responseType: 'blob'}).success(function(data, status, header, config, statusText){
			//var blob = new Blob(data, {type: "application/zip"});
			saveAs(data, $scope.module+".jar");
		});
	};
	
}).config(function($stateProvider, $urlRouterProvider) {
	  //
	  // Now set up the states
	  $stateProvider.state('moduledownload', {
		      url: "/moduledownload/:type",
		      templateUrl: "type/export_module.html",
		      controller:"moduleExporter"
		})
		;
	    
	    
});
flexdms.typemodules.push("flexdms.module");
