/**
 * @ngdoc directive
 * @name instDirective.directive:fxInstEditorForm
 * @restrict AE
 * @description
 * 
 * Create a prepackaged form to edit/add an instance. It exposes <b>saveInst</b> method which saves current instance and transition to 'viewinst' state.
 * 
 * @scope
 * 
 * @param {string} fx-typename the instance type
 * @param {number=} inst-id: instance id. Required if the form is used for editing instead of adding. In case of edit, 
 * if the instance real type is different from the specified type, real subtype of the instance instead of specified type is used to generate the form 
 * @param {String=} template-url a template url. If not specified, {@link instDirective.fxTemplates} is used to decide the template url.
 * 
 * 
 * 
 */
angular.module("instDirective").directive("fxInstEditorForm",function($compile,fxTemplates, $templateCache,Inst){
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
	      controller : function($scope, $element, $attrs, instCache, $state){
	    	  $scope.typename=$attrs.fxTypename;
	    	  
	    	  function decideFormUrl(){
    			  if ($scope.edit){
    					 $scope.formUrl=fxTemplates.getEditFormUrl($scope.typename);
    				 } else{
    					 $scope.formUrl=fxTemplates.getAddFormUrl($scope.typename);
    				 }
    		  }
	    	  if (angular.isDefined($attrs.fxInstId)){
	    		  $scope.edit=true;
	    		  $scope.inst=instCache.getInst($scope.typename, $attrs.fxInstId);
	    		  //we edit a copy
	    		  instCache.deleteInst($scope.typename, $attrs.fxInstId);
	    		  
	    		  //inst loaded has different typename from the one specified.
	    		  //could be a subtype.
	    		  $scope.inst.$promise.then(function(inst1){
	    			  if (inst1[flexdms.insttype]!=$scope.typename){
	    				  $scope.typename=inst1[flexdms.insttype];
		    			  decideFormUrl();
	    			  }
	    		  });
	    		  
	    		  
	    	  } else {
	    		  $scope.inst=Inst.newInst($scope.typename);
	    		  $scope.edit=false;
	    	  }
	    	  decideFormUrl();
	    	  $scope.saveInst=function(){
	    		  $scope.inst.$save(null, function(inst1){
	    			  $state.go("viewinst", {typename:$scope.typename, id:inst1.id});
	    		  });
	    	  };
	      }
	};
});
