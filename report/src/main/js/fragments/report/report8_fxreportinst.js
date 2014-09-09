/*
 * {
         "type" : "fxReport",
         "ParamValues" : {
            "paramValues" : [ {
               "index" : 0,
               "values" : [ "3" ]
            }, {
               "index" : 1,
               "values" : [ "test1", "test2" ]
            }, {
               "index" : 2,
               "values" : [ "2014-04-24T10:05:36.014-07:00", "2014-04-24T10:05:36.163-07:00" ]
            } ]
         },
         "Query" : 10000,
         "OrderBy" : {
            "entry" : [ {
               "key" : "propint",
               "value" : "ASC"
            } ]
         },
         "Properties" : {
            "entry" : [ {
               "key" : "0",
               "value" : "shortstring"
            }, {
               "key" : "1",
               "value" : "propint"
            }, {
               "key" : "2",
               "value" : "propdate"
            } ]
         }
      }
 */

/**
 * inst: fxReport Instance
 * queryobj:flexdms.query.defaulttypedquery object. All TraceDown is already called.
 */
function fxReportInitParamValues(inst, queryobj, instCache){
	//avoid stale values.
	queryobj.params=null;
	
	var params=queryobj.getParams(instCache);
	if (angular.isDefined(inst.ParamValues) && 
			inst.ParamValues!=null &&
			angular.isDefined(inst.ParamValues.paramValues) && 
			inst.ParamValues.paramValues!=null){
		
		if (inst.ParamValues.paramValues.length==params.length){
			//may not necessary to sort.
			var sortedparams=inst.ParamValues.paramValues.sort(function(a, b){
				return a.index-b.index;
			});
			
			for(var i=0; i<sortedparams.length; i++){
				if (params[i].getOp().howManyOperands()==1 ||params[i].getOp().howManyOperands()==2 ){
					if (angular.isDefined(sortedparams[i].values) && sortedparams[i].values!=null && sortedparams[i].values.length>0){
						params[i].value=sortedparams[i].values[0];
					}
				} else if (params[i].getOp().howManyOperands()>2) {
					if (angular.isDefined(sortedparams[i].values) && sortedparams[i].values!=null && sortedparams[i].values.length>0){
						params[i].value=sortedparams[i].values;
					} else {
						//do not do this, we could override the value from query
						//params[i].value=[];
					}
				}
				params[i].computeValue();
			}
		}
	} else {
		inst.ParamValues={'paramValues':[]};
	}
	return params;

}

angular.module("flexdms.report").controller("fxReportEditorCtrl", function($scope, Inst, instCache, Util, reportService, $state, $modal){
	
	//properties that can be selected for display.
	$scope.reportprops=[];
	
	//can be used for order.
	$scope.orderprops=[];
	
	//prop to display.
	$scope.reportDidsplayProps=[];
	
	
	//whether we already initialized properly.
	$scope.inited=false;
	
	
	function processParamsBeforeSave(){
		var params=[];
		for (var i=0; i<$scope.params.length; i++){
			params.push($scope.params[i].getValueObject(i));
		}
		$scope.inst.ParamValues.paramValues=params;
	}
	
	$scope.instpscope.isExecutable=function(){
		if (!$scope.inited){
			return false;
		}
		normalizeReport();
		for (var i=0; i<$scope.params.length; i++){
			if (!$scope.params[i].isReady()){
				return false;
			}
		}
		//has to select one property to show
		if ($scope.inst.Properties.entry.length==0){
			return false;
		}
		return true;
	};
	
	function normalizeReport(){
		//transform Properties
		var props=[];
		for(var i=0; i<$scope.reportDidsplayProps.length; i++){
			props.push({'key':i, 'value':$scope.reportDidsplayProps[i]});
		}
		$scope.inst.Properties.entry=props;
		
		//transform order field:NO need, we have direct bind.
		
		//transform query param
		processParamsBeforeSave();
	}
	$scope.saveReport=function($event){
		
		$event.preventDefault();
		$event.stopPropagation();
		normalizeReport();
		 $scope.inst.$save(null, function(inst1){
			 $state.go("viewinst", {typename:$scope.typename, id:inst1.id});
			 
		  });
	}
	
	$scope.instpscope.preview=function(){
		normalizeReport();
		var reportInst=$scope.inst;
		
		var modalInstance = $modal.open({
			templateUrl:'template/report/report_preview.html',
			controller: function($scope, $modalInstance){
				$scope.report=reportInst;
				$scope.ok=function(){
					 $modalInstance.close();
				}
			},
			size: 'lg'
		});
	};
	

	$scope.$watch("inst.Query", function(){
		if (!$scope.inst.Query){
			return;
		}
		var queryid=$scope.inst.Query;
		if (angular.isObject(queryid)){
			queryid=$scope.inst.Query.id;
		}
		//get all query used by this report recursively.
		reportService.getAllTraceDown(queryid).then(function(data){
			function hasEntry(prop){
				return angular.isDefined($scope.inst[prop]) && $scope.inst[prop]!=null && angular.isDefined($scope.inst[prop].entry) && $scope.inst[prop].entry!=null  ;
			}
			//!!!!!!!!!!!!!!!!!!!!These are the four major objects available at top level.
			$scope.queryobj=new flexdms.query.defaulttypedquery(instCache.getInst("DefaultTypedQuery", queryid));
			
			$scope.reportprops=$scope.queryobj.getDisplayableProps();
			$scope.orderprops=$scope.queryobj.getOrderProps();
			
			if (hasEntry("Properties")){
				//transform properties to a format that can be editted.
				var sortedprops=$scope.inst.Properties.entry.sort(function(a, b){
					return parseInt(a.key)-parseInt(b.key);
				});
				angular.forEach(sortedprops, function(entry){
					$scope.reportDidsplayProps.push(entry.value);
				});
			} else{
				$scope.inst.Properties={'entry':[]};
			}
			
			//order field transform: no needed.
			if (!hasEntry("OrderBy")){
				$scope.inst.OrderBy={'entry':[]};
			}
					
			//get parameter: default value is set from queryobject at this stage
			$scope.inst['$queryobj']=$scope.queryobj;
			$scope.params=fxReportInitParamValues($scope.inst, $scope.queryobj, instCache);
			$scope.inited=true;
		});
	});
	
});


