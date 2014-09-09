
//------------------------------handle embedded
/**
 * supply a name for ngForm directive.
 * initialize embedded instance properly.
 * set up environment by child instEditor: inst, type, props
 */
angular.module("instDirective").directive("singleEmbededEditorDecorator",function(){
	return  {
		require:"form",
		restrict: 'A',
		scope:true,
		priority:50, //make this before form
		controller :function($scope, $element, $attrs, Inst){
			this.$name="singleEmbededEditor";
			var propname=$scope.propobj.getName();
			if ($scope.propobj.isCollection()){
				//inst is from ngrpeat
				$attrs.$set("ngForm", propname+$scope.$index);
			} else {
				$attrs.$set("ngForm",propname);
				//inst
				if (!angular.isDefined($scope.inst[propname]) ||$scope.inst[propname]==null){
					var embededInst=Inst.newInst($scope.propobj.getTypeObject().value);
					$scope.inst[propname]=embededInst;
					embededInst[flexdms.parentinst]=$scope.inst;
				}
			}
		}, 
		link: function(scope, element, attrs, form){
//			if (scope.propobj.isCollection()){
//				scope.field=form[attrs.ngForm];
//			} else{
//				scope.field=form;
//			}
			scope.field=form;
			if (!angular.isDefined(scope.$parent.field)){
				//parent could have have field such as a form for multiple embedded.
				scope.$parent.field=form;
			}
		}
	};
});
