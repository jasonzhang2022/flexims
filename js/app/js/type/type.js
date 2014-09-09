
flexdms.typemodules.push("typeER");

//Script jsvascript for type pages
var typeApp=angular.module("type", flexdms.typemodules);
//UI-Router configuration
typeApp.config(function($stateProvider, $urlRouterProvider) {
	  //
	  $urlRouterProvider.otherwise("/types");
	  //
	  // Now set up the states
	  $stateProvider
	  .state('modifyresource', {
	      url: "/modifyresource/{url:.*}",
	      templateUrl: "type/modify_resource.html",
	      controller: "modifyRecourceCtrl"
	    })
	    .state('addtype', {
	      url: "/addtype",
	      resolve: {
	    	  types: function(Type){
	    		  return flexdms.types.$promise;
	    	  } 
	      },
	      templateUrl: "type/addtype.html",
	      controller:"addtypeCtrl"
	    })
	    .state('edittype', {
	      url: "/edittype/:typename",
	      resolve: {
	    	  types: function(Type){
	    		  return flexdms.types.$promise;
	    	  } 
	      },
	      templateUrl: "type/addtype.html",
	      controller:"edittypeCtrl"
	    })
	    .state('viewtype', {
	      url: "/viewtype/:typename",
	      resolve: {
	    	  types: function(Type){
	    		  return flexdms.types.$promise;
	    	  } 
	      },
	      templateUrl: "type/viewtype.html",
	      controller:"viewtypeCtrl"
	    })
	     .state('types', {
	      url: "/types",
	      resolve: {
	    	  types: function(Type){
	    		  return flexdms.types.$promise;
	    	  } 
	      },
	      templateUrl: "type/types.html",
	      controller:"typesCtrl"
	    })
	    .state('type', {
	    	abstract: true, 
	    	 resolve: {
		    	  types: function(Type){
		    		  return flexdms.types.$promise;
		    	  } 
		      },
	    	url: '/type/:typename',
	    	template: '<ui-view/>', //inline template
	         controller: 'typeCtrl'
		  }) 
	   
	    .state('type.addprop', {
		      url: "/addprop",
		      templateUrl: "type/addprop.html",
		      controller:"addpropCtrl"
		})
		
		 .state('type.editprop', {
		      url: "/editprop/:propname",
		      templateUrl: "type/addprop.html",
		      controller:"editpropCtrl"
		})
		.state('type.viewprop', {
		      url: "/viewprop/:propname",
		      templateUrl: "type/addprop.html",
		      controller:"viewpropCtrl"
		});
	    
	    
});

