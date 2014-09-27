
flexdms.isReportExecutable=function(report, instCache){
	if (!angular.isDefined(report) ||report==null){
		return false;
	}
	 if (!report.$resolved){
		 return false;
	 }
	 if (!angular.isDefined(report['$queryobj'])){
		 return false;
	 }
	 if (report.Properties.entry.length==0){
		return false;
	}
	 if (angular.isDefined(report.ParamValues) && angular.isDefined(report.ParamValues.paramValues)){
		 var params=report['$queryobj'].getParams(instCache);
		 for(var i=0; i<report.ParamValues.paramValues.length; i++){
			 var op=new flexdms.query.Operator(params[i].condition.inst.Operator);
			 var numOfOperands=op.howManyOperands();
			 if (numOfOperands==0){
				 continue;
			 }
			 if (report.ParamValues.paramValues[i].values===null || report.ParamValues.paramValues[i].values.length==0){
				 return false;
			 }
		 }
	 }
	 return true;
}
//used by row template
angular.module("flexdms.report").directive("fxReportInst", function($compile){
	return {
		restrict: 'A',
		scope:false, //ng-grid row alreay has scope.
		priority: 505, //make this before inst viewer controller
		controller: function($scope, $element, $attrs){
			$scope.$watch("row.entity", function(v){
				$scope.inst=$scope.row.entity;
			})
		}
	};
});

//used by column template
angular.module("flexdms.report").directive("fxReportProperty", function($compile, $controller){
	return {
		restrict: 'A',
		scope:true, //ng-grid row alreay has scope.
		priority: 505, 
		controller: function($scope, $element, $attrs){
			$scope.propobj=$scope.type.getProp($scope.col.field);
			if ($scope.col.field.indexOf(".")!=-1){
				var i=$scope.col.field.indexOf(".");
				var firstname=$scope.col.field.substring(0, i);
				$scope.$watch("$parent.inst", function(newvalue){
					if (newvalue){
						$scope.inst=$scope.$parent.inst[firstname];
					}
				});
				
				//subtype controller
				var type=$scope.propobj.getBelongingType();
				var ctrl=flexdms.config.getConfig("viewer", type.getName(), 'ctrl');
				if (ctrl){
					var controller=$controller(ctrl, {"$scope":$scope});
					$element.data(ctrl, controller);
				}
			}
			
		}
	};
});




