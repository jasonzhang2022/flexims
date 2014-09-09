//--------------template for query param input
flexdms.query.tplPrefix="template/queryparam/";
flexdms.query.getinputTemplate=function(param){
	var prop=param.condition.getProp();
	var proptype=prop.getTypeObject();
	var op=new flexdms.query.Operator(param.condition.inst.Operator);
	var numOfOperands=op.howManyOperands();
	if (numOfOperands==0){
		return flexdms.query.tplPrefix+"novalue.html";
	}
	if (numOfOperands==1 && op.isSize()){
		return flexdms.query.tplPrefix+"size.html";
	}
	if (proptype.isNumber()){
		if (numOfOperands<=2){
			return flexdms.query.tplPrefix+"num.html";
		} 
		return flexdms.query.tplPrefix+"nums.html";
	}
	
	if (proptype.isDateOnly()){
		if (numOfOperands<=2){
			return flexdms.query.tplPrefix+"date.html";
		} 
		return flexdms.query.tplPrefix+"dates.html";
	}
	if (proptype.isTimestamp()){
		if (numOfOperands<=2){
			return flexdms.query.tplPrefix+"datetime.html";
		} 
		return flexdms.query.tplPrefix+"datetimes.html";
	}
	if (proptype.isTimeOnly()){
		if (numOfOperands<=2){
			return flexdms.query.tplPrefix+"time.html";
		} 
		return flexdms.query.tplPrefix+"times.html";
	}
	if (proptype.isStringType()){
		if (numOfOperands<=2){
			return flexdms.query.tplPrefix+"string.html";
		} 
		return flexdms.query.tplPrefix+"strings.html";
	}
	if (proptype.isRelation() || proptype.isEmbedded()){
		if (numOfOperands<=2){
			return flexdms.query.tplPrefix+"num.html";
		} 
		return flexdms.query.tplPrefix+"nums.html";
	}
	if (numOfOperands<=2){
		return flexdms.query.tplPrefix+"string.html";
	} 
	return flexdms.query.tplPrefix+"strings.html";
};


//Decide the input template for the template parameter
angular.module("flexdms.report").directive("queryParam", function($compile){
	return {
		restrict: 'A',
		scope:true,
		controller: function($scope){
			$scope.inputurl=flexdms.query.getinputTemplate($scope.param);
    		$scope.openDatePicker=function($event){
    			 $event.preventDefault();
    			 $event.stopPropagation();
    			 this.showdatepicker = true;
    		};
		},
	};
});
//controler to operates on query param
angular.module("flexdms.report").controller("fxQueryParamCtrl", function($scope, Util){
	//whether a query param need input or not.
	$scope.needInput=function(param){
		return param.needInput();
	};
	$scope.timeunits=Util.getEnum({'classname':'com.flexdms.flexims.util.TimeUnit'});
	$scope.daterequired=function(param){
		return !angular.isDefined(param.relativevalue)||param.relativevalue==null;
	};
	
});

/**
 * For query params with multiple values such as 'contains'.
 * If multiple input fields are needed, this directive controls the add and remove
 */
angular.module("flexdms.report").controller("queryparamMultiple", function($scope){
	
	$scope.values=[];
	if (angular.isDefined($scope.param.value) && $scope.param.value!==null){
		angular.forEach($scope.param.value, function(v){
			$scope.values.push({'value':v});
		});
	}
	
	$scope.addItem=function(){
		$scope.values.push({'value':null});
	};
	
	$scope.removeItem=function(index){
		$scope.values.splice(index, 1);
	};
	$scope.$watch("values", function(){
		//$scope.inst[$scope.propname]
		
		//trasnfer value from multiple to inst
		var newvs=new Array();
		angular.forEach($scope.values, function(v){
			if (v.value ) {
				newvs.push(v.value);
			}
		});
		$scope.param.value=newvs;
	}, true);
});
