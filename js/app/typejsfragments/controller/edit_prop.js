

typeApp.controller("editpropCtrl", 
                                   function($scope, Type, $stateParams, $modal, $state){
	
	function initExtraProps(propobj){
	
		return {
			notnullable:false,
			fileupload:propobj.isFileUpload(),
			objectclass:propobj.getRootClass(),
			viewable:propobj.isViewable(),
			editable:propobj.isEditable(),
			minlen:propobj.getMinLen(),
			maxlen:propobj.getMaxLen(),
			minvalue:propobj.getMinValue(),
			maxvalue:propobj.getMaxValue(),
			autogenerate:propobj.isAutoGenerate(),
			summaryprop:propobj.isSummaryProp(),
			defaultvalue:propobj.getDefaultValue(),
			pattern:propobj.getPattern(),
			ignorepatterncase:propobj.isIgnoreCaseForPattern(),
			allowedvalues:propobj.getAllowedValues(),
			tooltip:propobj.getTooltip(),
			display:propobj.getDisplayText()
			
		};
	}
	
	$scope.propaction="edit";
	addOrEditProp($scope, Type, $stateParams, $modal, $state);
	$scope.init=function(){
		$scope.advanceattrs=true;
		$scope.relationattrs=false;
		$scope.basicattrs=false;
		
		//type constants
		$scope.proptypes=$scope.establishTypes();
		
		//object is ready for edit
		$scope.propObj=$scope.typetop.getProp($stateParams.propname);
		$scope.propObj.getTypeObject();
		$scope.resetPropType();
		$scope.prop=$scope.propObj.prop;
		$scope.extraprops=initExtraProps($scope.propObj);
		if ($scope.extraprops.allowedvalues==null){
			$scope.extraprops.allowedvalues=[];
		}
		$scope.initOrder();
		//still need to display this although edit is not allowed
		$scope.potentialMappedbys=$scope.findMappedBys();
	};
	
	$scope.init();
	//*****************Communicate with server to check name instantly.
	
	
	//synchronize multiple variable corresponding to private
	$scope.$watch("prop['private-owned']", function(value){
		if (!$scope.propObj.getTypeObject().isRelation()){
			return;
		}
		//synchronize value for private management.
		if (angular.isDefined(value)){
			//do not use this, foreign key is not established correctly in postgrseql.
			//We rely on cascade at persistence lay instead of database
			//$scope.prop['cascade-on-delete']=value;
			$scope.prop['@orphan-removal']=value;
		}
		if (value){
			$scope.prop["cascade"]={
					'cascade-remove':true
			};
		} else {
			delete($scope.prop["cascade"]);
		}
	});
	
	
	//have this a separate function so it can be tested.
	$scope.presave=function(){
		$scope.processExtraProps();
		
		$scope.typetop.cleanCache();
		
	};
	$scope.saveProp=function(){
		
		
		$scope.presave();
		if (!$scope.validateDefaultAndAllowed()){
			return;
		}
		$scope.typetop.$saveprop(null, function(value){
			flexdms.reloadTypes(Type);
			$state.go("viewtype", {typename:value.getName()});
		});
	};
	
	$scope.cancelProp=function(){
		$state.go("viewtype", {typename:$scope.typename});
	};
	//-----------------------Simple utility function
	
	
});
