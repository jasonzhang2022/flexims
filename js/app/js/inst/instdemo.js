angular.module("instDemo", ["instDirective"]).config(function($stateProvider, $urlRouterProvider){
	$stateProvider.state('addinstbatch', {
		url : "/addinstbatch/:typename",
		templateUrl :"demo/batchadder.html",
		controller: function($scope, $stateParams, $http, Inst){
			$scope.typename=$stateParams.typename;
			$scope.insts=[];
			for (var i=0; i<10; i++){
				var inst=Inst.newInst($scope.typename);
				$scope.insts[i]=inst;
			}
			$scope.add=function(){
				var inst=Inst.newInst($scope.typename);
				$scope.insts.push(inst);
			};
			$scope.save=function(){
				var entities={};
				entities[$scope.typename]=$scope.insts;
				$http.post(flexdms.instserviceurl+"/savebatch", {'entities': entities})
				.then( function(ret){
					$scope.saved=true;
				}, function(error){
					$scope.saveerror=fxAlert.getErrorObject().msg;
				});
			};
			
			$scope.deleteInst=function($index){
				$scope.insts.splice($index, 1);
			};
			$scope.props=flexdms.findType($scope.typename).getEditProps();
			$scope.viewprops=flexdms.findType($scope.typename).getViewProps();
			$scope.showlabel=false;
		}
	});
	
});


