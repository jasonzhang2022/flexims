var instApp=angular.module("instApp", flexdms.instAppModules);
instApp.controller("topController", function($rootScope, $scope, $http, $window, $location){
	$scope.showContent=false;
	$rootScope.$on("event:auth-loginRequired", function(){
		$scope.showContent=false;
	});
	$rootScope.$on("event:auth-loginConfirmed", function(){
		$scope.showContent=true;
	});
});
instApp.config(function($stateProvider, $urlRouterProvider,   $controllerProvider, $compileProvider, $filterProvider, $provide, datepickerPopupConfig) {
	
	
	//why remember all the providers
	//http://ify.io/lazy-loading-in-angularjs: for lazy loading
	instApp.$stateProvider=$stateProvider;
	instApp.$urlRouterProvide=$urlRouterProvider;
	instApp.$controllerProvider = $controllerProvider;
    instApp.$compileProvider    = $compileProvider;
    instApp.$filterProvider     = $filterProvider;
    instApp.$provide            = $provide;
    
	datepickerPopupConfig.dateFormat=flexdms.config.dateFormat;  
	  //
	  // For any unmatched url, redirect to /state1
	$urlRouterProvider.otherwise("/home");
	  //
	  // Now set up the states
	 $stateProvider.state('viewinst', {
		url : "/viewinst/:typename/:id",
		template :function(stateParams){
			return "<fx-inst-viewer-form fx-typename='"+stateParams.typename+"' fx-inst-id='"+stateParams.id+"'/>";
		}
	}).state('addinst', {
		url : "/addinst/:typename",
		template :function(stateParams){
			return "<fx-inst-editor-form fx-typename='"+stateParams.typename+"'/>";
		}
	})
	.state('editinst', {
		url : "/editinst/:typename/:id",
		template :function(stateParams){
			return "<fx-inst-editor-form fx-typename='"+stateParams.typename+"' fx-inst-id='"+stateParams.id+"'/>";
		},
		
	}).state('deleteinst', {
		url : "/deleteinst/:typename/:id",
		template :function(stateParams){
			return "<fx-inst-delete-form fx-typename='"+stateParams.typename+"' fx-inst-id='"+stateParams.id+"'/>";
		}
	}).state('admin', {
		url : "/admin",
		templateUrl: 'rs/res/web/customtemplates/adminmenu.html'
	}).state('home', {
		url : "/home",
		templateUrl: 'rs/res/web/customtemplates/home.html'
	});
	 
	
	 
	    
}).run(function($state, $rootScope){
	$rootScope.viewInst=function(typename, id){
		$state.go("viewinst", {typename:typename, id:id});
	};
	$rootScope.deleteInst=function(typename, id){
		$state.go("deleteinst", {typename:typename, id:id});
	};
	
	$rootScope.editInst=function(typename, id){
		$state.go("editinst", {typename:typename, id:id});
	};
	$rootScope.addInst=function(typename){
		$state.go("addinst", {typename:typename});
	};
	
	
});

