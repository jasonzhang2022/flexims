angular.module("flexdms.report").directive("fxRelationEditor", function($compile, $templateCache){
	return {
		restrict:'E',
		controller: function($scope, $element, $attrs, instCache, $modal, $timeout, reportService){
			$scope.relationui=flexdms.getExtraPropObj($scope.propobj.prop, 'relationui');
			if ($scope.relationui==null){
				return;
			}
			$scope.relationui.showselected=flexdms.parseTrueFalse($scope.relationui.showselected);
			if (!$scope.relationui.report){
				return;
			}
			
			$scope.options={};
			
			//what is selected;
			if ($scope.propobj.isRequired()){
				$scope.options.keepLastSelected=true;
			} else {
				$scope.options.keepLastSelected=false;
			}
			
			
			//multi select or single select
			if ($scope.propobj.isCollection()){
				$scope.options.multiSelect=true;
			} else {
				$scope.options.multiSelect=false;
			}
			if ($scope.relationui.uitype==flexdms.query.relationui.uitype[0]){
				$scope.options.enablePaging=false;
			} else {
				$scope.options.enablePaging=true;
			}
		
			$scope.options.selectedItems=[];
			//initialize select options after instance is loaded
			$scope.inst.$promise.then(function(){
				var propvalue=$scope.inst[$scope.propobj.getName()];
				if ($scope.propobj.isCollection() || $scope.relationui.uitype!=flexdms.query.relationui.uitype[0]) {
					
					
					if ($scope.propobj.isCollection()){
						if (propvalue && propvalue.length>0){
							angular.forEach(propvalue, function(id){
								$scope.options.selectedItems.push(instCache.getInst($scope.propobj.getTypeObject().value, id));
							})
						}
					} else if (propvalue) {
						$scope.options.selectedItems.push(instCache.getInst($scope.propobj.getTypeObject().value, propvalue));
					}
				} 	else if (propvalue) {
					$scope.options.selectedItems=instCache.getInst($scope.propobj.getTypeObject().value, propvalue);
				}
				
				//changed by grid or select
				if ($scope.propobj.isCollection() || $scope.relationui.uitype!=flexdms.query.relationui.uitype[0]) {
					$scope.$watchCollection("options.selectedItems", function(){
						if (!$scope.inst[$scope.propobj.getName()]){
							$scope.inst[$scope.propobj.getName()]=[];
						}
						$scope.inst[$scope.propobj.getName()].length=0;
						if ( $scope.options.selectedItems.length>0){
							if ($scope.propobj.isCollection()){
								angular.forEach($scope.options.selectedItems, function(s){
									$scope.inst[$scope.propobj.getName()].push(s.id);
								});
							} else {
								$scope.inst[$scope.propobj.getName()]=$scope.options.selectedItems[0].id;
							}
							
						}
					});
					
				} else{
					$scope.$watch("options.selectedItems", function(){
						if (angular.isObject($scope.options.selectedItems)) {
							$scope.inst[$scope.propobj.getName()]=$scope.options.selectedItems.id;
						} else {
							$scope.inst[$scope.propobj.getName()]=null;
						}
					});
					
				}
			});
			
			
			
			$scope.refreshing=false;
			$scope.timeout=null;
		
			reportService.getReportById($scope.relationui.report).then(function(rpt){
				$scope.report=rpt;
				//use by select, listbox, and autocomplete
				$scope.firstProp=$scope.report.Properties.entry[0].value;
				var hasprops=false;
				angular.forEach($scope.relationui.params, function(p){
					if (angular.isDefined(p) && p!==null){
						hasprops=true;
					}
				})
				if (!hasprops){
					//do does not depend on any property, static report can run itself
					return ;
				}
				
			
				$scope.$watch(
						//dependent properties changed?
						function() {
							var obj={};
							angular.forEach($scope.relationui.params, function(param){
								if (param && angular.isDefined($scope.inst[param])){
									obj[param]=$scope.inst[param]
								}
							});
							return angular.toJson(obj);
						}, 
						
						//what to do.
						function(newvalue, oldvalue){
							if (newvalue===oldvalue && $scope.setParamOnce){
								return;
							}
							if ($scope.setParamOnce){
								//we are refreshing the page.
								$scope.refreshing=true;
							}
							$scope.setQueryParamValue();
							$scope.setParamOnce=true;
							if ($scope.timeout!=null){
								//avoid rapid firing
								$timeout.cancel($scope.timeout);
							}
							$scope.timeout=$timeout(function(){
								$scope.refreshing=false;
							}, 500);
						}
				);
			});
			
			
			
			$scope.setQueryParamValue=function(){
				var params=$scope.report['$queryobj'].getParams(instCache);
				for(var i=0; i<$scope.relationui.params.length; i++){
					var propname=$scope.relationui.params[i];
					var entry=$scope.report.ParamValues.paramValues[i];
					
					//use default value from report itself.
					if (!angular.isDefined(propname) || propname==null){
						continue;
					}
					if (!angular.isDefined($scope.inst[propname]) || $scope.inst[propname]==null || $scope.inst[propname]===''){
						//remove default value so the query is not executable unless value is set from dependent property
						entry.values=null;
						continue;
					}
					 
					var propvalue=$scope.inst[propname];
					 var op=new flexdms.query.Operator(params[i].condition.inst.Operator);
					 var numOfOperands=op.howManyOperands();
					//set a single parameters
					if (numOfOperands==1 || numOfOperands==2){
						if (angular.isArray(propvalue)){
							
							if (propvalue.length>0 && propvalue[0]!==''){
								entry.values=[propvalue[0]];
							} else {
								//remove default value so the query is not executable unless value is set from dependent property
								entry.values=null;
							}
						} else {
							entry.values=[propvalue];
						}
						continue;	
					}
					//set value for multiple
					if (angular.isArray(propvalue)){
						if (propvalue.length>0 && propvalue[0]!==''){
							entry.values=propvalue;
						} else {
							entry.values=null;
						}
					} else {
						entry.values=[propvalue];
					} 
				}
			
			};
			
			$scope.isReportExecutable=function(){
				return flexdms.isReportExecutable($scope.report, instCache);
			}
			
			$scope.showPopuptable=function(){
				var modalInstance = $modal.open({
					templateUrl:"template/report/relation_editor_popup_table.html",
					scope:$scope,
					controller: function($scope, $modalInstance){
						$scope.ok=function(){
							 $modalInstance.close();
						}
					},
					size: 'lg'
				});
				
			};
		},
		link: function($scope, $element, $attrs){
			//deafult
			if ($scope.relationui===null ){
				$element.html(flexdms.query.originalrelationeditor);
				$compile($element.contents())($scope);
				return;
			}

			//show select
			var topelement=angular.element($templateCache.get("template/report/relation_editor_top.html"));
			if ($scope.relationui.showselected && ($scope.propobj.isCollection() || $scope.relationui.uitype!==flexdms.query.relationui.uitype[0])){
				topelement.find(".selected").html($templateCache.get("template/report/relation_editor_showselected.html"));
			}
			var editorhtml=flexdms.query.originalrelationeditor;
			
			//no report, default ui.
			if (!$scope.relationui.report){
				editorhtml=flexdms.query.originalrelationeditor
				topelement.find(".editor").html(editorhtml);
				$element.html(topelement.get(0).outerHTML);
				$compile($element.contents())($scope);
				return;
			} 
			
			if ($scope.relationui.uitype==flexdms.query.relationui.uitype[0]){
				if ($scope.propobj.isCollection()){
					editorhtml="template/report/relation_editor_listbox.html";
				} else {
					editorhtml="template/report/relation_editor_select.html";
				}
			} else  if ($scope.relationui.uitype==flexdms.query.relationui.uitype[1]){
				editorhtml="template/report/relation_editor_inlinetable.html";
			} else  if ($scope.relationui.uitype==flexdms.query.relationui.uitype[2]){
				editorhtml="template/report/relation_editor_popup_btn.html";
			}
			var html="<ng-include src=\"'"+editorhtml+"'\"/>";
			topelement.find(".editor").html(html);
			$element.html(topelement.get(0).outerHTML);
			$compile($element.contents())($scope);
		}
	}
});

angular.module("flexdms.report").run(function($templateCache){
	flexdms.query.originalrelationeditor=$templateCache.get("template/props/edit/default/simplerelation.html");
	$templateCache.put("template/props/edit/default/simplerelation.html", "<fx-relation-editor/>");
});

