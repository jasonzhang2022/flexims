flexdms.authserviceurl=appctx.modelerprefix+"/authrs/auth";
angular.module("flexdms.auth",["flexdms.TypeResource", "flexdms.InstResource", 'ngCookies', 'ui.bootstrap']).service("fxAuthService", function($rootScope, $modal, authService, $cookieStore, $http){
	
	var fxAuthService=this;
	
	
	function dialogCtrl($scope, fxAuthService, $timeout, authService, $modal, mode, $http, fxAlert){
		$scope.rememberMe=true;
		$scope.mode=mode;
		if ($scope.mode=='login'){
			$scope.email=localStorage.getItem("fxuser");
		}
		
		$scope.changeMode=function(newmode){
			$scope.mode=newmode;
			$scope.password='';
			$scope.password1='';
			$scope.username='';
			$scope.email='';
			$scope.errorMsg='';
			if ($scope.mode=='login'){
				$scope.email=localStorage.getItem("fxuser");
			}
		};
		
		$scope.showErrorMsg=function(msg){
			$scope.errorMsg=msg;
			$timeout(function(){
				$scope.errorMsg="";
			}, 5000);
		};
		
		$scope.login=function($event){
			$event.preventDefault();
			var scope=this;
			
			$http.post(flexdms.authserviceurl+"/auth", {
				'LoginInfo': {
					'email': this.email,
					'password': this.password,
					'rememberMe': this.rememberMe
				}
			}, {
				fxSkipError: true,
				fxAlertError: false
			}).then(function(xhr){
					var rsMsg=xhr.data.appMsg;
					if (rsMsg.statuscode==0){
						//authenticated
						fxAuthService.modalInstance.close();
						fxAuthService.modalInstance=null;
						authService.loginConfirmed();
						$http.get(flexdms.authserviceurl+"/currentUser").then(function(xhr){
							flexdms.fxuser=xhr.data.FxUser;
						});
						if (scope.rememberMe){
							localStorage.setItem("fxuser", scope.email);
						} else {
							localStorage.removeItem("fxuser");
						}
					} else {
						scope.showErrorMsg(rsMsg.msg);
					}

					return rsMsg;
			}, function(){
				scope.showErrorMsg(fxAlert.getErrorObject().msg);
			});
		};
		$scope.register=function($event){
			$event.preventDefault();
			var scope=this;
			
			if (this.password!==this.password1){
				scope.showErrorMsg("Two passwords do not match!");
				return;
			}
			
			$http.post(flexdms.authserviceurl+"/register", {
				'LoginInfo': {
					'username':this.username,
					'email': this.email,
					'password': this.password
				}
			},{
				fxSkipError: true,
				fxAlertError: false
			}).then(function(xhr){
					var rsMsg=xhr.data.appMsg;
					if (rsMsg.statuscode==0){
						//registered
						scope.changeMode("registered");
					} else {
						scope.showErrorMsg(rsMsg.msg);
					}
					return rsMsg;
			},function(){
				scope.showErrorMsg(fxAlert.getErrorObject().msg);
			});
		};
	}
	

	this.showDialog=function(mode){
		if (this.modalInstance!=null){
			this.modalInstance.close();
			this.modalInstance=null;
		}
		this.modalInstance=$modal.open({
		      templateUrl: 'template/login/login_register.html',
		      controller: dialogCtrl,
		      resolve :{
		    	  'mode': function(){
		    		  return mode;
		    	  }
		      }
		    });
	}
	
	
	//show login popup
	this.showLogin=function(){
		fxAuthService.showDialog("login");
	};
	
	//show register popup
	this.showRegister=function(){
		fxAuthService.showDialog("register");
	};
	
	this.showChangePassword=function(){
		fxAuthService.showDialog("changepassword");
	};
	
}).config(function($provide){
	
	//logout.
	$provide.decorator("securityInfoService", function($delegate, $http, $window, $cookieStore){
		$delegate.logout=function(){
			
			$delegate.setFxUser(null);
			
			$http.get(flexdms.authserviceurl+"/logout",
					{
						fxSkipError: true,
						fxAlertError: false
					}
			).then(function(){
				$cookieStore.remove("JSESSIONID");
				$window.location.href=flexdms.config.logout;
			}, function(){
				//do nothing since we logged out.
				$cookieStore.remove("JSESSIONID");
				$window.location.href=flexdms.config.logout;
			});
		};
		return $delegate;
	});
}).run(function(authService, fxAuthService, $http, securityInfoService, $rootScope){
	//trigger login
	$rootScope.$on("event:auth-loginRequired", fxAuthService.showLogin);
	$http.get(flexdms.authserviceurl+"/currentUser").then(function(xhr){
		securityInfoService.setFxUser(xhr.data.FxUser);
		authService.loginConfirmed();
	});
});

flexdms.typemodules.push("flexdms.auth");
