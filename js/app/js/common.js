



angular.module('flexdms.common.error', ['ng']).service("fxAlert", function(){
	
	this.setErrorObject=function(rsMsg){
		this.rsMsg=rsMsg;
	};
	
	this.showAlert=function(msg){
		alert(msg);
	};
	this.getErrorObject=function(){
		return this.rsMsg;
	};
	
}).config(function($provide, $httpProvider) {
	$provide.factory('errorInterceptor', function($q, fxAlert) {
		return {
			'response': function(response) {
				
				var rsMsg=null;
				if (angular.isObject(response.data) && angular.isDefined(response.data.rsMsg)){
					if (response.data.rsMsg.statuscode!=0){
						rsMsg=response.data.rsMsg;
					}
				}
				//alert no content by default
				var alertNoContent=true;
				if (angular.isDefined(response.config["fxAlertNoContent"]) && !response.config["fxAlertNoContent"]){
					alertNoContent=false;
				}
				
				if (alertNoContent && response.status==204){
					rsMsg={
						statuscode: 204,
						msg: "Request data does not exist"
					};
				}
				
				//no error
				if (rsMsg===null ){
					return  response || $q.when(response);
				}
				
				

				if (angular.isDefined(response.config["fxSkipError"]) && response.config["fxSkipError"]){
					return  response || $q.when(response);
				}
				//set error by default
				if (!angular.isDefined(response.config["fxSetError"]) || response.config["fxSetError"]){
					fxAlert.setErrorObject(rsMsg);
				}
				
				//alert by dfeault
				if (!angular.isDefined(response.config["fxAlertError"]) || response.config["fxAlertError"]){
					fxAlert.showAlert(rsMsg.msg);
				}
				
				//reject by default
				if (!angular.isDefined(response.config["fxRejectOnError"]) || response.config["fxRejectOnError"]){
					return  $q.reject(response.data.rsMsg);
				}
				
				if (alertNoContent && response.status==204){
					return $q.reject(response);
				}
				
				
				return  response || $q.when(response);
			},
			// optional method
			'responseError': function(rejection) {	
				var rsMsg=null;
				if (angular.isObject(rejection.data) && angular.isDefined(rejection.data.rsMsg)){
					rsMsg=rejection.data.rsMsg;
				}
				if (rsMsg===null){
					rsMsg={
							statuscode: rejection.status,
							msg:rejection.statusText
					};
				}
				//set error by default
				if (!angular.isDefined(rejection.config) || !angular.isDefined(rejection.config["fxSetError"]) || rejection.config["fxSetError"]){
					fxAlert.setErrorObject(rsMsg);
				}
				
				//not alert by dfeault
				if (!angular.isDefined(rejection.config) || !angular.isDefined(rejection.config["fxAlertError"]) || rejection.config["fxAlertError"]){
					fxAlert.showAlert(rsMsg.msg);
				}
				
				return $q.reject(rejection);
			}
		};
	});

	//$httpProvider.defaults.headers.common['Accept']="application/json,text/html,application/xhtml+xml,application/xml";
	//$httpProvider.defaults.headers.post['Accept']="application/json,text/html,application/xhtml+xml,application/xml";
	$httpProvider.interceptors.push('errorInterceptor');
});

angular.module('flexdms.common.fxAlert', ['ui.bootstrap', 'flexdms.common.error']).config(function($provide){
	$provide.decorator("fxAlert", function($delegate, $timeout, $rootScope, $injector){
		
		
		var template="<div id='fxalert'class='alertbox alertbox-show ng-hide' ng-show='showAlertMsg'>" +
				"<div>{{msg}}</div>" +
				"<div><button type='button' class='close' ng-click='close();'><span aria-hidden='true'>&times;</span><span class='sr-only'>Close</span></button></div>" +
				"</div>" ;
		
		var tholder={
				timer: null,
		};
		$delegate.showAlert=function(msg){
		
			
			if ($("#fxalert").length==0){
				var $scope=$rootScope.$new();
				var element=$injector.get("$compile")(angular.element(template))($scope);
				$("body").append(element);
				
				$scope.close=function(){
					$scope.showAlertMsg=false;
					if(tholder.timer!=null){
						$timeout.cancel(tholder.timer);
						tholder.timer=null;
					}
				};
			}
			var $scope=angular.element(document.getElementById("fxalert")).scope();
			$scope.showAlertMsg=true;
			$scope.msg=msg;
			if (tholder.timer!=null){
				$timeout.cancel(tholder.timer);
			}
			//digest only evaluate current scope
			//apply, evaluet function and call digest on rootScope
			//$scope.$digest();
			
			tholder.timer=$timeout(function(){
				$scope.showAlertMsg=false;
				tholder.timer=null;
			}, 7000);
		};
		return $delegate;
	});
});

angular.module('flexdms.common', [])
.directive('fxDynamicName', function() {
	/*
	 * give a name to input under ng-repeat so that is has a name.
	 */
	 return {
	    restrict: 'A',
	    priority: 500, 
	    controller : function($scope, $element, $attrs){
	         var name = $scope.$eval($attrs.fxDynamicName);
	         delete($attrs['fxDynamicName']);
	         $element.removeAttr('data-fx-dynamic-name');
	         $element.removeAttr('fx-dynamic-name');
	         if (angular.isDefined($attrs.ngForm)){
	        	 $attrs.$set("ngForm", name);
	         } else {
	        	  $attrs.$set("name", name);
	         }
	       
	    }
	  };
	}).directive('fxCollapseItem', function($rootScope) {
		/*
		 * give a name to input under ng-repeat so that is has a name.
		 */
		 return {
		    restrict: 'A', 
		    scope:true,
		    controller : function($scope, $element, $attrs){
		    	if ($rootScope.smallscreen){
		    		$scope.collapsed=true;
		    	} else {
		    		$scope.collapsed=false;
		    	}
		    	
		    	$scope.collapseMenu=function(){
		    		if ($rootScope.smallscreen){
		    			$scope.collapsed=true;
		    		}
		    		
		    	};
		    }
		  };
		})
	.run(function($window, $rootScope){
		
		
		if ($window.innerWidth<768){
			$rootScope.smallscreen=true;
		} else {
			$rootScope.smallscreen=false;
		}
	});
