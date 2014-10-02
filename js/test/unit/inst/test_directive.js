describe(
		"inst_directive.js",
		function() {
			var $rootScope = null;
			var $http = null;
			var $controller = null;
			var $myInjector = null;
			var $compile = null;

			beforeEach(function(){
				flexdms.typemeta=meta;
			});
			// Load the myApp module, which contains the directive
			beforeEach(module('instApp'));

			// Store references to $rootScope and $compile
			// so they are available to all tests in this describe block
			beforeEach(inject(function($injector) {

				$http = $injector.get("$httpBackend");
				$rootScope = $injector.get("$rootScope");
				$controller = $injector.get('$controller');
				$myInjector = $injector;
				$compile = $injector.get("$compile");

				$http.whenGET("rs/res/web/customtemplates/home.html").respond("");
				$http.whenGET(appctx.modelerprefix+"/rs/type/meta").respond(meta);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Basictype/10060").respond(
						inst, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Basictype/10060?refresh=true").respond(
						inst, jsonHeaders);
				//http://localhost:8080/modeler/rs/inst/get/Embedmain/10150
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Embedmain/10060").respond(
						einst, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Embedmain/10060?refresh=true").respond(
						einst, jsonHeaders);
			}));

			afterEach(function() {
				$http.verifyNoOutstandingRequest();
				$http.verifyNoOutstandingExpectation();
			});
			function setupPropEditorScope(typename, propname){
				$scope=$rootScope.$new();
				var type=flexdms.findType(typename);
				var Inst=$myInjector.get("Inst");
				var inst=Inst.newInst(typename);
				$scope.inst=inst;
				$scope.type=type;
				$scope.typenmae=typename;
				if (propname){		
					//required for prop validator and prop editor
					$scope.propobj=type.getProp(propname);
				}
				
				return $scope;
			}

			it('Test Inst Resource: wrap for Embedded', function() {
				var typename = "Embedmain";
				var id = 10060;
				var Inst = $myInjector.get("Inst");
				var inst = Inst.get({
					typename : typename,
					id : id
				});
				$http.flush();

				// type name is added by unwrap
				expect(inst.multiembed[0][flexdms.insttype]).toEqual("Embed2");
				expect(inst.multiembed[0].$promise).toBeDefined();
				expect(inst.multiembed[0].$resolved).toBeTruthy();
				expect(inst.singleembed[flexdms.insttype]).toEqual("Embed1");
				expect(inst.singleembed.$promise).toBeDefined();
				expect(inst.singleembed.$resolved).toBeTruthy();
			});
			it('Test Inst Resource: Wrap for Simple', function() {
				var typename = "Basictype";
				var id = 10060;
				var Inst = $myInjector.get("Inst");
				var inst = Inst.get({
					typename : typename,
					id : id
				});
				$http.flush();

				// property is moved to upper level
				expect(inst.shortstring).toEqual("jason");
				// type name is added by unwrap
				expect(inst[flexdms.insttype]).toEqual(typename);
				


			});
			it('Test InstCache: inst is cached', function() {
				var typename = "Basictype";
				var id = 10060;
				var instCache = $myInjector.get("instCache");
				var inst = instCache.getInst(typename, id);
				expect(inst.$resolved).toBeFalsy();
				$http.flush();
				expect(inst.$resolved).toBeTruthy();
				expect(inst.id).toEqual(10060);
				expect(inst.shortstring).toEqual("jason");

				inst.shortstring = "jason1";
				instCache.updateInst(typename, inst);
				inst = instCache.getInst(typename, id);
				expect(inst.$resolved).toBeTruthy();
				
				//make sure this can be called
				instCache.refreshInst(typename, id);
				$http.flush();

				instCache.deleteInst(typename, id);
				expect(instCache.insts[typename + ":" + id]).not.toBeDefined();

			});

			it("Test query parameter parsing", function(){
				var test={'a':5, 'b':6, 'c.0.a':1,'c.1.b':2, 'c.2.d':3, 'd.e':'e', 'd.f':'f'};
				var result=flexdms.unflatObject(test);
				expect(angular.isArray(result.c)).toBeTruthy();
				expect(result.c.length).toBe(3);
				expect(angular.isObject(result.c[0]));
				expect(result.c[0].a).toBe(1);
				
				expect(angular.isDefined(result.a.e));
				expect(angular.isDefined(result.a.f));
				
			});
			it('Test fxTemplates:edit template is fetched correctly',function() {
				var templateCache = $myInjector.get("$templateCache");
				
				var fxTemplates = $myInjector.get("fxTemplates");
				
				var shortstring = flexdms.findType("Basictype")
				.getProp("shortstring");
				
				
				//------------------inst:
				expect(fxTemplates.getInstUrl("Basictype"))
				.toEqual("template/props/edit/inst.html");
				
				//type -specific inst url
				templateCache
				.put(
						"template/props/edit/basictype/inst.html",
				"x");
				expect(fxTemplates.getInstUrl("Basictype"))
				.toEqual("template/props/edit/basictype/inst.html");
				
				// ------------------input.
				// default template
				expect(fxTemplates.getSimpleInputTemplate(shortstring))
				.toEqual(
						templateCache
						.get("template/props/edit/default/shortstring.html"));
				
				// property type under BasicType
				templateCache
				.put(
						"template/props/edit/basictype/shortstring.html",
				"x");
				expect(fxTemplates.getSimpleInputTemplate(shortstring))
				.toEqual("x");
				
				// property sepcific
				templateCache
				.put(
						"template/props/edit/basictype/shortstring/shortstring.html",
				"y");
				expect(fxTemplates.getSimpleInputTemplate(shortstring))
				.toEqual("y");
				
				// ---------simple_select
				shortstring = flexdms.findType("Sallowes").getProp(
				"shortstring");
				// ------------------input.
				// default template
				expect(fxTemplates.getSimpleSelect(shortstring))
				.toEqual(
						templateCache
						.get("template/props/edit/default/simple_select.html"));
				
				// property type under BasicType
				templateCache
				.put(
						"template/props/edit/sallowes/simple_select.html",
				"x1");
				expect(fxTemplates.getSimpleSelect(shortstring))
				.toEqual("x1");
				
				// property sepcific
				templateCache
				.put(
						"template/props/edit/sallowes/shortstring/simple_select.html",
				"y1");
				expect(fxTemplates.getSimpleSelect(shortstring))
				.toEqual("y1");
				
				// ---------------------error
				// default template
				expect(fxTemplates.getErrorTemplate(shortstring))
				.toEqual(
						templateCache
						.get("template/props/edit/default/shortstring_error.html"));
				
				// property type under BasicType
				templateCache
				.put(
						"template/props/edit/sallowes/shortstring_error.html",
				"x2");
				expect(fxTemplates.getErrorTemplate(shortstring))
				.toEqual("x2");
				
				// property sepcific
				templateCache
				.put(
						"template/props/edit/sallowes/shortstring/shortstring_error.html",
				"y2");
				expect(fxTemplates.getErrorTemplate(shortstring))
				.toEqual("y2");
				
				//Custom object
				var propobj = flexdms.findType("Mspecial").getProp("propobject");
				expect(fxTemplates.getSimpleInputTemplate(propobj)).toEqual(templateCache.get("template/props/edit/namevaluelist.html"));
				
				// useSingleFieldForMultiple
				var type = flexdms.findType("Collection1");
				//
				expect(
						fxTemplates.useSingleFieldForMultiple(type
								.getProp("propint"))).toBeTruthy();
				expect(
						fxTemplates.useSingleFieldForMultiple(type
								.getProp("propemail"))).toBeTruthy();
				expect(
						fxTemplates.useSingleFieldForMultiple(type
								.getProp("propurl"))).toBeTruthy();
				expect(
						fxTemplates.useSingleFieldForMultiple(type
								.getProp("shortstring"))).toBeFalsy();
				expect(
						fxTemplates.useSingleFieldForMultiple(type
								.getProp("propdate"))).toBeFalsy();
				
				
				//basic relation
				type = flexdms.findType("Mdoomroom");
				//
				expect(fxTemplates.useSingleFieldForMultiple(type.getProp("doombuild"))).toBeTruthy();
				expect(fxTemplates.getSimpleInputTemplate(type.getProp("doombuild"))).toEqual(templateCache.get("template/props/edit/default/simplerelation.html"));
				
				
				
			});

			it('Test fxViewTemplates:View Template is fetched correctly.',function() {
				var templateCache = $myInjector.get("$templateCache");

				var fxTemplates = $myInjector.get("fxViewTemplates");

				var shortstring = flexdms.findType("Basictype")
				.getProp("shortstring");
				
				//------------------inst:
				expect(fxTemplates.getInstUrl("Basictype"))
				.toEqual("template/props/view/inst.html");
				
				//type -specific inst url
				templateCache
				.put(
						"template/props/view/basictype/inst.html",
				"x");
				expect(fxTemplates.getInstUrl("Basictype"))
				.toEqual("template/props/view/basictype/inst.html");
				
				// ------------------input.
				// default template
				expect(fxTemplates.getSimpleViewTemplate(shortstring))
				.toEqual(
						templateCache
						.get("template/props/view/default/shortstring.html"));

				// property type under BasicType
				templateCache
				.put(
						"template/props/view/basictype/shortstring.html",
				"x");
				expect(fxTemplates.getSimpleViewTemplate(shortstring))
				.toEqual("x");

				// property sepcific
				templateCache
				.put(
						"template/props/view/basictype/shortstring/shortstring.html",
				"y");
				expect(fxTemplates.getSimpleViewTemplate(shortstring))
				.toEqual("y");

				// multiple layout
				// default template
				expect(fxTemplates.getMultipleLayout(shortstring))
				.toEqual(
						templateCache
						.get("template/props/view/default/multiple_layout.html"));

				// property type under BasicType
				templateCache
				.put(
						"template/props/view/basictype/multiple_layout.html",
				"x");
				expect(fxTemplates.getMultipleLayout(shortstring))
				.toEqual("x");

				// property sepcific
				templateCache.put("template/props/view/basictype/shortstring/multiple_layout.html","y");
				expect(fxTemplates.getMultipleLayout(shortstring))
				.toEqual("y");
				
				//Custom object
				var propobj = flexdms.findType("Mspecial").getProp("propobject");
				expect(fxTemplates.getSimpleViewTemplate(propobj)).toEqual(templateCache.get("template/props/view/namevaluelist.html"));
				

				//basic relation
				type = flexdms.findType("Mdoomroom");
				//
				expect(fxTemplates.shouldShowMultipleValueAsSimple(type.getProp("doombuild"))).toBeTruthy();
				expect(fxTemplates.getSimpleViewTemplate(type.getProp("doombuild"))).toEqual(templateCache.get("template/props/view/default/simplerelation.html"));
				


			});

			
			it('prop auxillary fucntions:tooltip, css classes, etc', function() {
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/props/edit/addinst.html");
				var typename="Basictype";
			
				var $scope=$rootScope.$new();
				var Inst=$myInjector.get("Inst");
				var inst=Inst.newInst(typename);
				$scope.inst=inst;
				$scope.typename=typename;
				
				var $compile=$myInjector.get("$compile");
				var element=$compile(template)($scope);
				$scope.$digest();

				var propobj=flexdms.findType(typename).getProp("shortstring");
				$scope=element.find("form[name=instform] > div").scope();
				 // fire all the watches.
			
			
				expect(angular.isFunction($scope.showInvalid)).toBeTruthy();
				
				expect(angular.isFunction($scope.propclasses)).toBeTruthy();
				expect($scope.propclasses(propobj).length).toBe(4);
				expect($scope.propclasses(propobj)[0]).toEqual("form-group");
				expect($scope.propclasses(propobj)[1]).toEqual("prop");
				expect($scope.propclasses(propobj)[2]).toEqual("shortstring");
				expect($scope.propclasses(propobj)[3]).toEqual("simpleprop");
				
				
				var field=element.find("form[name=instform]  .shortstring").scope().field;
				expect(angular.isFunction($scope.propLabelClasses)).toBeTruthy();
				expect($scope.propLabelClasses(field).length).toBe(2);
				expect($scope.propLabelClasses(field)[0]).toEqual("proplabel");
				expect($scope.propLabelClasses(field)[1]).toEqual("has-error");
				
				expect(angular.isFunction($scope.propTooltip)).toBeTruthy();
				expect($scope.propTooltip(propobj)).toContain("Long description");
			});
			
			
			
			it('Test Property Editor: form controls are inserted correctly', function() {
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/prop_edit_decorator.html");
				var $compile=$myInjector.get("$compile");
				var element=null;
				
				//basic an input field
				$scope=setupPropEditorScope("Basictype", "shortstring");
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("div.propinput > input").length).toBe(1);
				expect(element.find("div.propinput > span").length).toBe(1);
				expect(element.find("div.prophelp > span").length).toBe(4);
				
				//-------------------allowed: select
				$scope=setupPropEditorScope("Sallowes", "shortstring");
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				 // fire all the watches.
				$scope.$digest();
				expect(element.find("div.propinput > select").length).toBe(1);
				expect(element.find("div.propinput > select > option").length).toBe(4);
				expect(element.find("div.prophelp > span").length).toBe(1);
				
				//multiple selection: select with multiple
				$scope=setupPropEditorScope("Mallowes", "shortstring");
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect(element.find("div.propinput > select[multiple]").length).toBe(1);
				expect(element.find("div.propinput > select[multiple] > option").length).toBe(3);
				expect(element.find("div.prophelp > span").length).toBe(1);
				
				
				//multiple fields for string property
				$scope=setupPropEditorScope("Mdefault", "shortstring");
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect(element.find("div.propinput input").length).toBe(2);
				expect(element.find("div.prophelp").length).toBe(2);
				
				//Single field for integer
				$scope=setupPropEditorScope("Mdefault", "propint");
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect(element.find("div.propinput input").length).toBe(1);
				expect(element.find("div.prophelp").length).toBe(1);
				
				//relation
				$scope=setupPropEditorScope("Mdoomroom", "doombuild");
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect(element.find("div.propinput input").length).toBe(1);
				expect(element.find("div.prophelp").length).toBe(1);
			});
			
			it('Test Property Validator--single value, single field: Validation is performed', function() {
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/prop_edit_decorator.html");
				var $compile=$myInjector.get("$compile");
				var element=null;
				
				//basic an input field
				$scope=setupPropEditorScope("Basictype", "shortstring");
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				
				//not value, but required, validate the model valeu
				expect($scope.field.$error.required).toBeTruthy();
				
				var input=element.find("div.propinput > input");
				var help=element.find("div.prophelp");
				
				expect($scope.field.$dirty).toBeFalsy();
				//-----------minlength
				$scope.field.$setViewValue("test");
				$scope.$digest(); 
				expect($scope.field.$dirty).toBeTruthy();
				expect($scope.field.$invalid).toBeTruthy();
				expect($scope.testform.$invalid).toBeTruthy();
				expect($scope.field.$error.minlength).toBeTruthy();
				expect(help.find("span:nth-child(1)").hasClass("ng-hide")).toBeTruthy();
				expect(help.find("span:nth-child(2)").hasClass("ng-hide")).toBeFalsy();
				expect(help.find("span:nth-child(3)").hasClass("ng-hide")).toBeTruthy();
				expect(help.find("span:nth-child(4)").hasClass("ng-hide")).toBeTruthy();
				
				//----------required
				$scope.field.$setViewValue("");
				$scope.$digest(); 
				expect($scope.field.$invalid).toBeTruthy();
				expect($scope.testform.$invalid).toBeTruthy();
				expect($scope.field.$error.minlength).toBeFalsy();
				expect($scope.field.$error.required).toBeTruthy();
				expect(help.find("span:nth-child(1)").hasClass("ng-hide")).toBeFalsy();
				expect(help.find("span:nth-child(2)").hasClass("ng-hide")).toBeTruthy();
				expect(help.find("span:nth-child(3)").hasClass("ng-hide")).toBeTruthy();
				expect(help.find("span:nth-child(4)").hasClass("ng-hide")).toBeTruthy();
				
				//----------maxlength
				$scope.field.$setViewValue("artgtdtgdttdgdtdtgdttdgdtdt");
				$scope.$digest(); 
				expect($scope.field.$invalid).toBeTruthy();
				expect($scope.testform.$invalid).toBeTruthy();
				expect($scope.field.$error.maxlength).toBeTruthy();
				expect($scope.field.$error.required).toBeFalsy();
				expect(help.find("span:nth-child(1)").hasClass("ng-hide")).toBeTruthy();
				expect(help.find("span:nth-child(2)").hasClass("ng-hide")).toBeTruthy();
				expect(help.find("span:nth-child(3)").hasClass("ng-hide")).toBeFalsy();
				expect(help.find("span:nth-child(4)").hasClass("ng-hide")).toBeTruthy();
				
				
				//good value. Also test pattern is case-insensitive
				$scope.field.$setViewValue("GOODVAL");
				$scope.$digest(); 
				expect($scope.field.$valid).toBeTruthy();
				expect($scope.testform.$valid).toBeTruthy();
				expect(help.find("span:nth-child(1)").hasClass("ng-hide")).toBeTruthy();
				expect(help.find("span:nth-child(2)").hasClass("ng-hide")).toBeTruthy();
				expect(help.find("span:nth-child(3)").hasClass("ng-hide")).toBeTruthy();
				expect(help.find("span:nth-child(4)").hasClass("ng-hide")).toBeTruthy();
				expect($scope.inst.shortstring).toEqual("GOODVAL");
				
				
				//---------------integer
				$scope=setupPropEditorScope("Basictype", "propint");
				$scope.inst.propint=3;
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect($scope.field.$error.min).toBeTruthy(); //validate model value
				input=element.find("div.propinput > input");
				help=element.find("div.prophelp");
				
				expect($scope.field.$dirty).toBeFalsy();
				//integer
				$scope.field.$setViewValue("test");
				$scope.$digest(); 
				expect($scope.field.$error.integer).toBeTruthy();
				
				$scope.field.$setViewValue("3.5");
				$scope.$digest(); 
				expect($scope.field.$error.integer).toBeTruthy();
				
				$scope.field.$setViewValue("3");
				$scope.$digest(); 
				expect($scope.field.$error.min).toBeTruthy();
				
				$scope.field.$setViewValue("25");
				$scope.$digest(); 
				expect($scope.field.$error.max).toBeTruthy();
				
				//------------float
				$scope=setupPropEditorScope("Basictype", "propfloat");
				$scope.inst.propfloat="6.3";
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect($scope.field.$error.number).toBeTruthy(); //validate model value
				input=element.find("div.propinput > input");
				help=element.find("div.prophelp");
				
				expect($scope.field.$dirty).toBeFalsy();
				//integer
				$scope.field.$setViewValue("test");
				$scope.$digest(); 
				expect($scope.field.$error.number).toBeTruthy();
				
				
				//--------------------email
				$scope=setupPropEditorScope("Basictype", "propemail");
				$scope.inst.propemail="teste";
				
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect($scope.field.$error.email).toBeTruthy(); //validate model value
				input=element.find("div.propinput > input");
				help=element.find("div.prophelp");
				
				expect($scope.field.$dirty).toBeFalsy();
				//integer
				$scope.field.$setViewValue("test");
				$scope.$digest(); 
				expect($scope.field.$error.email).toBeTruthy();
				
				
				//----------------------URL
				$scope=setupPropEditorScope("Basictype", "propurl");
				$scope.inst.propurl="badurl"
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect($scope.field.$error.url).toBeTruthy(); //validate modle value
				input=element.find("div.propinput > input");
				help=element.find("div.prophelp");
				
				expect($scope.field.$dirty).toBeFalsy();
				//integer
				$scope.field.$setViewValue("test");
				$scope.$digest(); 
				expect($scope.field.$error.url).toBeTruthy();
				
				//---------------relation input: integer
				$scope=setupPropEditorScope("Mdoomroom", "doombuild");
				$scope.inst.doombuild="x";
				
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect($scope.field.$error.integer).toBeTruthy(); //validate model value
				input=element.find("div.propinput > input");
				help=element.find("div.prophelp");
				
				expect($scope.field.$dirty).toBeFalsy();
				//integer
				$scope.field.$setViewValue("test");
				$scope.$digest(); 
				expect($scope.field.$error.integer).toBeTruthy();
				
				$scope.field.$setViewValue("3.5");
				$scope.$digest(); 
				expect($scope.field.$error.integer).toBeTruthy();
				
				$scope.field.$setViewValue("3");
				$scope.$digest(); 
				expect($scope.field.$valid).toBeTruthy();
				
			});
			
			it('Test Property Validator--Multiple Value, Single Field: Validation is performed', function() {
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/prop_edit_decorator.html");
				var $compile=$myInjector.get("$compile");
				var element=null;
				
				//basic an input field
				$scope=setupPropEditorScope("Collection1", "propint");
				$scope.type.getProp("propint").setRequired(true);
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect($scope.field.$error.required).toBeTruthy(); //model validation
				
				
				$scope=setupPropEditorScope("Collection1", "propint");
				$scope.type.getProp("propint").setRequired(true);
				$scope.inst.propint=["5", "6"];
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect($scope.field.$error.integer).toBeTruthy(); //model validation
				
				var input=element.find("div.propinput > input");
				var help=element.find("div.prophelp");
				
				expect($scope.field.$dirty).toBeFalsy();
				//-----------minlength
				$scope.field.$setViewValue("test");
				$scope.$digest(); 
				expect($scope.field.$dirty).toBeTruthy();
				expect($scope.field.$invalid).toBeTruthy();
				expect($scope.testform.$invalid).toBeTruthy();
				expect($scope.field.$error.integer).toBeTruthy();
				
				$scope.field.$setViewValue("3");
				$scope.$digest(); 
				expect($scope.field.$error.min).toBeTruthy();
				
				$scope.field.$setViewValue("25");
				$scope.$digest(); 
				expect($scope.field.$error.max).toBeTruthy();
				
				$scope.field.$setViewValue("7,3");
				$scope.$digest(); 
				expect($scope.field.$error.min).toBeTruthy();
				
				$scope.field.$setViewValue("19,25");
				$scope.$digest(); 
				expect($scope.field.$error.max).toBeTruthy();
				
				$scope.field.$setViewValue("");
				$scope.$digest(); 
				expect($scope.field.$error.required).toBeTruthy();
				
				$scope.field.$setViewValue("6,7");
				$scope.$digest(); 
				expect($scope.inst.propint.length).toBe(2);
				expect($scope.inst.propint[0]).toBe(6);
				expect($scope.inst.propint[1]).toBe(7);
				
				$scope.field.$setViewValue("6,");
				$scope.$digest(); 
				expect($scope.inst.propint.length).toBe(1);
				expect($scope.inst.propint[0]).toBe(6);
				
				$scope.field.$setViewValue(",6,,");
				$scope.$digest(); 
				expect($scope.inst.propint.length).toBe(1);
				expect($scope.inst.propint[0]).toBe(6);
			
				
			});
			
			it('Test Property Validator--Relation Input: Validation is performed', function() {
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/prop_edit_decorator.html");
				var $compile=$myInjector.get("$compile");
				var element=null;
				
				//basic an input field
				$scope=setupPropEditorScope("Mstudent", "OneManys");
				$scope.inst.OneManys=["x", "y"];
				element=$compile(template)($scope);
				$scope=element.find("#editorcontainer").scope();
				$scope.$digest();
				expect($scope.field.$error.integer).toBeTruthy(); //validate model
				var input=element.find("div.propinput > input");
				var help=element.find("div.prophelp");
				
			
				//integer
				$scope.field.$setViewValue("test");
				$scope.$digest(); 
				expect($scope.field.$error.integer).toBeTruthy();
				
				$scope.field.$setViewValue("3.5");
				$scope.$digest(); 
				expect($scope.field.$error.integer).toBeTruthy();
				

				$scope.field.$setViewValue("7,x");
				$scope.$digest(); 
				expect($scope.field.$error.integer).toBeTruthy();
				
			
				
				$scope.type.getProp("OneManys").setRequired(true);
				$scope.field.$setViewValue("");
				$scope.$digest(); 
				expect($scope.field.$error.required).toBeTruthy();
				
				$scope.field.$setViewValue("6,7");
				$scope.$digest(); 
				expect($scope.inst.OneManys.length).toBe(2);
				expect($scope.inst.OneManys[0]).toBe(6);
				expect($scope.inst.OneManys[1]).toBe(7);
				
				$scope.field.$setViewValue("6,");
				$scope.$digest(); 
				expect($scope.inst.OneManys.length).toBe(1);
				expect($scope.inst.OneManys[0]).toBe(6);
				
				$scope.field.$setViewValue(",6,,");
				$scope.$digest(); 
				expect($scope.inst.OneManys.length).toBe(1);
				expect($scope.inst.OneManys[0]).toBe(6);
			
			});
			
			it('Test Property Validator--Multiple Value, Multiple Fields: Validation is performed', function() {
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/prop_edit_decorator.html");
				var $compile=$myInjector.get("$compile");
				var element=null;
				
				//basic an input field
				$scope=setupPropEditorScope("Collection1", "shortstring");
				$scope.inst.shortstring=["ab"];
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope.$digest();
				expect($scope.testform.shortstring.shortstring0.$error.minlength).toBeTruthy(); //validate model value
				
				$scope=element.find("#editorcontainer .multiple").scope();
				//add two empty item
				$scope.addItem(); 
				$scope.addItem();
				$scope.$digest();
				
				var subform=$scope.field;
				var field=$scope.field.shortstring0;
				var field1=$scope.field.shortstring1;
				

				//check individual field.
				expect(field.$dirty).toBeFalsy();
				//-----------minlength
				field.$setViewValue("at");
				$scope.$digest(); 
				expect(field.$dirty).toBeTruthy();
				expect(field.$invalid).toBeTruthy();
				expect(field1.$invalid).toBeFalsy();
				expect(subform.$invalid).toBeTruthy();
				expect(field.$error.minlength).toBeTruthy();
				
				//field does not interfere with each others
				field.$setViewValue("attgty");
				field1.$setViewValue("at");
				$scope.$digest(); 
				expect(field.$dirty).toBeTruthy();
				expect(field.$valid).toBeTruthy();
				expect(field1.$invalid).toBeTruthy();
				expect(subform.$invalid).toBeTruthy();
				expect(field1.$error.minlength).toBeTruthy();
				
				//required is enforced at subform level
				field.$setViewValue("at");
				field1.$setViewValue("at");
				$scope.$digest(); 
				expect(subform.$invalid).toBeTruthy();
				expect(subform.fakelist.$error.required).toBeTruthy();
				expect(subform.fakelist.$invalid).toBeTruthy();
				
				field.$setViewValue("jason");
				field1.$setViewValue("maria");
				$scope.$digest(); 
				expect(subform.$valid).toBeTruthy();
				expect(subform.fakelist.$error.required).toBeFalsy();
				expect(subform.fakelist.$valid).toBeTruthy();
				
				expect($scope.inst.shortstring[0]).toEqual("jason");
				expect($scope.inst.shortstring[1]).toEqual("maria");
				
				
				
			});

			it('Test fxSimpleMultiple--Wrap multiple single inputs field as a subform', function(){
				
				////No need to perform anything. The logic is already verified in validator 
				
			});
			it('Test fxPropInput--Give input field a name', function(){
				//No need to perform anything. The logic is already verified in validator 
			});
			it('Test InstEditor: show label', function(){
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/editor1.html");
				var $compile=$myInjector.get("$compile");
				var element=null;
				
				//basic : show label by default
				$scope=setupPropEditorScope("Basictype");
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("label").length>5).toBeTruthy(); //default show label.
				expect($scope.instform.shortstring).toBeDefined();
				
				
				//basic: retrieve from parent scope.
				$scope=setupPropEditorScope("Basictype");
				$scope.showlabel=false;
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("label").length).toBe(0); //retrieve from parent scope.
				expect($scope.instform.shortstring).toBeDefined();
				
				//honor attribute value
				template=templateCache.get("template/editor2.html");
				$scope=setupPropEditorScope("Basictype");
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("label").length).toBe(0); //retrieved from property value
				
			});
			
			it('Test InstEditor: show props', function(){	

				var templateCache = $myInjector.get("$templateCache");
				var element=null;
				
				//basic :default props from type
				var template=templateCache.get("template/editor1.html");
				var $compile=$myInjector.get("$compile");
				var $scope=setupPropEditorScope("Basictype");
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("input").length>5).toBeTruthy();
				expect($scope.instform.shortstring).toBeDefined();
				
				//honor attribute value
				template=templateCache.get("template/editor2.html");
				$scope=setupPropEditorScope("Basictype");
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("input").length).toBe(2);


				//string: props from parent scopes
				template=templateCache.get("template/editor3.html");
				$scope=setupPropEditorScope("Basictype");
				$scope.props="shortstring,propint";
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("input").length).toBe(2);
				
				//string array
				template=templateCache.get("template/editor3.html");
				$scope=setupPropEditorScope("Basictype");
				$scope.props=['shortstring', 'propint'];
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("input").length).toBe(2);
				
				//object array
				template=templateCache.get("template/editor3.html");
				$scope=setupPropEditorScope("Basictype");
				$scope.props=[$scope.type.getProp("shortstring"), $scope.type.getProp("propint")];
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("input").length).toBe(2);
				
				//some thing bad
				//object array
				template=templateCache.get("template/editor3.html");
				$scope=setupPropEditorScope("Basictype");
				$scope.props=[{}, function(){ //ok
					}, $scope.type.getProp("shortstring") ];

				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("input").length).toBe(1);
				
				
				//new template
				template=templateCache.get("template/editor4.html");
				$scope=setupPropEditorScope("Basictype");
				$scope.props=['shortstring', 'propint'];
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("table tr").length).toBe(2);
				
				//extensible property
				$scope=setupPropEditorScope("Mspecial");
				expect($scope.type.isExtensible()).toBeTruthy();
				
				//editable
				template=templateCache.get("template/editor1.html");
				$scope=setupPropEditorScope("Basictype");
				angular.forEach($scope.type.getEditProps(), function(prop){
					prop.setEditable(false);
				});
				$scope.type.cleanCache();
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("input").length).toBe(0);
				
				//editable
				template=templateCache.get("template/editor1.html");
				$scope=setupPropEditorScope("Basictype");
				angular.forEach($scope.type.getEditProps(), function(prop){
					prop.setEditable(false);
				});
				$scope.type.cleanCache();
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("input").length).toBe(0);
				
			});
			it('Test InstEditor: template-url', function(){	

				var templateCache = $myInjector.get("$templateCache");
				var element=null;
				
				//new template
				template=templateCache.get("template/editor4.html");
				$scope=setupPropEditorScope("Basictype");
				$scope.props=['shortstring', 'propint'];
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.find("table tr").length).toBe(2);
				
			});
			
			
			it('Test InstEditor: type-specific url', function(){
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/editor1.html");
				var $compile=$myInjector.get("$compile");

				var element=null;
				templateCache.put("template/props/edit/basictype/inst.html","ABCDEF");
				
				template=templateCache.get("template/editor1.html");
				$scope=setupPropEditorScope("Basictype");
				$scope.props=['shortstring', 'propint'];
				element=$compile(template)($scope);
				$scope.$digest();
				expect(element.html().indexOf("ABCDEF")).not.toEqual(-1);
				
				
			});
			
			it('Test InstEditor: value from query parameter: basic ', function(){
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/editor1.html");
				var $compile=$myInjector.get("$compile");
	
				
				var $location=$myInjector.get("$location");
				spyOn($location, 'search').andReturn({
					'fname': "testname",
					"singleembed.streetAddress": "sst",
					"singleembed.mint": "3,4,6,7",
					"multiembed.0.streetAddress": "sst1",
					"multiembed.2.streetAddress": "sst2"
				});
//				$location.search("fname", "testname");
//				$location.search("singleembed.streetAddress", "sst");
//				$location.search("singleembed.mint", "3,4,6,7");
//			
//				
//				//multiembed
//				$location.search("multiembed.0.streetAddress", "sst1");
//				$location.search("multiembed.2.streetAddress", "sst2");
				//$rootScope.$apply();
				
				
				//basic an input field
				$scope=setupPropEditorScope("Embedmain");
				element=$compile(template)($scope);
				$scope.$digest();
				
				expect($scope.inst.fname).toEqual("testname");
				expect($scope.inst.singleembed.streetAddress).toEqual("sst");
				expect($scope.inst.singleembed.mint.length).toBe(4);
				expect($scope.inst.singleembed.mint[0]).toBe(3);
				expect($scope.inst.singleembed.mint[1]).toBe(4);
				expect($scope.inst.singleembed.mint[2]).toBe(6);
				expect($scope.inst.singleembed.mint[3]).toBe(7);
				
				expect($scope.inst.multiembed.length).toBe(3);
				expect($scope.inst.multiembed[0].streetAddress).toEqual("sst1");
				expect($scope.inst.multiembed[1].streetAddress).toBeNull();
				expect($scope.inst.multiembed[2].streetAddress).toEqual("sst2");
				
			});
			
			
			it('Test Embedded:SingleEmbedded', function() {
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/prop_edit_decorator.html");
				var $compile=$myInjector.get("$compile");

				var element=null;
				
				//basic an input field
				$scope=setupPropEditorScope("Embedmain", "singleembed");
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope.$digest();
				var $originalScope=$scope;
				$scope=element.find(".embedded").scope();
				
				
				var subform=$scope.field;
				var streetAaddress=$scope.field.streetAddress;
				var mint=$scope.field.mint;
				var mstr=$scope.field.mstr;
				

				//check individual field.
				expect(streetAaddress.$dirty).toBeFalsy();
				//-----------minlength
				streetAaddress.$setViewValue("at");
				$scope.$digest(); 
				streetAaddress.$setViewValue("");
				$scope.$digest(); 
				expect(streetAaddress.$dirty).toBeTruthy();
				expect(streetAaddress.$invalid).toBeTruthy();
				expect(streetAaddress.$valid).toBeFalsy();
				expect(subform.$invalid).toBeTruthy();
				expect(streetAaddress.$error.required).toBeTruthy();
				
				
				expect(mint.$dirty).toBeFalsy();
				//-----------minlength
				mint.$setViewValue("6,7,8,,");
				$scope.$digest(); 
				expect(mint.$dirty).toBeTruthy();
				expect(mint.$valid).toBeTruthy();
				expect(mint.$invalid).toBeFalsy();
				expect($originalScope.inst.singleembed.mint.length).toBe(3);
				expect($originalScope.inst.singleembed.mint[0]).toBe(6);
				
				
				//scope here is scope for embeddded inst
				expect($originalScope.inst.singleembed[flexdms.parentinst]).toBe($originalScope.inst);
				expect($originalScope.inst.singleembed[flexdms.insttype]).toBeDefined();
				expect($originalScope.inst.singleembed[flexdms.insttype]).toEqual("Embed1");
				
			});
			it('Test Embedded:MultiEmbedded', function() {
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/prop_edit_decorator.html");
				var $compile=$myInjector.get("$compile");
		
				var element=null;
				
				
				
				//basic an input field
				$scope=setupPropEditorScope("Embedmain", "multiembed");
				var isRequired=$scope.propobj.isRequired();
				$scope.propobj.setRequired(true);
				
				//the scope will be modified by prop-edit-decorator, PropInput, validator, ngForm, ngModel
				//we only test prop editor: input is inserted into place correctly.
				element=$compile(template)($scope);
				$scope.$digest();
				var embedElement=element.find(".multiple")[0];
				$scope=angular.element(embedElement).scope();
				
				$scope.addItem();
				$scope.addItem();
				$scope.$digest();
				
				
				
				expect($scope.inst[flexdms.insttype]).toEqual("Embedmain");
				
				expect($scope.inst.multiembed.length).toBe(2);
				expect($scope.inst.multiembed[0][flexdms.insttype]).toBeDefined();
				expect($scope.inst.multiembed[0][flexdms.insttype]).toEqual("Embed2");
				
				expect($scope.field).toBeDefined();
				expect($scope.field.$name).toEqual("multiembed");
				expect($scope.field.multiembed0).toBeDefined();
				expect($scope.field.multiembed1).toBeDefined();
				expect($scope.field.multiembed0.streetAddress).toBeDefined();
				expect($scope.field.multiembed1.streetAddress).toBeDefined();
				
				$scope.field.multiembed0.streetAddress.$setViewValue("at");
				$scope.$digest();
				$scope.field.multiembed0.streetAddress.$setViewValue("");
				$scope.$digest();
				
				expect($scope.field.multiembed0.streetAddress.$error.required).toBeTruthy();
				expect($scope.field.multiembed0.streetAddress.$invalid).toBeTruthy();
				expect($scope.field.multiembed0.$invalid).toBeTruthy();
				expect($scope.field.$invalid).toBeTruthy();
				expect($scope.field.fakelist).toBeDefined();
				expect($scope.field.fakelist.$error.required).toBeFalsy();
				expect($scope.field.fakelist.$valid).toBeTruthy();
				
				$scope.removeItem(0);
				$scope.removeItem(0);
				$scope.$digest();
				expect($scope.field.fakelist.$error.required).toBeTruthy();
				expect($scope.field.fakelist.$invalid).toBeTruthy();
				expect($scope.field.$invalid).toBeTruthy();
				
				$scope.propobj.setRequired(isRequired);
				
			});
			
			
			//make this test last one
			//the register controller can not be unregistered
			it('Test InstEditor: type specific controller', function(){
				angular.module("instApp").$controllerProvider.register("mainCtrl", function($scope){
					$scope.inst.maintest=1;
				});
				angular.module("instApp").$controllerProvider.register("singleEmbedded", function($scope){
					$scope.inst.singletest=2;
				});
				angular.module("instApp").$controllerProvider.register("multiEmbedded", function($scope){
					$scope.inst.multitest=3;
				});
				flexdms.config.addConfig("editor", "Embedmain", "ctrl", 'mainCtrl');
				flexdms.config.addConfig("editor", "Embed1", "ctrl", "singleEmbedded");
				flexdms.config.addConfig("editor", "Embed2", "ctrl", "multiEmbedded");
				
				
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/editor1.html");
				var $compile=$myInjector.get("$compile");
				
				
				$scope=setupPropEditorScope("Embedmain");
				//give multiembedded an element
				$scope.inst.multiembed=[$myInjector.get("Inst").newInst("Embed2")];
				
				element=$compile(template)($scope);
				$scope.$digest();
				
				expect($scope.inst.maintest).toBe(1);
				expect($scope.inst.singleembed.singletest).toBe(2);
				expect($scope.inst.multiembed[0].multitest).toBe(3);
			});
		});
