angular.module("flexdms.report").controller("TypedQueryController", function($scope, Util){
	//The targeted type
	var targetTypes={};
	angular.forEach(flexdms.types, function(type){
		targetTypes[type.getName()]=type.getName()+":"+type.getDescription();
	});

	$scope.targetTypes=targetTypes;
	$scope.inst.$promise.then(function(){
		$scope.queryobject=new flexdms.query.defaulttypedquery($scope.inst);
	});
	
	$scope.$watch("inst.TargetedType", function(typename, oldname){
		if (typename){
			$scope.queriableprops=$scope.queryobject.getQueriableProps();
			if (oldname && typename!==oldname){
				//clear all conditions applies to old type
				$scope.inst.Conditions=[];
			}
		}
		
	});

	$scope.opmap={};
	Util.getEnum({'classname':'com.flexdms.flexims.query.Operator'}).$promise.then( function(ops){
		angular.forEach(ops, function(op){
			$scope.opmap[op.key]=op;
		});
	});
	$scope.timeunits=Util.getEnum({'classname':'com.flexdms.flexims.util.TimeUnit'});
});

angular.module("flexdms.report").controller("PropertyConditionController", function($scope, Util, reportService){

	var generalOps=["eq", "ne", "notnull", "isnull",  "oneof", "notoneof"];
	var sizeOps=["notempty", "empty",  "size", "sizeGt", "sizeLt"];
	var boolOps=["checked", "unchecked"];
	var compaisonOps=["lt", "le", "gt", "ge", "ne", "eq","between", "notbetween", "notnull", "isnull", "oneof", "notoneof"];
	var stringOps=["eq", "ne", "like", "notlike", "notnull", "isnull",  "oneof", "notoneof"];
	var timeOps=["eq", "ne", "notnull", "isnull", "oneof", "notoneof"];
	var comparisonPlusSizeOps= ["lt", "le", "gt", "ge", "ne", "eq","between", "notbetween", "notnull", "isnull", "oneof", "notoneof","notempty", "empty",  "size", "sizeGt", "sizeLt"]; 
	var stringPlusSizeOps= ["eq", "ne", "like", "notlike", "notnull", "isnull",  "oneof", "notoneof", "notempty", "empty",  "size", "sizeGt", "sizeLt"];
	var timePlusSizeOps= ["eq", "ne", "notnull", "isnull", "oneof", "notoneof","notempty", "empty",  "size", "sizeGt", "sizeLt"];
	var generalPlusSizeOps= ["eq", "ne", "notnull", "isnull",  "oneof", "notoneof","notempty", "empty",  "size", "sizeGt", "sizeLt"];
	var typeCollectionOps=["eq", "ne", "oneof", "notoneof", "tracedown", "notempty", "empty",  "size", "sizeGt", "sizeLt"];
	var typeOps=["notnull", "isnull", "eq", "ne", "oneof", "notoneof", "tracedown"];
	function decideOperator(propobj){
		var multiple=propobj.isCollection();
		var proptype=propobj.getTypeObject();
		if (proptype.isPrimitive() && !proptype.isCustomObject())
		{
			if (proptype.isTrueFalse())
			{
				return boolOps;
			} else if (proptype.isNumber()||proptype.isDateOnly() || proptype.isTimestamp() ||proptype.isTimeOnly())
			{
				if (multiple)
				{
					return comparisonPlusSizeOps;
				} 
				return compaisonOps;
			} else if (proptype.isStringType())
			{
				if (multiple)
				{
					return stringPlusSizeOps;
				} 
				return stringOps;

			}  
//			else if (proptype.isTimeOnly())
//			{
//				if (multiple)
//				{
//					return timePlusSizeOps;
//				}
//				return timeOps;
//			} 
			else
			{
				if (multiple)
				{
					return generalPlusSizeOps;
				} 
				return generalOps;			
			}
		}  
		if (proptype.isCustomObject()){
			return new Array();
		}
		if (multiple)
		{
			return typeCollectionOps;
		} 
		return typeOps;
	}
	$scope.queryobject=new flexdms.query.defaulttypedquery($scope.inst[flexdms.parentinst]);
	//get the prop object for selected property in condition
	$scope.getQueryPropertyObj=function(){ 
		var targetedType=$scope.queryobject.getTargetedType();
		var propobj=undefined;
		if (targetedType!=null && $scope.inst['Property']){
			propobj=targetedType.getProp($scope.inst['Property']);
		}
		return propobj;
	};
	
	//availbale operator for selected property
	$scope.operatoroptions=function(){
		var propobj=$scope.getQueryPropertyObj();
		if (propobj==null){
			return new Array();
		}
		var opstrs= decideOperator(propobj);
		var ops=new Array();
		var opmap=flexdms.searchForData($scope, "opmap");
		angular.forEach(opstrs, function(opstr){
			ops.push(opmap[opstr]);
		});
		return ops;
	};
	$scope.showFirst=function(propobj){
		//show first only we need value
		var opobj=new flexdms.query.Operator($scope.inst.Operator);
		return opobj.howManyOperands()>0 && opobj.op!='tracedown';
	};
	$scope.showSecond=function(propobj){
		//show first only we need value
		var opobj=new flexdms.query.Operator($scope.inst.Operator);
		if (opobj.howManyOperands()>2){
			return false;
		}
		return opobj.howManyOperands()>1;
	};
	$scope.showIgnoreCase=function(propobj){
		return propobj.getTypeObject().isStringType();
	};
	
	$scope.showRelativeStartDate=function(propobj){
		var opobj=new flexdms.query.Operator($scope.inst.Operator);
		return ( propobj.getTypeObject().isDateOnly()|| propobj.getTypeObject().isTimestamp()) && opobj.howManyOperands()>=1 &&  opobj.isComparison();
	};
	$scope.showRelativeEndDate=function(propobj){
		var opobj=new flexdms.query.Operator($scope.inst.Operator);
		return ( propobj.getTypeObject().isDateOnly()|| propobj.getTypeObject().isTimestamp()) && opobj.howManyOperands()>=2 &&  opobj.isComparison();
	};
	$scope.showWholeTime=function(propobj){
		var opobj=new flexdms.query.Operator($scope.inst.Operator);
		if (opobj.isSize()){
			return false;
		}
		if (opobj.howManyOperands()>2){
			return false;
		}
		return ( propobj.getTypeObject().isDateOnly()|| propobj.getTypeObject().isTimestamp());
	};
	$scope.showCollectionMode=function(propobj){
		var opobj=new flexdms.query.Operator($scope.inst.Operator);
		if (opobj.isSize()){
			return false;
		}
		return propobj.isCollection();
	};
	$scope.showTraceDown=function(propobj){
		var op=$scope.inst.Operator;
		if (op!="tracedown"){
			return false;
		}
		return true;
	};
	
	$scope.isTraceDownRequired=function(){
		return $scope.inst.Operator==='tracedown';
	};
	
	//whether to show form field for a property for PropertyCondition
	$scope.showProp=function(propobj){
		var propname=propobj.getName();
		if (propname=="Description" ||propname=="Property" ||propname=="Operator"){
			return true;
		}
		var propobj=$scope.getQueryPropertyObj();
		//no query property selected yet, show nothing
		if (!angular.isDefined(propobj) ||propobj===null){
			return false;
		}
		var op=$scope.inst.Operator;
		if (!op){
			//no operator is selected yet.
			return false;
		}
		switch(propname){
		case 'FirstValue':
			return $scope.showFirst(propobj);
		case 'SecondValue':
			return $scope.showSecond(propobj);
		case "IgnoreCase":
			return $scope.showIgnoreCase(propobj);
		case "RelativeStartDate":
		case "RelativeStartUnit":
			return $scope.showRelativeStartDate(propobj);
		case "RelativeEndDate":
		case "RelativeEndUnit":
			return $scope.showRelativeEndDate(propobj);
		case "WholeTime":
			return $scope.showWholeTime(propobj);
		case "TraceDown":
			return $scope.showTraceDown(propobj);
		case "CollectionMode":
			return $scope.showCollectionMode(propobj);
		default:
			return true;
				
			
		}
		return true;
	};
	
	function  cleanCondition(newvalue, oldvalue){
		if (newvalue && oldvalue && newvalue!=oldvalue){
			$scope.inst.FirstValue=null;
			$scope.inst.SecondValue=null;
			$scope.inst.RelativeStartDate=null;
			$scope.inst.RelativeEndDate=null;
			$scope.inst.TraceDown=null;
			
		}
		
	}
	$scope.$watch("inst.Property", function(newvalue, oldvalue){
		cleanCondition(newvalue, oldvalue);
		if (newvalue){
			var propobj=$scope.getQueryPropertyObj();
			if (propobj.isEmbedded() || propobj.isEmbeddedElementCollection()|| propobj.isRelation()){
				reportService.getQueriesByType(propobj.getTypeObject().value).then(function(data){
					$scope.possibletracdown=data;
				});
			}
		}
	});
	$scope.$watch("inst.Operator", cleanCondition);
});

