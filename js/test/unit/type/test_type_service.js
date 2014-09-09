describe("type_service.js", function() {

	var $rootScope = null;
	var $http = null;
	var $controller = null;
	var $myInjector = null;
	beforeEach(function(){
		flexdms.typemeta=meta;
	});
	beforeEach(module("type"));

	beforeEach(inject(function($injector) {
		$http = $injector.get("$httpBackend");
		$rootScope = $injector.get("$rootScope");
		$controller = $injector.get('$controller');
		$myInjector = $injector;

		$http.whenGET(appctx.modelerprefix+"/rs/type/meta").respond(meta);
		//we do not test html
		$http.whenGET("type/addtype.html").respond("<html></html>");
	}));

	afterEach(function() {
		$http.verifyNoOutstandingRequest();
		$http.verifyNoOutstandingExpectation();
	});
	
	
	
	it("Type Resource/Service", function() {
		var types=flexdms.types;
		expect(types.length>5).toBeTruthy();
		var type=flexdms.findType("Test");
		expect(type.getName()).toEqual('Test');
		expect(type.isEntity()).toBeTruthy();
		expect(type.getPropNames().length).toBe(3);
		expect(type.getEditProps().length).toBe(1);
		expect(angular.isArray(type.getProps())).toBeTruthy();
		
		type=flexdms.findType("Embed1");
		expect(type.getName()).toEqual('Embed1');
		expect(type.isEntity()).toBeFalsy();
		
		//the inverse prop is not in the editted list
		type=flexdms.findType("Mdoombuild");
		expect(type.getEditProps().length).toBe(1);
		
		


	});
	
	it("Type Resource/Service:inheritance", function() {
		var type=flexdms.findType("DefaultTypedQuery");
		expect(type.getName()).toEqual('DefaultTypedQuery');
		
		//test the property in parent type
		var propobj=type.getProp("TargetedType");
		expect(propobj).toBeDefined();
		//type should be parent type;
		expect(propobj.getBelongingType().getName()).toEqual("TypedQuery");
		
		
		//test prop in child type
		propobj=type.getProp("Conditions");
		expect(propobj).toBeDefined();
		//type should be parent type;
		expect(propobj.getBelongingType().getName()).toEqual("DefaultTypedQuery");
		
		var props=type.getSelfProps();
		angular.forEach(props, function(propobj1){
			expect(propobj1.getBelongingType().getName()).toEqual("DefaultTypedQuery");
		});
		
		var selfSize=props.length;
		var allSize=type.getProps().length;
		expect(allSize>selfSize).toBeTruthy();
		
		props=type.getEditProps();
		expect(props.length>selfSize).toBeTruthy();
		
		//initialize instance works for parent
		var inst={};
		type.initInstance(inst, false);
		expect(inst["TargetedType"]).toBeNull();

	});
	it("Type Resource/Service:child-parent", function() {
		var type=flexdms.findType("DefaultTypedQuery");
		var parentype=flexdms.findType("TypedQuery");
		expect(parentype.isChildSelf(type)).toBeTruthy();
		expect(parentype.getChildren().length).toBe(2);
		

	});
	it("Type Resource/Service:is AddAble", function() {
		angular.forEach(flexdms.types, function(type){
			type.isEntity() ;
		});

	});
	
	it("Type Resource/Service:initialize instance for boolean", function() {
		var type=flexdms.findType("Basictype");
		
		//initialize instance works for parent
		var inst={};
		type.initInstance(inst, false);
		expect(inst["propboolean"]).toBeFalsy();

	});
	
});