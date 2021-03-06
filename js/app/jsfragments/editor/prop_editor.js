
flexdms.decideEditTemplate=function(prop, fxTemplates){
	if(prop.getTypeObject().isEmbedded()){
		if (prop.isCollection()) {
			var input = fxTemplates.getMultipleEmbedded(prop);
			return input;
		}
		var input = fxTemplates.getSingleEmbedded(prop);
		return input;
	}
	if (prop.getTypeObject().isCustomObject()){
		var input = fxTemplates.getSimpleInputTemplate(prop);
		return input;
	}
	
	//allowed value use select
	if (prop.getAllowedValues()!=null){
		var simple_layout = fxTemplates.getSimpleLayout(prop);
		var topelement=angular.element(simple_layout);
		topelement.find(".propinput").html(fxTemplates.getSimpleSelect(prop));
		topelement.find(".prophelp").html(fxTemplates.getRequiredError());
		return  topelement.get(0).outerHTML;
		
	}
	
	//for simple property with single value
	if (prop.isBasic() || (prop.isElementCollection() && fxTemplates.useSingleFieldForMultiple(prop))) {
		var simple_layout = fxTemplates.getSimpleLayout(prop);
		var topelement=angular.element(simple_layout);
		topelement.find(".propinput").html(fxTemplates.getSimpleInputTemplate(prop));
		topelement.find(".prophelp").html(fxTemplates.getErrorTemplate(prop));
		return  topelement.get(0).outerHTML;
	}
	
	//for simple property with multiple values.
	if (prop.isElementCollection()){
		var multiple_layout = fxTemplates.getMultipleLayout(prop);
		var topelement=angular.element(multiple_layout);
		topelement.find(".propinput").html(fxTemplates.getSimpleInputTemplate(prop));
		topelement.find(".prophelp").html(fxTemplates.getErrorTemplate(prop));
		return  topelement.get(0).outerHTML;	
	}
	
	if (prop.isRelation()){
		var simple_layout = fxTemplates.getSimpleLayout(prop);
		var topelement=angular.element(simple_layout);
		topelement.find(".propinput").html(fxTemplates.getSimpleInputTemplate(prop));
		topelement.find(".prophelp").html(fxTemplates.getErrorTemplate(prop));
		return  topelement.get(0).outerHTML;
	}
};
/**
 * @ngdoc directive
 * @name instDirective.directive:fxPropEditor
 * @requires ngForm
 * @restrict E
 * @description
 * 
 * Create a property editor.
 * 
 * If flexdms.config.editor.TYPENAME.props.PROPNAME.ctrl is not null, it is used as controller for this property editor.
 * 
 *  It expects that  <b>Type</b> object and <b>Inst</b> object are available in current scope.
 * 
 *  This directive is used by {@link instDirective.directive:fxInstEditor} internally.
 * @param {Expression=} propobj an expression returns a Property object or a property name.  If not specified, it is looked up from current scope. 
 *  
 */
angular.module("instDirective").directive("fxPropEditor",function($compile,fxTemplates){
	return {
		require:"^form",
		replace:true, //since this is replace, this editor will not be called the second time.
		restrict: 'E',
		template: "<div class=\"propinputandhelp\"/>",
		scope:true,
		controller: function($scope, $element, $attrs){
			if (angular.isDefined($attrs.propobj)){
				//use the one from attrs instead of parent.
				var propobj=$scope.$eval($attrs.propobj);
				if (angular.isString(propobj)){
					propobj=$scope.type.getProp(propobj);
				}
				$scope.propobj=propobj;
			}
			var propctrl=flexdms.config.getConfig("editor", $scope.$parent.typename, "props", $scope.propobj.getName(), "ctrl");
			if (propctrl){
				var controller=$controller(propctrl, {'$scope': $scope});
				$element.data(propctrl, controller);
			}
		
			
		},
		link : function(scope, iElement, iAttrs, form, transcludeFn,  $controller) {
			var template=flexdms.decideEditTemplate(scope.propobj, fxTemplates);
			iElement.html(template);
			$compile(iElement.contents())(scope);
			
		}
	};
}).controller("relationAdder", function($scope, fxInstPopup){
	
	$scope.showAdder=flexdms.parseTrueFalse($scope.propobj.getExtraProp("showRelationAdder"));
	
	$scope.addrelation=function($event){
		var propobj=$scope.propobj;
		fxInstPopup.popupInstEditor(propobj.getTypeObject().value, null, true, true, false)
		.then(function(inst){
			if (propobj.isCollection()){
				if (!angular.isDefined($scope.inst[propobj.getName()]) ||$scope.inst[propobj.getName()]==null ){
					$scope.inst[propobj.getName()]=[];
				}
				$scope.inst[propobj.getName()].push(inst.id);
			} else {
				$scope.inst[propobj.getName()]=inst.id;
			}
		});
	};
});
