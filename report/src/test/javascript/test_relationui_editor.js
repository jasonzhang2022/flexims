describe(
		"relationui: relationui editting in type administration",
		function() {
			var $rootScope = null;
			var $http = null;
			var $controller = null;
			var $myInjector = null;

			
			beforeEach(function(){
				flexdms.typemeta=meta;
			});
			beforeEach(module("instApp"));
			
			// Store references to $rootScope and $compile
			// so they are available to all tests in this describe block
			beforeEach(inject(function($injector) {
				
				$http = $injector.get("$httpBackend");
				$rootScope = $injector.get("$rootScope");
				$controller = $injector.get('$controller');
				$myInjector = $injector;
				$http.whenGET("rs/res/web/customtemplates/home.html").respond("<div>This is home</div>");
			}));
			
			afterEach(function() {
				$http.verifyNoOutstandingRequest();
				$http.verifyNoOutstandingExpectation();
			});
			
			it("RelationUI Spec: add", function() {
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/allreportbytype/Mdoomroom").respond(allreportforMdoomroom, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/reportbytype/Mdoomroom").respond(reportbytypForMdoomroom, jsonHeaders);
				
				//step2 create the needed html 
				var $topScope=$rootScope.$new();
				$controller("fxRelationuiSpecCtrl", {
					$scope : $topScope,
					$stateParams: {
						'typename':'Mstudent',
						'propname':'doomroom'
					},
					instCache: $myInjector.get("instCache"),
					reportService: $myInjector.get("reportService")
				});
				var $compile=$myInjector.get("$compile")
				var template=$myInjector.get("$templateCache").get("template/report/prop_editor_ui.html");
				var element=$compile(template)($topScope);
				$topScope.$digest();
				$http.flush();
				
				expect(element.find("select#relationuireport option").length).toBe(3);
				expect(element.find(".form-group:nth(1)").hasClass("ng-hide")).toBeTruthy();
				
				$topScope.relationuiform.relationuireport.$setViewValue("10055");
				$topScope.$digest();
				expect($topScope.relationui.report).toBe("10055");
				expect(element.find(".form-group:nth(1)").hasClass("ng-hide")).toBeFalsy()
				expect(element.find("select[name^=param]").length).toBe(0);
				
				$topScope.relationuiform.relationuireport.$setViewValue("10350");
				$topScope.$digest();
				expect($topScope.relationui.report).toBe("10350");
				expect(element.find(".form-group:nth(1)").hasClass("ng-hide")).toBeFalsy();
				expect(element.find("select[name^=param]").length).toBe(1);
				expect(element.find("select[name=param0] option").length).toBe(3);
				expect(element.find("select[name=param0] option").get(1).text).toBe("number");
				
				
				$topScope.relationuiform.relationuireport.$setViewValue(null);
				$topScope.$digest();
				expect(element.find("select[name^=param]").length).toBe(0);
				expect(element.find(".form-group:nth(1)").hasClass("ng-hide")).toBeTruthy();
				
				
			});
			
			/*
			 * <orm:property name="relationui.report" value="10350"/>
<orm:property name="relationui.uitype" value="Inline Table"/>
<orm:property name="relationui.params.0" value="number"/>
<orm:property name="relationui.showselected" value="false"/>
			 */
			it("RelationUI Spec: edit", function() {
				
				var relationUI={
						report:'10350',
						uitype:'Inline Table',
						params: ['number'],
						showSelected:false
				};
				var student=flexdms.findType("Mstudent");
				var propobj=student.getProp("doomroom");
				flexdms.setExtraPropObj(propobj.prop,"relationui", relationUI);
					
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/allreportbytype/Mdoomroom").respond(allreportforMdoomroom, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/reportbytype/Mdoomroom").respond(reportbytypForMdoomroom, jsonHeaders);
				
				//step2 create the needed html 
				var $topScope=$rootScope.$new();
				$controller("fxRelationuiSpecCtrl", {
					$scope : $topScope,
					$stateParams: {
						'typename':'Mstudent',
						'propname':'doomroom'
					},
					instCache: $myInjector.get("instCache"),
					reportService: $myInjector.get("reportService")
				});
				var $compile=$myInjector.get("$compile")
				var template=$myInjector.get("$templateCache").get("template/report/prop_editor_ui.html");
				var element=$compile(template)($topScope);
				$topScope.$digest();
				$http.flush();
				
				
				expect(element.find("select#relationuireport option").length).toBe(3);
				expect(element.find("select#relationuireport").val()).toEqual("1");
				
				expect(element.find(".form-group:nth(1)").hasClass("ng-hide")).toBeFalsy();
				expect(element.find(".form-group:nth(1) select").val()).toBe("1");
				
				expect(element.find("select[name=param0] option").length).toBe(3);
				expect(element.find("select[name=param0] option").get(1).selected).toBeTruthy();
				
				
			});
		});