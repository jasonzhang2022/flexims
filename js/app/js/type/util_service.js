
angular.module("flexdms.util",["ngResource"]).factory("Util", function($resource) {
	
	
	var Util = $resource(flexdms.utilserviceurl, null, {
		'getEnumBackend' : {
			method : 'GET',
			url : flexdms.utilserviceurl + '/getenum/:classname',
			 transformResponse: function(data, headers) {
		    	return angular.fromJson(data).nameValueList.entry;
			 },
			isArray: true,
			cache: true
		},
	});
	

	Util.getEnum=function(params){
		return Util.getEnumBackend(params);
	};
	return Util;
});

flexdms.typemodules.push("flexdms.util");
//-------------------------------global utility function
flexdms.searchForData=function($scope, prop){
	//do not search current.
	var current=$scope.$parent;
	while(current) {
		if (angular.isDefined(current[prop]) && !angular.isFunction(current[prop])){
			return current[prop];
		}
		if (angular.isDefined(current.$parent)){
			current=current.$parent;
		}
	}
	return null;
	
};

flexdms.flatObject=function(obj, results){
	var prefix=arguments[2];
	if (!angular.isDefined(prefix)){
		prefix='';
	}
	
	for (var prop in obj){
		if (prop[0]=='$'){
			continue;
		}
		if (angular.isFunction(obj[prop])){
			continue;
		}
		if (angular.isObject(obj[prop])){
			flexdms.flatObject(obj[prop], results, prefix?prefix+"."+prop:prop);
			continue;
		}
		results[prefix?prefix+"."+prop:prop]= obj[prop];
	}
};

flexdms.unflatObject=function(inputobj){
	function nestObject(name, value){
		var valueobject=value;
		var ns=name.split(".");
		for (var i=ns.length-1;i>=0; i--){
			var v={};
			v[ns[i]]=valueobject;
			valueobject=v;
		}
		return valueobject;
	}
	
	var num=new RegExp(/^\d+$/);
	
	//only expect one prop in nestedObject
	function mergeNestedToFinal(src, base){
		for(name in src){
			var value=src[name];
			if (angular.isDefined(base[name])){
				mergeNestedToFinal(value, base[name]);
			} else{
				base[name]=value;
			}
		}
	}
	function arrayLize(obj){
		if (!angular.isObject(obj)){
			return obj;
		}
		
		var isnum=false;
		
		//test should we create an array
		for (var prop in obj){
			//we only need to judge one property;
			if (num.test(prop)){
				isnum=true;
			}
			break;
		}
		
		if(isnum){
			var newobj=new Array();
			for (var prop in obj){
				newobj[prop-0]=arrayLize(obj[prop]);
			}
			return newobj;
		}  
		for (var prop in obj){
			obj[prop]=arrayLize(obj[prop]);
		}
		return obj;
	}
	var ret={};
	for(var prop in inputobj){
		//return an object {prop: $location.search)[prop]}
		var nestedObj=nestObject(prop, inputobj[prop]);
		mergeNestedToFinal(nestedObj, ret);
	}
	
	return arrayLize(ret);
};