angular.module("flexdms.report").controller("TypedQueryControllerViewer", function($scope, Util){
	$scope.$watch("inst", function(inst){
		if (inst){
			$scope.inst.$promise.then(function(){
				$scope.queryobj=new flexdms.query.defaulttypedquery($scope.inst);
				$scope.queryType=$scope.queryobj.getTargetedType();
			});
		}
	});
	
	
	$scope.opmap={};
	Util.getEnum({'classname':'com.flexdms.flexims.query.Operator'}).$promise.then( function(ops){
		angular.forEach(ops, function(op){
			$scope.opmap[op.key]=op;
		});
	});
	$scope.timeunits=Util.getEnum({'classname':'com.flexdms.flexims.util.TimeUnit'});
	
});

angular.module("flexdms.report").controller("PropertyConditionControllerViewer", function($scope, $filter){
	
	$scope.conditionProp=$scope.instpscope.queryType.getProp($scope.inst['Property']);
	
	
	$scope.showProp=function(propobj){
		var propname=propobj.getName();
		if (propname=="Description" ||propname=="Property" ||propname=="Operator"){
			return true;
		}
		if ($scope.inst[propname]){
			return true;
		}
		return false;
	};
	
	function formatValue(propobj, modelValue, $filter){
		var proptype=propobj.getTypeObject();
		
		if (proptype.isDateOnly()){
			var date=new Date(modelValue);
			return $filter('date')(date, flexdms.dateFormat);
		}
		if (proptype.isTimestamp()){
			var date=new Date(modelValue);
			return $filter('date')(date, flexdms.dateTimeFormat);
		}
	
		if (proptype.isTimeOnly()){
			var date=new Date(modelValue);
			return $filter('date')(date, flexdms.timeFormat);
		}
		return modelValue;
	}
	$scope.formatPropValue=function(value){
		if (!angular.isDefined(value)|| value==null){
			return "";
		}
		if (!$scope.conditionProp.getTypeObject().isDateTime()){
			return value;
		}
		var opobj=new flexdms.query.Operator($scope.inst.Operator);
		if (opobj.howManyOperands()==0){
			return value;
		}
		if (opobj.howManyOperands()==1||opobj.howManyOperands()==2){
			return formatValue(propobj, value, $filter);
		}
		
		//many values
		var vs=value.split(/,\s*/);
		var ret=new Array();
		for (var i=0; i<vs.length; i++){
			var v=formatValue($scope.conditionProp, vs[i], $filter);
			ret.push(v);
		}
		return ret.join()
		
		
	}
});

