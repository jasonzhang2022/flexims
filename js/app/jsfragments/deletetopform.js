angular.module("instDirective").directive("fxInstDeleteForm", function($compile, $templateCache,Inst){
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
			return  '<div ng-include="\'template/props/delete/deleteinst.html\'"></div>';
		},
		controller : function($scope, $element, $attrs, instCache){
			$scope.typename=$attrs.fxTypename;
			$scope.type=flexdms.findType($scope.typename);
			$scope.inst=instCache.getInst($scope.typename, $attrs.fxInstId);
			var vprops=$scope.type.getViewProps();
			var props=new Array();
			angular.forEach(vprops, function(prop){
				if (prop.isBasic() && !prop.isEmbedded()) {
					props.push(prop);
				}
			});
			$scope.simpleprops=props;
			$scope.deleted=false;
			$scope.deleteInst=function(){
				$scope.inst.$delete({typename:$scope.typename, id:$scope.inst.id}, function(){
					$scope.deleted=true;
					instCache.deleteInst($scope.typename, $scope.inst.id);
					
				});
			};
		}
	};
});
