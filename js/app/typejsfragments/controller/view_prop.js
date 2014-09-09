

typeApp.controller("viewpropCtrl", 
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
	
	$scope.propaction="view";
	addOrEditProp($scope, Type, $stateParams, $modal, $state);
	$scope.init=function(){
		$scope.advanceattrs=false;
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
	};
	
	$scope.init();
	//*****************Communicate with server to check name instantly.
	
	$scope.cancelProp=function(){
		$state.go("viewtype", {typename:$scope.typename});
	};
	//-----------------------Simple utility function
	
	
});
