/*
 * {
   "typeACL" : {
      "typename" : "FxUser",
      "aclParentTypes" : [ "doombuild", "parent2" ],
      "rolePermissions" : [ {
         "id" : 0,
         "order" : 0,
         "actions" : [ "Edit", "Watch" ],
         "decision" : "Allow",
         "roleid" : 1
      }, {
         "id" : 0,
         "order" : 0,
         "actions" : [ "Edit", "Watch" ],
         "decision" : "Deny",
         "roleid" : 2
      } ],
      "propPermissions" : [ {
         "id" : 0,
         "order" : 0,
         "actions" : [ "Edit", "Watch" ],
         "decision" : "Allow",
         "propName" : "IncludedBy"
      } ]
   }
}

 */

flexdms.securityserviceurl=appctx.modelerprefix+"/aclrs/acl";
angular.module("flexdms.acl", ['ui.router', 'flexdms.role']).service("securityService", function($http, Inst, instCache){
	
	this.getAllActions=function(){
		return $http.get(flexdms.securityserviceurl+"/allactions").then(function(xhr){
			return xhr.data;
		});
	};
	this.getTypeACL=function(type){
		return $http.get(flexdms.securityserviceurl+"/typeacl/"+type).then(function(xhr){
			return xhr.data;
		});
	};
	this.saveTypeACL=function(typeacl){
		return $http.post(flexdms.securityserviceurl+"/typeacl", typeacl, {fxAlertNoContent:false});
	};
	this.getInstACL=function(type, id){
		return $http.get(flexdms.securityserviceurl+"/instacl/"+type+"/"+id).then(function(xhr){
			return xhr.data;
		});
	};
	this.saveInstACL=function(type, id, aces){
		return $http.post(flexdms.securityserviceurl+"/instacl/"+type+"/"+id, aces, {fxAlertNoContent:false});
	};
	this.hasActionPermissions=function(username, type, id){
		function processResult(data){
			var ret={};
			angular.forEach(data.nameValueList.entry, function(entry){
				ret[entry.key]=entry.value==='true'?true:false;
			});
			return ret;
		}
		if (angular.isDefined(id)){
			return $http.get(flexdms.securityserviceurl+"/permissions/"+username+"/"+type+"/"+id).then(function(xhr){
				return processResult(xhr.data);
			});
		} else {
			return $http.get(flexdms.securityserviceurl+"/permissions/"+username+"/"+type).then(function(xhr){
				return processResult(xhr.data);
			});
		}
		
	};
	this.hasTypePermissions=function(username, action){
		function processResult(data){
			var ret={};
			angular.forEach(data.nameValueList.entry, function(entry){
				ret[entry.key]=entry.value==='true'?true:false;
			});
			return ret;
		}
		return $http.get(flexdms.securityserviceurl+"/typepermissions/"+username+"/"+action).then(function(xhr){
			return processResult(xhr.data);
		});
		
	};
}).controller("typeacl", function($scope, $stateParams, securityService, $state, roleService){
	$scope.typename=$stateParams.typename;
	$scope.type=flexdms.findType($scope.typename);
	
	securityService.getAllActions().then(function(data){
		$scope.actions=data.nameValueList.entry;
	});
	roleService.getAllRoles().then(function(data){
		$scope.roles=data;
	});
	securityService.getTypeACL($scope.typename).then(function(data){
		$scope.typeacl=data;
	});
	
	$scope.addRolePermission=function(){
		if (!angular.isDefined($scope.typeacl.typeACL.rolePermissions)){
			$scope.typeacl.typeACL.rolePermissions=[];
		}
		$scope.typeacl.typeACL.rolePermissions.push({"decision" : "Allow"});
	};
	$scope.addPropPermission=function(){
		if (!angular.isDefined($scope.typeacl.typeACL.propPermissions)){
			$scope.typeacl.typeACL.propPermissions=[];
		}
		$scope.typeacl.typeACL.propPermissions.push({"decision" : "Allow"});
	};
	$scope.deleteRolePermission=function($index){
		$scope.typeacl.typeACL.rolePermissions.splice($index, 1);
	};
	$scope.deletePropPermission=function($index){
		$scope.typeacl.typeACL.propPermissions.splice($index, 1);
	};
	
	$scope.relationProps=$scope.type.filterProps(function(){
		return this.isRelation();
	});
	
	$scope.roleProps=$scope.type.filterProps(function(){
		return this.getTypeObject().value==='FxRole' ||this.getTypeObject().value==='FxUser';
	});
	$scope.save=function(){
		if (!angular.isDefined($scope.typeacl.typeACL.rolePermissions)){
			for(var i=0; i<$scope.typeacl.typeACL.rolePermissions.length; i++){
				$scope.typeacl.typeACL.rolePermissions[i].order=i;
			}
		}
		if (!angular.isDefined($scope.typeacl.typeACL.propPermissions)){
			for(var i=0; i<$scope.typeacl.typeACL.propPermissions.length; i++){
				$scope.typeacl.typeACL.propPermissions[i].order=i;
			}
		}
		securityService.saveTypeACL($scope.typeacl).success(function(){
			$state.go("types");
		});
		
	}
	
	
}).config(function($stateProvider, $urlRouterProvider, $provide ){
	$stateProvider.state('typeacl', {
	      url: "/typeacl/:typename",
	      templateUrl: "template/acl/typeacl.html",
	      controller:"typeacl"
	    });
	$provide.decorator("securityInfoService", function($delegate, roleService, securityService){
		$delegate.getPermissions=function(username, type, id){
			return securityService.hasActionPermissions(username, type, id);
		};
		$delegate.getTypePermissions=function(username, action){
			return securityService.hasTypePermissions(username, action);
		};
		return $delegate;
	});
}).run(function(securityService){
	
});
flexdms.typemodules.push("flexdms.acl");
