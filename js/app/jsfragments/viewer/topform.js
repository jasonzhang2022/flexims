/**
 * @ngdoc directive
 * @name instDirective.directive:fxInstViewerForm
 * @restrict AE
 * @description
 * 
 * Create a prepackaged form to view an instance. It exposes <b>refresh</b> method which refetches data from server  and redisplay current instance.
 * 
 * @scope
 * 
 * @param {string} fx-typename the instance type
 * @param {number=} inst-id: instance id. Required if the form is used for editing instead of adding. In case of edit, 
 * if the instance real type is different from the specified type, real subtype of the instance instead of specified type is used to generate the form 
 *  @param {String=} template-url a template url. If not specified, {@link instDirective.fxViewTemplates} is used to decide the template url.
 * 
 * 
 */
angular.module("instDirective").directive("fxInstViewerForm",function($compile, $templateCache,Inst, $state, $injector){
	return {
		replace:true, 
		restrict: 'AE',
		scope:true,
		//http://stackoverflow.com/questions/21835471/angular-js-directive-dynamic-templateurl
		template: function(tElement, tAttrs){
			//user-specified url
			if (angular.isDefined(tAttrs.templateUrl) && tAttrs.templateUrl!=""){
				var templateUrl=tAttrs.templateUrl;
				return $templateCache.get(templateUrl);
			}
			//default url.
			return  '<div ng-include="formUrl"></div>';
		},
		controller : function($scope, $element, $attrs, instCache, securityInfoService){
			
			var fxViewTemplates=$injector.get("fxViewTemplates");
			if (angular.isDefined($attrs.fxViewTemplates)){
				fxViewTemplates=$injector.get($attrs.fxViewTemplates);
			}
			$scope.fxViewTemplates=fxViewTemplates;
			$scope.typename=$attrs.fxTypename;
			$scope.type=flexdms.findType($scope.typename);
			
			//cachr attributes information
			$scope.formUrl=$scope.fxViewTemplates.getViewTopUrl($scope.typename);
			$scope.instid=$attrs.fxInstId;
			
			//load inst if needed.
			$scope.inst=instCache.getInst($scope.typename, $scope.instid);
			
			$scope.refresh=function(){
				var id=$scope.instid;
				instCache.refreshInst($scope.typename, id);
				$state.go("viewinst", {typename:$scope.typename, 'id':id}, {reload:true});
			};
			 
			 $scope.$watch(function(){
					return flexdms.fxuser;
				}, function(newvalue, oldvalue){
					if (angular.isDefined(newvalue) && !angular.isDefined($scope.actions)){
						securityInfoService.getPermissions(flexdms.fxuser.Name, $scope.typename, $scope.instid).then(function(result){
							$scope.actions=result;
						}, function(){
							//do nothing, use default results.
						});
					}
				});
		}
	};
});
