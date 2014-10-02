/**
 * @ngdoc directive
 * @name instDirective.directive:fxInstDeleteForm
 * @restrict AE
 * @description
 * 
 * Create a prepackaged form to delete an instance. It exposes <b>delete</b> method which deletes current instance.
 * 
 * @scope
 * 
 * @param {string} fx-typename the instance type
 * @param {number=} inst-id: instance id. Required if the form is used for editing instead of adding. In case of edit, 
 * if the instance real type is different from the specified type, real subtype of the instance instead of specified type is used to generate the form 
 * @param {string=} template-url alternative template-url for the form instead of the default one.
 * 
 * 
 */
angular.module("instDirective").directive("fxInstDeleteForm", function($compile, $templateCache){
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
		controller : function($scope, $element, $attrs, instCache, Inst){
			$scope.typename=$attrs.fxTypename;
			$scope.type=flexdms.findType($scope.typename);
			
			$scope.insts=[];
			
			angular.forEach($attrs.fxInstId.split(","), function(id){
				$scope.insts.push(instCache.getInst($scope.typename, id));
			});
			var vprops=$scope.type.getViewProps();
			var props=new Array();
			angular.forEach(vprops, function(prop){
				if (prop.isBasic() && !prop.isEmbedded()) {
					props.push(prop);
				}
			});
			$scope.simpleprops=props;
			$scope.deleted=false;
			$scope.deleteInsts=function(){
				Inst.remove({typename:$scope.typename, id:$attrs.fxInstId}, function(){
					$scope.deleted=true;
					angular.forEach($scope.insts, function(inst){
						instCache.deleteInst($scope.typename, inst.id);
					});
				});
			};
		}
	};
});
