describe("common.js", function() {


	var $rootScope = null;
	var $http = null;
	var $myInjector = null;

	beforeEach(module("flexdms.common"));
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
	
	it("common:fxIDynamicName", function() {
		var scope=$rootScope.$new();
		scope.nums=[{value:1},{value:2}];
		var template="<div><ul><li ng-repeat='n in nums'><input type='text' name='num' fx-dynamic-name='\"num\"+$index' ></li></ul></div>";
		
		var $compile = $myInjector.get("$compile");
		var element=$compile(template)(scope);
		scope.$digest();
		console.log(element[0].outerHTML);
		var inputs=element.find("input");
		expect(inputs.length).toBe(2);
		expect(inputs[0].name).toEqual("num0");
		expect(inputs[1].name).toEqual("num1");
	});
	
	
	
	
});
