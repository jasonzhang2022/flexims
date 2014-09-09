describe("util_service.js", function() {
	
	
	var $rootScope = null;
	var $http = null;
	var $myInjector = null;
//	
	beforeEach(module("flexdms.util"));
	beforeEach(inject(function($injector) {
		$http = $injector.get("$httpBackend");
		$rootScope = $injector.get("$rootScope");
		$controller = $injector.get('$controller');
		$myInjector = $injector;
		$http.whenGET(appctx.modelerprefix+"/rs/util/getenum/com.flexdms.flexims.query.OrderDirection").respond(ods);
		$http.whenGET(appctx.modelerprefix+"/rs/util/getenum/com.flexdms.flexims.util.TimeUnit").respond(timeunits);
		$http.whenGET(appctx.modelerprefix+"/rs/util/getenum/com.flexdms.flexims.query.Operato").respond(ops);
	}));
	
	afterEach(function() {
		$http.verifyNoOutstandingRequest();
		$http.verifyNoOutstandingExpectation();
	});
	
	/*
	 // Can not test this, cache is done at http side. we still need flush
	  it("Util Service:getEnum", function() {
		
		var Util=$myInjector.get("Util");
		var ords=Util.getEnum({'classname':"com.flexdms.flexims.query.OrderDirection"});
		expect(ords.$resolved).toBeFalsy();
		$http.flush();
		expect(ords.$resolved).toBeTruthy();
		
		ords=Util.getEnum({'classname':"com.flexdms.flexims.query.OrderDirection"});
		//should be cached.
		expect(ords.$resolved).toBeTruthy();
	});*/
	it("Util Service:searchForData", function() {
		
		var scope1=$rootScope.$new();
		scope1.test="test1";
		
		//current scope is not searched
		var scope2=scope1.$new();
		scope2.test="test2";
		expect(flexdms.searchForData(scope2, "test")).toEqual("test1");
		
		
		//function is not searched
		scope1=$rootScope.$new();
		scope1.test=function(){
			///test
		};
		
		scope2=scope1.$new();
		expect(flexdms.searchForData(scope2, "test")).toBeNull();
		
		
	});
	
	it("Util Service:flatobject", function() {
		
		var obj={
				prop1:{
					'name':'value1',
				},
				'array1':[1,2,3],
				'simple': 's',
		};
		
		var results={};
		flexdms.flatObject(obj, results);
		expect(results["prop1.name"]).toEqual("value1");
		expect(results["array1.0"]).toEqual(1);
		expect(results["simple"]).toEqual("s");
		
		var unflat=flexdms.unflatObject(results);
		expect(angular.isObject(unflat.prop1)).toBeTruthy();
		expect(unflat.prop1.name).toEqual("value1");
		expect(angular.isArray(unflat.array1)).toBeTruthy();
		expect(angular.isNumber(unflat.array1[0])).toBeTruthy();
		expect(unflat.array1[0]).toBe(1);
		expect(unflat.simple).toEqual("s");
	});
	
	
});
