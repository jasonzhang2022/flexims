//----------javascript to edit the relation editor specification
flexdms.query.relationui={
		'uitype': ['Dropdown/ListBox', 'Inline Table', 'Popup Table']
};
angular.module("flexdms.report").controller("fxRelationuiSpecCtrl",  function($scope, $stateParams, instCache, reportService){
	$scope.type=flexdms.findType($stateParams.typename);
	$scope.propobj=$scope.type.getProp($stateParams.propname);
	
	$scope.relationui=flexdms.getExtraPropObj($scope.propobj.prop, 'relationui');
	if ($scope.relationui===null){
		$scope.relationui={
				'report':null,
				'uitype':flexdms.query.relationui.uitype[0],
				'params':[],
				'showselected': true,
			};
	}  else{
		$scope.relationui.showselected=flexdms.parseTrueFalse($scope.relationui.showselected);
		if (!angular.isDefined($scope.relationui.params) ||$scope.relationui.params===null){
			$scope.relationui.params=[];
		}
		if (!angular.isDefined($scope.relationui.uitype)){
			$scope.relationui.uitype=flexdms.query.relationui.uitype[0];
		}
		if ($scope.relationui.report){
			//model expects a number
			$scope.relationui.report=$scope.relationui.report-0;
		}
	}
	
	
	$scope.cancelurl=appctx.modelerprefix+"/type/index.html#/viewtype/"+$scope.type.getName();
	$scope.save=function(){
		$scope.relationui.params.length=0;
		angular.forEach($scope.params, function(param){
			if (param.prop){
				$scope.relationui.params.push(param.prop);
			} else {
				$scope.relationui.params.push(null);
			}
		});
		flexdms.setExtraPropObj(this.propobj.prop,"relationui", $scope.relationui);
		$scope.type.$minorsaveprop(null, function(value){
			window.location.replace($scope.cancelurl);
		});
	}
	
	$scope.reports=new Array();
	$scope.uitypes=flexdms.query.relationui.uitype;
	

	
	//temporary params for the current uireport.
	$scope.params=null;
	$scope.calculatePossibleReports=function(){
		var proptype=$scope.propobj.proptype;
		if (!proptype.isRelation()){
			return;
		}
		// call all service to make sure that all report is created
		reportService.getAllReport(proptype.value).then(function(allreport){
			reportService.getReportsByType(proptype.value).then(function(reports){
				$scope.reports=reports;
				$scope.prepareParams();
			});
		});
	};
	
	
	$scope.calculatePossibleReports();
	
	$scope.prepareParams=function(){
		if ($scope.params!=null){
			$scope.params.length=0;
		}
		if (!$scope.relationui.report){
			return;
		}
		//report is prepared by getReportsByType so the report is ready and in cache.
		$scope.report=instCache.getInst("FxReport", $scope.relationui.report);
		$scope.params=fxReportInitParamValues($scope.report, $scope.report['$queryobj'], instCache);
		for(var i=0; i<$scope.params.length; i++){
			if (angular.isDefined($scope.relationui.params[i])){
				$scope.params[i].prop=$scope.relationui.params[i];
			}
		}
	};
	
	$scope.needInput=function(param){
		return param.needInput();
	};

	$scope.isRequired=function(param){
		if (!angular.isDefined(param.value) || param.value===null){
			return true;
		}
		if (angular.isArray(param.value)){
			if (param.value.length==0){
				return true;
			}
		} 
		return false;
	}
	
	$scope.getInputProps=function(param){
		var prop=param.condition.getProp();
		var proptype=prop.getTypeObject();
		var op=new flexdms.query.Operator(param.condition.inst.Operator);
		var numOfOperands=op.howManyOperands();
		if (numOfOperands==0){
			return new Array();
		}
		if (numOfOperands==1 && op.isSize()){
			return $scope.type.filterProps(function(){
				return this.getTypeObject().isInteger();
			});
		}
		if (proptype.isInteger()){
			return $scope.type.filterProps(function(){
				if (this.isIdOrVersion()){
					return false;
				}
				return this.getTypeObject().isInteger();
			});
		}
		
		if (proptype.isFloat()){
			return $scope.type.filterProps(function(){
				if (this.isIdOrVersion()){
					return false;
				}
				return this.getTypeObject().isNumber();
			});
		}
		
		if (proptype.isDateOnly()){
			return $scope.type.filterProps(function(){
				if (this.isIdOrVersion()){
					return false;
				}
				return this.getTypeObject().isDateOnly();
			});
		}
		if (proptype.isTimestamp()){
			return $scope.type.filterProps(function(){
				if (this.isIdOrVersion()){
					return false;
				}
				return this.getTypeObject().isDateOnly() ||  this.getTypeObject().isTimestamp();
			});
		}
		if (proptype.isTimeOnly()){
			return $scope.type.filterProps(function(){
				if (this.isIdOrVersion()){
					return false;
				}
				this.getTypeObject().isTimeOnly();
			}); 
		}
		//we can not select embedded
		if (proptype.isEmbedded()){
			return new Array();
		}
		
		//the same type: 
		if (proptype.isRelation() ){
			return $scope.type.filterProps(function(){
				return this.getTypeObject().value==proptype.value;
			}); 
		}
		
		return $scope.type.filterProps(function(){
			if (this.isIdOrVersion()){
				return false;
			}
			return this.getTypeObject().isStringType();
		});
	};
	
	$scope.$watch("relationui.report", function(newvalue, oldvalue){
		if (newvalue && newvalue!==oldvalue){
			$scope.prepareParams(newvalue, oldvalue);
		} else if (!newvalue && $scope.params){
			$scope.params.length=0;
		}
		
	});
});
angular.module("flexdms.report").config(function($stateProvider){
	$stateProvider.state('relationui', {
		url : "/relationui/:typename/:propname",
		templateUrl:'template/report/prop_editor_ui.html',
		controller: "fxRelationuiSpecCtrl"
	});
});