angular.module("flexdms.report").controller("fxReportViewerCtrl", function($scope, Inst, instCache, Util, reportService, $filter){
	
	$scope.queryinst=null;
	$scope.queryTargetedType=null;
	$scope.orderdirections=Util.getEnum({'classname':'com.flexdms.flexims.query.OrderDirection'});
	$scope.inited=false;
	$scope.$watch("inst.Query", function(){
		if (!$scope.inst.Query){
			return;
		}
		var queryid=$scope.inst.Query;
		if (angular.isObject(queryid)){
			queryid=$scope.inst.Query.id;
		}
		//get all query used by this report recursively.
		reportService.getAllTraceDown(queryid).then(function(data){
			//!!!!!!!!!!!!!!!!!!!!These are the four major objects available at top level.
			$scope.queryobj=new flexdms.query.defaulttypedquery(instCache.getInst("DefaultTypedQuery", queryid));
			$scope.inst['$queryobj']=$scope.queryobj;
			//get parameter: default value is set from queryobject at this stage
			$scope.params=fxReportInitParamValues($scope.inst, $scope.queryobj, instCache);
			$scope.inited=true;
		});
	});
	$scope.instpscope.isExecutable=function(){
		if (!$scope.inited){
			return false;
		}
		for (var i=0; i<$scope.params.length; i++){
			if (!$scope.params[i].isReady()){
				return false;
			}
		}
		//has to select one property to show
		if ($scope.inst.Properties.entry.length==0){
			return false;
		}
		return true;
	};
	
	$scope.displayPropText=function(propname){
		if (!$scope.inited){
			return propname;
		}
		return $scope.queryobj.getTargetedType().getProp(propname).getDisplayText();
	}
	$scope.paramValue=function(param){
		if (!angular.isDefined(param) || param.value==null){
			return "";
		}
		
		if (!angular.isArray(param.value)){
			if (angular.isDate(param.value)) {
				return     $filter('date')(param.value, flexdms.dateFormat);
			} else {
				return param.value;
			}
		} 
		var ret="";
		angular.forEach(param.value, function(v){
			if (angular.isDate(v)) {
				ret+=$filter('date')(v, flexdms.dateFormat);
			} else {
				ret+=v;
			}
			ret+=",";
		});
		return ret;
	}
	
	
});
flexdms.config.addConfig("viewer", "FxReport", "actions", function(){
	var ret=angular.copy(flexdms.config.getConfig("viewer", 'default', 'actions'));
	ret.push("<a type='button' class='btn btn-primary' title='execute' data-ng-show='isExecutable()' href='#/report/{{inst.id}}' >Execute</a>");
	return ret;
})	
flexdms.config.addConfig("viewer", "FxReport", "ctrl", "fxReportViewerCtrl");

flexdms.config.addConfig("editor", "FxReport", "actions", function(){
	var ret=angular.copy(flexdms.config.getConfig("editor", 'default', 'actions'));
	ret.push("<button class='btn btn-default'  data-ng-click='preview();' data-ng-disabled='instform.$invalid  || !isExecutable()'>Preview</button>");
	return ret;
})	
flexdms.config.addConfig("editor", "FxReport", "ctrl", "fxReportEditorCtrl");
