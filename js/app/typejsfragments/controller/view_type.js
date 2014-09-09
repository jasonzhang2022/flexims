
typeApp.controller("viewtypeCtrl",  function($scope, Type, $stateParams){
	if (!angular.isDefined($scope.typename)){
		$scope.typename=$stateParams.typename;
	} 
	$scope.types=flexdms.types;
	$scope.typetop=flexdms.findType($scope.typename);
	$scope.typetop.cleanCache(); //make sure we do not stale cache
	$scope.type=$scope.typetop.getTypeJson();
	$scope.editable=!$scope.typetop.isExtraProp('fixed', false);
	
	function prepareProps(){
	
		var props=[];
		angular.forEach($scope.typetop.getSelfProps(), function(prop){
			if (!prop.isIdOrVersion()){
				props.push(prop);
			}
		});
		$scope.props=props;
	}
	
	prepareProps();
	$scope.deleteProp=function(propname){
		Type.deleteprop({typename:$scope.typename, 'propname':propname}, function(data, headers){
			Type.assignnewmeta(data);
			$scope.typetop=flexdms.findType($scope.typename);
			$scope.type=$scope.typetop.getTypeJson();
			prepareProps();
		});
	};
	
	$scope.showRelationUi=function(prop){
		return prop.isRelation() && prop.isRelationOwner()?true: false;
	};
	
	$scope.relationeditorurl=function(prop){
		return appctx.formprefix+"/index.html#/relationui/"+$scope.typename+"/"+prop.getName();
	};
	
});
