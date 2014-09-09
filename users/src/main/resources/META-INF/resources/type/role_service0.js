flexdms.roleserviceurl=appctx.modelerprefix+"/rolers/role";
angular.module("flexdms.role", ['ui.router']).service("roleService", function($http){
	
	
	this.getAllRoles=function(){
		return $http.get(flexdms.roleserviceurl+"/allroles").then(function(xhr){
			var ret=[];
			if (angular.isDefined(xhr.data.entities.FxRole)){
				angular.forEach(xhr.data.entities.FxRole, function(r){
					ret.push(r);
				})
			}
			if (angular.isDefined(xhr.data.entities.FxUser)){
				angular.forEach(xhr.data.entities.FxUser, function(r){
					ret.push(r);
				})
			}
			return ret;
			
		});
	};
	this.getAllUsers=function(){
		return $http.get(flexdms.roleserviceurl+"/allusers").then(function(xhr){	
			var ret=[];
			if (angular.isDefined(xhr.data.entities.FxUser)){
				angular.forEach(xhr.data.entities.FxUser, function(r){
					ret.push(r);
				})
			}
			return ret;
		});
	};
	this.isInRole=function(username, role){
		return $http.get(flexdms.roleserviceurl+"/inrole/"+username+"/"+role, {fxSkipError: true}).then(function(xhr){	
			if (xhr.data.appMsg.statuscode==0){
				return true;
			}
			return false;
		});
	};
	
	
	
	
}).config(function($provide){
	$provide.decorator("securityInfoService", function($delegate, roleService){
		$delegate.isAdmin=function(username){
			return roleService.isInRole(username, "Administrator");
		};
		return $delegate;
	});
});
flexdms.typemodules.push("flexdms.role");