angular.module("flexdms.report").directive("fxQueryParamValueValidator", function( $filter){
	function resetError(ctrl){
		for(var key in ctrl.$error){
			ctrl.$setValidity(key, true);
		}
	}
	function validateSingleValue(prop, viewValue1, ctrl){
		var proptype=prop.getTypeObject();
		
		var viewValue=viewValue1;
		if (proptype.isDateOnly()){
			if(isNaN(Date.parse(viewValue))){
				ctrl.$setValidity("date", false);
				return undefined;
			}
			//the value from browser is the local value.
			//so we operate on local hours instea of UTC
			var date=new Date(viewValue);
			date.setHours(0, 0, 0,0);
			return date.toISOString();
		}
		if (proptype.isTimestamp()){
			if(isNaN(Date.parse(viewValue))){
				ctrl.$setValidity("datetime", false);
				return undefined;
			}
			var date=new Date(viewValue);
			date.setMilliseconds(0);
			return date.toISOString();
		}
	
		if (proptype.isTimeOnly()){
			if(isNaN(Date.parse(viewValue)) && isNaN(Date.parse("1970-01-01T"+viewValue))){
				ctrl.$setValidity("time", false);
				return undefined;
			}
			var date=null;
			if (isNaN(Date.parse(viewValue))){
				date=new Date('1970-01-01T'+viewValue);
			} else {
				date=new Date(viewValue);
			}
			date.setFullYear(1970, 0, 1);
			return date.toISOString();
		}
		
		var typepat=null;
		var errorkey=null;
		if(proptype.isInteger()){
			errorkey="integer";
			typepat=flexdms.INTEGER_REGEXP;
		} else if (proptype.isNumber()){
			errorkey="number";
			typepat=flexdms.NUMBER_REGEXP;
		}else if (proptype.isRelation()){
			errorkey="integer";
			typepat=flexdms.INTEGER_REGEXP;
		}
		if (typepat!=null){
			if (!typepat.test(viewValue)) {
				ctrl.$setValidity(errorkey, false);
				return undefined;
			}
		}
		return viewValue;
	}
	function formatValue(propobj, modelValue, ctrl, $filter){
		var proptype=propobj.getTypeObject();
		
		if (proptype.isDateOnly()){
			if(isNaN(Date.parse(modelValue))){
				ctrl.$setValidity("date", false);
				return undefined;
			}
			var date=new Date(modelValue);
			return $filter('date')(date, flexdms.dateFormat);
		}
		if (proptype.isTimestamp()){
			if(isNaN(Date.parse(modelValue))){
				ctrl.$setValidity("datetime", false);
				return undefined;
			}
			var date=new Date(modelValue);
			return $filter('date')(date, flexdms.dateTimeFormat);
		}
	
		if (proptype.isTimeOnly()){
			if(isNaN(Date.parse(modelValue))){
				ctrl.$setValidity("time", false);
				return undefined;
			}
			var date=new Date(modelValue);
			return $filter('date')(date, flexdms.timeFormat);
		}
		return modelValue;
		
	}

	return {
		require:"ngModel",
		restrict: 'A',
	    link: function(scope, iElement, iAttrs, ctrl, transcludeFn) {
	    	scope.field=ctrl;
	    	ctrl.$parsers.push(function(viewValue1) {
	    		var viewValue=viewValue1;
	    		resetError(ctrl);
	    		var propobj=scope.getQueryPropertyObj();
	    		if(propobj===null){
	    			return undefined;
	    		}
	    		if (!scope.inst.Operator){
	    			return undefined;
	    		}
	    		var opobj=new flexdms.query.Operator(scope.inst.Operator);
	    		if (opobj.howManyOperands()==0){
	    			return undefined;
	    		}
	    		if (opobj.isSize()){
	    			if (!flexdms.INTEGER_REGEXP.test(viewValue)){
	    				ctrl.$setValidity("integer", false);
	    				return undefined;
	    			}
	    			return viewValue;
	    		}
	    		if (opobj.howManyOperands()==1||opobj.howManyOperands()==2){
	    			return validateSingleValue(propobj, viewValue, ctrl);
	    		}
	    		var vs=viewValue.split(/,\s*/);
	    		var ret=new Array();
	    		for (var i=0; i<vs.length; i++){
	    			if (ctrl.$isEmpty(vs[i])){
	    				continue;
	    			}
	    			var v=validateSingleValue(propobj, vs[i], ctrl);
	    			if (!angular.isDefined(v)){
	    				return undefined;
	    			} 
	    			ret.push(v);
	    		}
	    		return ret.join()
	    	});
	    	
	    	
	    	ctrl.$formatters.push(function(modelValue){
	    		resetError(ctrl);
	    		if (!angular.isDefined(modelValue) || modelValue===null){
	    			return modelValue;
	    		}
	    		var propobj=scope.getQueryPropertyObj();
	    		if(propobj===null){
	    			return undefined;
	    		}
	    		if (!scope.inst.Operator){
	    			return undefined;
	    		}
	    		var opobj=new flexdms.query.Operator(scope.inst.Operator);
	    		if (opobj.howManyOperands()==0){
	    			return undefined;
	    		}
	    		if (opobj.howManyOperands()==1||opobj.howManyOperands()==2){
	    			return formatValue(propobj, modelValue, ctrl, $filter);
	    		}
	    		
	    		//many values
	    		var vs=modelValue.split(/,\s*/);
	    		var ret=new Array();
	    		for (var i=0; i<vs.length; i++){
	    			if (ctrl.$isEmpty(vs[i])){
	    				continue;
	    			}
	    			var v=formatValue(propobj, vs[i], ctrl, $filter);
	    			if (!angular.isDefined(v)){
	    				return undefined;
	    			} 
	    			ret.push(v);
	    		}
	    		return ret.join()
	    		
	    	});
	    }
	};
});
//--------------type-specific controller
flexdms.config.addConfig("editor", "TypedQuery", "ctrl",'TypedQueryController' );
flexdms.config.addConfig("viewer", "TypedQuery", "ctrl",'TypedQueryControllerViewer' );
flexdms.config.addConfig("editor", "DefaultTypedQuery", "ctrl",'TypedQueryController' );
flexdms.config.addConfig("viewer", "DefaultTypedQuery", "ctrl",'TypedQueryControllerViewer' );
flexdms.config.addConfig("editor", "PropertyCondition", "ctrl",'PropertyConditionController' );
flexdms.config.addConfig("viewer", "PropertyCondition", "ctrl",'PropertyConditionControllerViewer' );
flexdms.config.addConfig("viewer", "DefaultTypedQuery", "actions", function(){
	var ret=angular.copy(flexdms.config.getConfig("viewer", 'default', 'actions'));
	ret.push("<a type='button' class='btn btn-default' title='Add Report' data-ng-href='#/addinst/FxReport?Query={{instid}}&Name={{inst.Name}}'>Add Report</a>");
	return ret;
});


