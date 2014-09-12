
/**
 * Set up a scope to edit an instance
 * supported attributes
 * 	showlable=true|false;
 *  props= a list of properties to be editted. Default to type.getViewProps.
 * precondition: 
 * 	inst and type object from parent scope
 */
angular.module("instDirective").controller("fxInstViewerController", function($scope, $element, $attrs, instCache, $controller, $state, $injector){
	this.$name="instViewer";
	
	//get a hold the parent scope so we can use its variable, function
	$scope.instpscope=$scope.$parent;
	
	
	var fxViewTemplates=$injector.get("fxViewTemplates");
	if (angular.isDefined($attrs.fxViewTemplates)){
		fxViewTemplates=$injector.get($attrs.fxViewTemplates);
	} else if (angular.isDefined($scope.$parent.fxViewTemplates)){
		fxViewTemplates=$scope.$parent.fxViewTemplates;
	}
	$scope.fxViewTemplates=fxViewTemplates;
	
	//show label or not
	if (!angular.isDefined($scope.showlabel)){
		if (angular.isDefined($scope.$parent.showlabel)){
			$scope.showlabel=$scope.$parent.showlabel;
		} else {
			$scope.showlabel=true;
		}
	}
	
	
	//make typename and type ready. They are needed in linked function.
	if (!angular.isDefined($scope.inst)){
		$scope.type=flexdms.findType($scope.typename);
		//inst is not available yet.
	} else if (!angular.isObject($scope.inst)){
		$scope.type=flexdms.findType($scope.typename);
		$scope.inst=instCache.getInst($scope.typename, $scope.inst);
	} else{
		if (!angular.isDefined($scope.typename)){
			$scope.typename=$scope.inst[flexdms.insttype];
		} 
		$scope.type=flexdms.findType($scope.typename);
	}
	$scope.instUrl=$scope.fxViewTemplates.getInstUrl($scope.typename);
	var ctrl=flexdms.config.getConfig("viewer", $scope.typename, 'ctrl');
	if (ctrl){
		var controller=$controller(ctrl, {"$scope":$scope});
		$element.data(ctrl, controller);
	}
	
	$scope.instClass=[];
	$scope.instClass.push($scope.typename);
	if (flexdms.config.getConfig("viewer", $scope.typename, 'cssClass')){
		$scope.instClass.push(flexdms.config.getConfig("viewer", $scope.typename, 'cssClass'));
	}
	
	function init(){
		if ($attrs.props){
			$scope.props=flexdms.parseEditViewProps($scope.instpscope.$eval($attrs.props), $scope.type, false);
		} else {
			$scope.props=flexdms.parseEditViewProps($scope.instpscope.props, $scope.type, false);
		}
		
		
	}
	
	
	
	$scope.searchForData=function(prop){
		return flexdms.searchForData($scope, prop);
	};
	if (!angular.isDefined($scope.inst)){
		$scope.$watch("inst", function(){
			$scope.inst.$promise.then(init);
		});
	} else {
		$scope.inst.$promise.then(init);
	}
	
	
	 $scope.tooltip=function(propobj) {
		 flexdms.htmlTtooltip(flexdms.viewTooltip(propobj, fxViewTemplates));
	 };
});

/**
 * @ngdoc directive
 * @name instDirective.directive:fxInstViewer
 * @requires ngForm
 * @restrict AE
 * @description
 * 
 * Create a prepackaged instance viewer. It creates a <b>isolated scope</b>.If flexdms.config.viewer.TYPENAME.ctrl is not null, it is used as controller for this inst viewer.
 * 
 * @scope
 * 
 * @param {boolean=} [showlabel=true] Whether to show lable or not.
 * @param {string} typename type of the instance to edit 
 * @param {object|number} inst instance resource object or instance id
 * @param {Array.string|string|Array.Object=} props a list of properties to edit. Can be a list of name separated by ',',  an array of propname, an array of prop object.
 * @param {String=} template-url a template url. If not specified, {@link instDirective.fxViewTemplates} is used to decide the template url.
 * 
 */
angular.module("instDirective").directive("fxInstViewer", function($templateCache){
	return {
		replace:true, 
		restrict: 'AE',
		scope:{
			inst:"=", //inst resource object or id.
			typename: "=?", //required if the inst is id. not required if inst is resource object
			showlabel:"=?"
		},
		//http://stackoverflow.com/questions/21835471/angular-js-directive-dynamic-templateurl
		 template: function(tElement, tAttrs){
			 //user-specified url
			 if (angular.isDefined(tAttrs.templateUrl) && tAttrs.templateUrl!=""){
					var templateUrl=tAttrs.templateUrl;
					return $templareCache.get(templateUrl);
				}
			 //default url.
			 return  '<div ng-include="instUrl" ng-class="instClass" ></div>';
		 },
		controller :"fxInstViewerController",
	};
});

