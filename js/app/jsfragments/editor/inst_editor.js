
angular.module("instDirective").controller("instEditorController", function($scope, $element, $attrs, $controller, fxTemplates, instCache){
	this.name="instEditor";
	
	//get a hold the parent scope so we can use its variable, function
	$scope.instpscope=$scope.$parent;
	
	//show label or not
	if (!angular.isDefined($scope.showlabel)){
		if (angular.isDefined($scope.$parent.showlabel)){
			$scope.showlabel=$scope.$parent.showlabel;
		} else {
			$scope.showlabel=true;
		}
	}
	
	
	//chnage inst from id to type.
	if (!angular.isObject($scope.inst)){
		$scope.inst=instCache.getInst($scope.typename, $scope.inst);
	} else {
		if (!angular.isDefined($scope.typename)){
			$scope.typename=$scope.inst[flexdms.insttype];
		}
	}
	
	function changedTypename(){
		 $scope.instUrl=fxTemplates.getInstUrl($scope.typename);
		 $scope.type=flexdms.findType($scope.typename);
		 
		if ($attrs.props){
			$scope.props=flexdms.parseEditViewProps($scope.instpscope.$eval($attrs.props), $scope.type, true);
		} else {
			$scope.props=flexdms.parseEditViewProps($scope.instpscope.props, $scope.type, true);
		}
		
		//dynamic controller for inst\
		var ctrl=flexdms.config.getConfig("editor", $scope.typename, 'ctrl');
		if (ctrl){
			var controller=$controller(flexdms.config.getConfig("editor", $scope.typename, 'ctrl'), {"$scope":$scope});
			$element.data(ctrl, controller);
		}
	}
	changedTypename();
	
	//only useful in edit case
	$scope.inst.$promise.then(function(){
		if (!angular.isDefined($scope.edit)){
			if (angular.isDefined($scope.inst.id)) {
				$scope.edit=true;
			} else {
				$scope.edit=true;
			}
		}
		
		if ($scope.inst[flexdms.insttype]!=$scope.typename){
			changedTypename();
		}
		
		$scope.instClass=[];
		$scope.instClass.push($scope.type.getName());
		if (flexdms.config.getConfig("editor", $scope.typename, 'cssClass')){
			$scope.instClass.push(flexdms.config.getConfig("editor", $scope.typename, 'cssClass'));
		}

	});
	
	$scope.searchForData=function(prop){
		return flexdms.searchForData($scope, prop);
	};
	
	//----------------------utility to help the property label 
	$scope.propclasses=function(propobj){
		var propclasses=["form-group", "prop", propobj.getName()];
		if (propobj.getTypeObject().isEmbedded()){
			propclasses.push('embeddedprop');
		}
		if (!propobj.getTypeObject().isEmbedded()){
			propclasses.push('simpleprop');
		}
			
		return propclasses;
	};
	
	$scope.propTooltip=function(propobj){
		return flexdms.htmlTtooltip(flexdms.editTooltip(propobj, fxTemplates));
	};
	
	$scope.showInvalid=function(field){
		 if (angular.isDefined(field)){
			 return field.$dirty && field.$invalid;	 
		 }
		 return false;
	};
	
	$scope.propLabelClasses=function(field){
		var labelclasses=["proplabel"];
		//if (angular.isDefined($scope.form[$scope.propname])? $scope.form[$scope.propname].$invalid:false,)
		if (angular.isDefined(field) && field.$invalid){
			labelclasses.push("has-error");
		}
		return labelclasses;
	};
});
//-----------------------------instance-level directive
/**
 * Set up a scope to edit an instance
 * supported attributes
 * 	showlable=true|false;
 *  props= a list of properties to be editted. Default to type.getEditProps.
 * precondition: 
 * 	inst and type object from parent scope
 *  under form controller so validation of form control can be channelled
 */
angular.module("instDirective").directive("fxInstEditor",function( $templateCache){
	return {
		require:"^form", //a form or ngForm to attach validation state
		replace:true, 
		restrict: 'AE',
		scope:{
			inst:"=", //inst resource object or id.
			typename: "=", //required
			showlabel:"=?",
			edit:"=?",
		},
		//http://stackoverflow.com/questions/21835471/angular-js-directive-dynamic-templateurl
		 template: function(tElement, tAttrs){
			 //user-specified url
			 if (angular.isDefined(tAttrs.templateUrl) && tAttrs.templateUrl!=""){
					var templateUrl=tAttrs.templateUrl;
					return $templateCache.get(templateUrl);
				}
			 //default url.
			 return  '<div ng-include="instUrl" ng-class="instClass" ></div>';
		 },
		controller :"instEditorController",
		link: function($scope, $element, $attrs, ctrl){
			$scope.form=ctrl;
		}
	};
});
