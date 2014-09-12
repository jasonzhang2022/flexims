
flexdms.fxDecideViewTemplate=function(fxViewTemplates, prop){
	if(prop.getTypeObject().isEmbedded()){
		if (prop.isCollection()) {
			var input = fxViewTemplates.getMultipleEmbedded(prop);
			return input;
		}
		
		var input = fxViewTemplates.getSingleEmbedded(prop);
		return input;
		
	}
	//for simple property with single value
	//if (prop.isBasic() || (prop.isElementCollection() && fxViewTemplates.useSingleFieldForMultiple(prop))) {
	if (prop.isBasic() ) {
		//if (prop.isBasic()){
		var view = fxViewTemplates.getSimpleViewTemplate(prop);
		return view;
	}
	
	//for simple property with multiple values.
	if (prop.isElementCollection()){
		var multiple_layout = fxViewTemplates.getMultipleLayout(prop);
		var input = fxViewTemplates.getSimpleViewTemplate(prop);
		var topelement=angular.element(multiple_layout);
		topelement.find(".childvalue").replaceWith(input);
		return topelement.get(0).outerHTML;
	}
	if (prop.getTypeObject().isRelation()){
		if (prop.isCollection()){
			var multiple_layout = fxViewTemplates.getMultipleLayout(prop);
			var input = fxViewTemplates.getSimpleViewTemplate(prop);
			var topelement=angular.element(multiple_layout);
			topelement.find(".childvalue").replaceWith(input);
			return topelement.get(0).outerHTML;
		}
		var view = fxViewTemplates.getSimpleViewTemplate(prop);
		return view;
	}
};

/**
 * @ngdoc directive
 * @name instDirective.directive:fxPropViewer
 * @restrict E
 * @description
 * 
 * Create a property viewer. 
 * 
 * If flexdms.config.viewer.TYPENAME.props.PROPNAME.ctrl is not null, it is used as controller for this property viewer.
 * 
 *  It expects that  <b>Type</b> object and <b>Inst</b> object are available in current scope.
 *  
 *   This directive is used by {@link instDirective.directive:fxInstViewer} internally.
 *   
 * @param {Expression=} propobj an expression returns a Property object or a property name.  If not specified, it is looked up from current scope. 
 *
 */
angular.module("instDirective").directive("fxPropViewer",function($compile, $injector){
	return {
		replace:true, //since this is replace, this editor will not be called the second time.
		restrict: 'E',
//		scope:{
//			inst:'=', //a inst resource object
//			propobj:'=' //propobj.
//				
//		},
		scope:true,
		template: "<div class='propvalue' />",
		controller:function($scope, $element, $attrs){
			
			//Template function could be overridden by attribute
			var fxViewTemplates=$injector.get("fxViewTemplates");
			if (angular.isDefined($attrs.fxViewTemplates)){
				fxViewTemplates=$injector.get($attrs.fxViewTemplates);
			} else if (angular.isDefined($scope.$parent.fxViewTemplates)){
				fxViewTemplates=$scope.$parent.fxViewTemplates;
			}
			$scope.fxViewTemplates=fxViewTemplates;
			
			if (angular.isDefined($attrs.propobj)){
				var propobj=$scope.$eval($attrs.propobj);
				if (angular.isString(propobj)){
					propobj=$scope.type.getProp(propobj);
				}
				$scope.propobj=propobj;
			}
			$scope.listClass= {
					proplist:true, 
					simple: $scope.propobj.isElementCollection() ||$scope.propobj.isRelation(),
					singlefield:fxViewTemplates.useSingleFieldForMultiple($scope.propobj), 
					multiplefield:!fxViewTemplates.useSingleFieldForMultiple($scope.propobj),
			};
			$scope.listClass[$scope.propobj.getName()]=true;
			$scope.dateFormat=flexdms.dateFormat;
			$scope.timeFormat=flexdms.timeFormat;
			$scope.dateTimeFormat=flexdms.dateTimeFormat;
			/*
			 * Use watch to retrieve value
			 * The inst could be changed dynamically. 1 asynchronous loadig
			 * 2. object switching like ngGrid.
			 */
			$scope.$watch("inst", function(v){
				$scope.propvalue=$scope.inst[$scope.propobj.getName()];	
			});
			
			//agument scope, then compile so scope is ready before recompile
			var propctrl=flexdms.config.getConfig("viewer", $scope.$parent.typename, "props", $scope.propobj.getName(), "ctrl");
			if (propctrl){
				$element.data(propctrl, $controller(propctrl, {'$scope': $scope}));
			}
		
		},
		link : function(scope, iElement, iAttrs, form, transcludeFn,  $controller) {
			
			
			
			var fxViewTemplates=scope.fxViewTemplates;
			var template=flexdms.fxDecideViewTemplate(fxViewTemplates, scope.propobj);
			iElement.html(template);
			$compile(iElement.contents())(scope);
			
			
			
		}
	};
});