angular.module("flexdms.report").controller("fxReportCtrl", function($scope, $element, $attrs, instCache, reportService, $templateCache, instsToExcel){
	//repare scope.
	if (!angular.isDefined($scope.report)){
		//not from isolated scope, report is required for isolated scope.
		$scope.$parent.$watch("report", function(newvalue, oldvalue){
			if (newvalue){
				$scope.report=newvalue;
			}
		});
	}
	if (!angular.isDefined($scope.options)){
		if (angular.isDefined($scope.$parent.options)){
			$scope.options=$scope.$parent.options;
		} else {
			$scope.options={};
		}
	}
	if (!angular.isDefined($scope.showHeader)){
		$scope.showHeader=false;
	}
	if (!angular.isDefined($scope.refreshing)){
		$scope.refreshing=false;
	}
	
	$scope.executing=false;
	$scope.executed=false;
	$scope.internalIsExecutable=function(){
		if ($scope.refreshing){
			//parent is in refreshing stage.
			$scope.executing=false;
			$scope.pagingOptions.currentPage=1;
			return false;
		}
		var ret=flexdms.isReportExecutable($scope.report, instCache)
		
		if (ret && !$scope.executing){
			$scope.executing=true;
			$scope.executeQuery();
		}
		return ret;
	};
	//-----------------------retrieve data from server.
	$scope.executeQuery=function(){
		instCache.getInst("TypedQuery", $scope.report.Query).$promise.then(function(query){
			//type is needed for instance  display
			$scope.type=flexdms.findType(query.TargetedType);
			$scope.typename=$scope.type.getName();
			$scope.firstProp=$scope.report.Properties.entry[0].value;
			var reportWrapper={'FxReport':$scope.report};	
			var promise=reportService.prepare(reportWrapper);
			promise.then(function(data){
				//!!!!!!!!!!!!!server is ready to serve data
				$scope.reportWrapper=data;
			
				$scope.initGridOption();
				if ($scope.gridOptions. enablePaging){
					$scope.fetchPage();
				} else{
					$scope.fetchAll();
				}
				$scope.executed=true;
			});
		});
		
	};
	$scope.fetchPage=function(){
		reportService.fetchPartial($scope.reportWrapper, 
				$scope.pagingOptions.pageSize*($scope.pagingOptions.currentPage-1), 
				$scope.pagingOptions.pageSize)
				.then(function(data){
					$scope.results=data;
//					if (!$scope.$$phase) {
//					$scope.$apply();
//					}
				});
	};
	
	$scope.fetchAll=function(){
		reportService.fetchAll($scope.reportWrapper)
		.then(function(data){
			$scope.results=data;
		});
	};
	$scope.refresh=function($event){
		$event.preventDefault();
		$event.stopPropagation();
		$scope.fetchPage();
	};
	
	
	//destroy report if this element is removed from controller.
	$scope.$on('$destroy', function() {
		if (angular.isDefined($scope.reportWrapper) && angular.isDefined($scope.reportWrapper.uuid) && $scope.reportWrapper.uuid!=null){
			reportService.destory($scope.reportWrapper);
		}
	});
	
	
	//-------------------------environment variable for inst/propviewer inside  grid.
	$scope.showlabel=false;  //for inst
	$scope.notimplemented=function($event){
		$event.preventDefault();
		$event.stopPropagation();
	};
	
	//----------------------report grid related information.
	$scope.reporturl=function(format, nested){
		if (!angular.isDefined($scope.reportWrapper)){
			return "";
		}
		if (!nested){
			nested=false;
		}
		return flexdms.reportserviceurl+"/fetchall/"+$scope.reportWrapper.uuid+"?format="+format+"&nested="+nested;
	};
	
	$scope.toExcel=function(){
		//nested.
		reportService.fetchAll($scope.reportWrapper, true)
		.then(function(data){
			instsToExcel.toExcel(data, $scope.report.Name);
		});
	}
	//------------------------grid options
	//control the paging
	$scope.pagingOptions = {
			pageSizes: [40, 100, 200,400,500,1000, 20, 10, 5],
			pageSize: 100,
			currentPage: 1,
	};	
	$scope.$watch('pagingOptions', function (newVal, oldVal) {
		if (newVal !== oldVal && newVal.currentPage !== oldVal.currentPage) {
			setTimeout( function(){
				$scope.fetchPage();
			}, 100);
			
		}
	}, true);
	
	$scope.columnDefs=[];
	$scope.initGridOption=function(){
		$scope.gridOptions={
				data:"results", 
				primaryKey:"id",
				columnDefs:'columnDefs',
				enablePaging: true,
				showFooter: true,
				totalServerItems: 'reportWrapper.count',
				pagingOptions: $scope.pagingOptions,
				showSelectionCheckbox:true,
				rowTemplate:'template/reportgrid/row.html',
				// plugins: [new ngGridCsvExportPlugin()]
				
		};
		
		//extra option from report.
		if (angular.isDefined($scope.reportWrapper.FxReport.gridOptions) && angular.isDefined($scope.reportWrapper.FxReport.gridOptions.entry)){
			angular.forEach($scope.reportWrapper.FxReport.gridOptions.entry, function(opt){
				$scope.gridOptions[opt.key]=opt.value;
			});
		}
		//column definition.
		var targetedType=$scope.type;
		$scope.columnDefs.length=0;
		
		//find out which column is shown as link to the current entity managed by current row.
		var backEntityEntry=null;
		angular.forEach($scope.report.Properties.entry, function(entry){
			//default first one.
			if (backEntityEntry===null){
				backEntityEntry=entry;
				return;
			}
			var propname=entry.value;
			if (propname.indexOf(".")!==-1){
				//nested property can be linked back to main entity
				return;
			}
			var propobj=targetedType.getProp(propname);
			if (propobj.isSummaryProp()){
				backEntityEntry=entry;
			}
			return;
		});
		
		//define column
		angular.forEach($scope.report.Properties.entry, function(entry){
			var propname=entry.value;
			var propobj=targetedType.getProp(propname);
			$scope.columnDefs.push(
					{'field':propname, 
						'displayName':propobj.getDisplayText(),
						//'cellTemplate':ret,
						'cellTemplate':entry===backEntityEntry?$templateCache.get("template/reportgrid/linked_cell.html"):$templateCache.get("template/reportgrid/cell.html")
								
					});
		});
		
		//set any options passed from outside.
		for (var prop in $scope.options){
			$scope.gridOptions[prop]=$scope.options[prop];
		}
	};
});

/**
 * @ngdoc directive
 * @name flexdms.report.fxReportGrid
 * @module flexdms.report
 * @restrict E
 * @description
 * 
 * show a report. This directive creates <b>an isolated scope</b>
 * 
 * @scope
 * 
 * @param {Object} report report instance
 * @param {Object=} options report grid options
 * @param {boolean=} [showHeader=true] whether to show report header
 * @param {boolean=} refreshing whether parent DOM is in refreshig stage
 * 
 * 
 */
angular.module("flexdms.report").directive("fxReportGrid", function(){
	return {
		restrict:'E',
		scope: {
			'report':'=',
			'options':'=?',
			'showHeader':'=?',
			'refreshing':"=?",
		}, 
		templateUrl:'template/report/report_nggrid.html',
		controller:'fxReportCtrl'
	};
});

/**
 * @ngdoc directive
 * @name flexdms.report.fxAllReport
 * @module flexdms.report
 * @restrict AE
 * @description
 * 
 * Show ALL instance for a type.
 * 
 * @scope
 * 
 * @param {string} typename the type to show all instances for
 * 
 */
angular.module("flexdms.report").directive("fxAllReport", function(){
	return {
		restrict: 'AE',
		scope: true,
		controller : function($scope, $element, $attrs, reportService, instCache){
			reportService.getAllReport($attrs.typename).then(function(data){
				$scope.report=data;
			});	
		},
		template:"<fx-report-grid report='report' show-header='true' />",
	};
});
angular.module("flexdms.report").config(function($stateProvider){
	 $stateProvider.state('report', {
			url : "/report/:id",
			controller: function($scope, $stateParams, report, reportService, instCache ){
				$scope.report=report;
			},
			template:"<fx-report-grid report='report' show-header='true'/>",
			resolve: {
				report: function($stateParams, instCache, reportService){
					//this is a httpPromise;
					return reportService.getReportById($stateParams.id);
				}
			}
				
		}).state('allreport', {
			url : "/allreport/:typename",
			template: function(stateParams){
				return "<fx-all-report typename='"+stateParams.typename+"'/>";
			}	
		});
});
