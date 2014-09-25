angular.module("instDirective").controller("instEditorController", function($scope, $element, $attrs, $controller, fxTemplates, instCache, $rootScope){
	this.name="instEditor";
	
	//get a hold the parent scope so we can use its variable, function
	$scope.instpscope=$scope.$parent;
	//show label or not
	if (!angular.isDefined($scope.showlabel)){
		if (angular.isDefined($scope.$parent.showlabel)){
			$scope.showlabel=$scope.$parent.showlabel;
		} else {
			if ($rootScope.smallscreen){
				$scope.showlabel=false;
			} else {
				$scope.showlabel=true;
			}
			
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
		var propclasses=[$scope.propclass?$scope.propclass:'form-group', "prop", propobj.getName()];
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
 * @ngdoc directive
 * @name instDirective.directive:fxInstEditor
 * @module instDirective
 * @kind directive
 * @requires ngForm
 * @restrict AE
 * @description
 * 
 * Create a prepackaged insance editor inside a form. It creates a <b>isolated scope</b>. If flexdms.config.editor.TYPENAME.ctrl is not null, it is used as controller for this inst editor.
 * 
 * @scope
 * 
 * @param {boolean=} [showlabel=true] Whether to show lable or not.
 * @param {string} typename type of the instance to edit 
 * @param {object|number} inst instance resource object or instance id
 * @param {boolean=} [edit=true] edit an instance or add an instance
 * @param {Array.string|string|Array.Object=} props a list of properties to edit. Can be a list of name separated by ',',  an array of propname, an array of prop object.
 * @param {String=} template-url a template url. If not specified, {@link instDirective.fxTemplates} is used to decide the template url.
 * @param {String=} propclass what class attached to every prop div, default to 'form-group'
 * 
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
			propclass: "@?"
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
