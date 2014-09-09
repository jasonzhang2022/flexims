

function addOrEditType($scope, Type){
	//this function is only called when value is valid
	$scope.checkName=function()
	{
		if (typeof($scope.type['@class'])=='undefined')
			return;
		
			//use static method to avoid the returned value override the scope type
			Type.checkName({typename: $scope.type['@class']}, function(data, headers){
				if (data.appMsg.statuscode!=0)
				{
					$scope.typeform.name.$valid=false;
					$scope.typeform.name.$invalid=true;
					$scope.typeform.name.$error.badname=data.appMsg.msg;
					
				}else {
					$scope.typeform.name.$valid=true;
					$scope.typeform.name.$invalid=false;
					$scope.typeform.name.$error.badname=false;
					$scope.type['@name']=$scope.type['@class'];
				}
			});
	};
	
	//when embedded is changed, adjust where type's type properly.
	$scope.$watch('embedded', function(newValue, oldValue) {
		if (angular.isDefined(oldValue) ||angular.isDefined(newValue)){
			if (newValue!=oldValue){
				$scope.realType.switchEntityEmbedded();
			}
		}
		if (newValue){
			$scope.extension=false;
		}
	});
	$scope.$watch("extension",function(newValue, oldValue) {
		if (newValue){
			$scope.type.attributes["basic"].push(flexdms.createExtensionProperty());
		} else {
			var index=-1;
			for(var i=0; i<$scope.type.attributes["basic"].length; i++){
				if ($scope.type.attributes["basic"][i]['@name']=='fxExtraProp'){
					index=i;
					break;
				}
			}
			if (index!=-1){
				$scope.type.attributes["basic"].splice(index, 1);
			}
			
		}
	});
	$scope.$watch("abstracttype",function(newValue, oldValue) {
		flexdms.addExtraProp($scope.type, "abstract", newValue);
	});
	
	$scope.addTypeProp=function()
	{
		var prop={'@name':"", '@value':"True"};
		$scope.type['property'].push(prop);
	};
	$scope.removeTypeProp=function(index)
	{
		$scope.type['property'].splice(index, 1);
	};

}



typeApp.controller("addtypeCtrl", function($scope, Type, $state, types){
	$scope.supers=[];
	$scope.extension=true;
	$scope.abstracttype=false;
	$scope.realType=new Type(flexdms.createEmptyType());
	$scope.types=types;
	angular.forEach(types, function(type){
		if (type.isEntity()) {
			$scope.supers.push(type.getName());
		}
	});
	
	

	//have a short cut here.
	$scope.type=$scope.realType.getTypeJson();
	$scope.embedded=!$scope.realType.isEntity();

	$scope.edit=false;
	addOrEditType($scope, Type);
	$scope.saveType=function()
	{
		$scope.realType.cleanCache();
		$scope.realType.$savetype(function(value, headers){
			flexdms.reloadTypes(Type);
			$state.go("types");
		});
	};
});

typeApp.controller("edittypeCtrl", function($scope, Type, $state, types, $stateParams){
	$scope.supers=[];
	$scope.realType=flexdms.findType($stateParams.typename);
	$scope.extension=$scope.realType.isExtensible();
	$scope.abstracttype=$scope.realType.isAbstract();
	$scope.types=types;
	angular.forEach(types, function(type){
		if (type.isEntity()) {
			$scope.supers.push(type.getName());
		}
	});
	
	

	//have a short cut here.
	$scope.type=$scope.realType.getTypeJson();
	$scope.embedded=!$scope.realType.isEntity();

	$scope.edit=true;
	addOrEditType($scope, Type);
	$scope.saveType=function()
	{
		$scope.realType.cleanCache();
		$scope.realType.$updatetype(function(value, headers){
			flexdms.reloadTypes(Type);
			$state.go("types");
		});
	};
});
