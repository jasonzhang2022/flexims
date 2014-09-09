
angular.module("flexdms.instacl", ['ui.router']).controller("instacl", function($scope, $stateParams, securityService, $state){
	/*
	 * {
   "instanceACES" : {
      "aces" : [ {
         "id" : 0,
         "typename" : "test",
         "instanceid" : 52,
         "order" : 0,
         "rolePermission" : {
            "roleid" : 1,
            "actions" : [ "Edit", "Watch" ],
            "decision" : "Allow"
         }
      }, {
         "id" : 0,
         "typename" : "test",
         "instanceid" : 52,
         "order" : 0,
         "rolePermission" : {
            "roleid" : 2,
            "actions" : [ "Edit", "Watch" ],
            "decision" : "Deny"
         }
      } ]
   }
}

	 */
	$scope.typename=$stateParams.typename;
	$scope.id=$stateParams.id;
	$scope.type=flexdms.findType($scope.typename);
	
	securityService.getAllActions().then(function(data){
		$scope.actions=data.nameValueList.entry;
	});
	securityService.getAllRoles().then(function(data){
		$scope.roles=data;
	});
	securityService.getInstACL($scope.typename, $scope.id).then(function(data){
		$scope.instacl=data;
	});
	
	$scope.addRolePermission=function(){
		if (!angular.isDefined($scope.instacl.instanceACES.aces)){
			$scope.instacl.instanceACES.aces=[];
		}
		$scope.instacl.instanceACES.aces.push({
				'typename': $scope.typename,
				'instanceid': $scope.id,
				'rolePermission': {
					"actions":[],
					"decision" : "Allow"
				}
			});
	};
	$scope.deleteRolePermission=function($index){
		$scope.instacl.instanceACES.aces.splice($index, 1);
	};

	$scope.saveAcl=function(){
		if (!angular.isDefined($scope.instacl.instanceACES.aces)){
			for(var i=0; i<$scope.instacl.instanceACES.aces.length; i++){
				$scope.instacl.instanceACES.aces[i].order=i;
			}
		}
		securityService.saveInstACL($scope.typename, $scope.id, $scope.instacl).success(function(){
			$state.go("viewinst", {typename: $scope.typename, id: $scope.id});
		});
	}
}).config(function($stateProvider, $urlRouterProvider){
	$stateProvider.state('instacl', {
		      url: "/instacl/:typename/:id",
		      templateUrl: "template/acl/instacl.html",
		      controller:"instacl"
		    });
});
flexdms.instAppModules.push("flexdms.instacl");
flexdms.config.viewer['default'].actions.push( "<a type='button' class='btn btn-default' title='permission' ng-show='actions.Grant'" +
		" href='#/instacl/{{typename}}/{{instid}}'> <span class='glyphicon glyphicon-lock'></span></a>");

