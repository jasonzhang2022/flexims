describe(
		"queryparam.js: from query value to report in report editor instance",
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
				//hijack utilities service
				var Util=$injector.get("Util");
				var $q=$injector.get("$q");
				spyOn(Util, "getEnum").andCallFake(function(params) {
					var deferred = $q.defer();
					var data=null;
					if (params.classname==='com.flexdms.flexims.util.TimeUnit'){
						data=timeunits.nameValueList.entry;
					} else {
						data=operators.nameValueList.entry;
					}
					deferred.resolve(data);
					data.$promise=deferred.promise;
					return data;
			    });
				
				$http.whenGET("rs/res/web/customtemplates/home.html").respond("<div>This is home</div>");
			}));

			afterEach(function() {
				$http.verifyNoOutstandingRequest();
				$http.verifyNoOutstandingExpectation();
			});
			function establishFxReportAddScope(){
				var $scope=$rootScope.$new();
				var $location=$myInjector.get("$location");
				spyOn($location, "search").andCallFake(function(params) {
					return {
						"Query":"10350"
					};
				});
				return $scope;
				
			}
			var addFxTemplate="<div><fx-inst-editor-form fx-typename='FxReport' /></div>";
			var editFxTemplate="<div><fx-inst-editor-form fx-typename='FxReport' fx-inst-id='10201'/></div>";
			
			it("query parameter from query:single int", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				
				//step2 create the needed html 
				var $topScope=establishFxReportAddScope();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$http.flush();
				$topScope.$digest();
				//---------html is ready here
				
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isNumber(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value).toBe(50);
				
				//check visble form value
				expect(paramsui.find("input").length).toBe(1);
				expect(paramsui.find("input").val()).toEqual("50");
				
				//value to server
				var pvs=reportScope.params[0].getValueObject(0).values;
				expect(angular.isArray(pvs)).toBeTruthy();
				expect(pvs.length).toBe(1);
				expect(pvs[0]).toBe("50");
				
			});
			it("query parameter from query:single str", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="shortstring";
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=establishFxReportAddScope();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$http.flush();
				$topScope.$digest();
				//---------html is ready here
				
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isString(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value).toBe("50");
				
				//check visble form value
				expect(paramsui.find("input").length).toBe(1);
				expect(paramsui.find("input").val()).toEqual("50");
				
				//value to server
				var pvs=reportScope.params[0].getValueObject(0).values;
				expect(angular.isArray(pvs)).toBeTruthy();
				expect(pvs.length).toBe(1);
				expect(pvs[0]).toBe("50");
				
			});
			it("query parameter from query:single Date", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="propdate";
				var date=new Date("2013/04/05")
				cond1.FirstValue=date.toISOString();
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=establishFxReportAddScope();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$http.flush();
				$topScope.$digest();
				//---------html is ready here
				
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isDate(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value).toEqual(date)
				
				expect(paramsui.find("input").length).toBe(3);
				expect(paramsui.find("select").length).toBe(1);
				var viewdate=new Date(paramsui.find("input[name=param0]").val());
				expect(viewdate.getFullYear()).toEqual(2013);
				expect(viewdate.getMonth()).toEqual(3);
				expect(viewdate.getDate()).toEqual(5);
				
				var pvs=reportScope.params[0].getValueObject(0).values;
				expect(angular.isArray(pvs)).toBeTruthy();
				expect(pvs.length).toBe(1);
				expect(pvs[0]).toBe(viewdate.toISOString());
				
			});
			it("query parameter from query:date relative start", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="propdate";
				delete(cond1.FirstValue);
				cond1.RelativeStartUnit='WEEK';
				cond1.RelativeStartDate=-1;
				cond1.WholeTime=true;
				
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=establishFxReportAddScope();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$http.flush();
				$topScope.$digest();
				//---------html is ready here
				
				//model value
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(reportScope.params[0].relativevalue).toBe(-1);
				expect(reportScope.params[0].relativeunit).toEqual("WEEK");
				
				//view value
				expect(paramsui.find("input").length).toBe(3);
				expect(paramsui.find("select").length).toBe(1);
				expect(paramsui.find("input[name=param0]").val()).toEqual("");
				expect(paramsui.find("input[name=rtime0]").val()).toEqual("-1");
				expect(paramsui.find("select[name=rtimeunit0]").val()).toEqual("0"); //week is the first option
				expect(paramsui.find("input[name=wholetime0]")[0].checked).toBeTruthy();
				
				//value to server
				var pvs=reportScope.params[0].getValueObject(0).values;
				expect(angular.isArray(pvs)).toBeTruthy();
				expect(pvs.length).toBe(1);
				expect(pvs[0]).toBe("-1:WEEK:true");
				
			});
			
			it("query parameter from query:multiple integers", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="propint";
				cond1.Operator="oneof"
				cond1.FirstValue="3,5";
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=establishFxReportAddScope();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$http.flush();
				$topScope.$digest();
				//---------html is ready here
				
				//model value
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isArray(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value[0]).toBe(3);
				expect(reportScope.params[0].value[1]).toBe(5);
				
				//view value
				expect(paramsui.find("input").length).toBe(2);
				expect(paramsui.find("input").first().val()).toEqual("3");
				expect(paramsui.find("input").eq(1).val()).toEqual("5");
				
				
				//value to server
				var pvs=reportScope.params[0].getValueObject(0).values;
				expect(angular.isArray(pvs)).toBeTruthy();
				expect(pvs.length).toBe(2);
				expect(pvs[0]).toBe("3");
				expect(pvs[1]).toBe("5");
				
			});
			it("query parameter from query:multiple String", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="shortstring";
				cond1.Operator="oneof"
				cond1.FirstValue="v1,v2";
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=establishFxReportAddScope();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$http.flush();
				$topScope.$digest();
				//---------html is ready here
				
				//model value
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isArray(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value[0]).toBe("v1");
				expect(reportScope.params[0].value[1]).toBe("v2");
				
				//view value
				expect(paramsui.find("input").length).toBe(2);
				expect(paramsui.find("input").first().val()).toEqual("v1");
				expect(paramsui.find("input").eq(1).val()).toEqual("v2");
				
				
				//value to server
				var pvs=reportScope.params[0].getValueObject(0).values;
				expect(angular.isArray(pvs)).toBeTruthy();
				expect(pvs.length).toBe(2);
				expect(pvs[0]).toBe("v1");
				expect(pvs[1]).toBe("v2");
				
			});
			
			it("query parameter from query:multiple Date", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="propdate";
				cond1.Operator="oneof"
				var date1=new Date("2013/04/05");
				var date2=new Date("2013/04/07");
				cond1.FirstValue=date1.toISOString()+","+date2.toISOString();
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=establishFxReportAddScope();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$http.flush();
				$topScope.$digest();
				//---------html is ready here
				
				//model value
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isArray(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value[0]).toEqual(date1);
				expect(reportScope.params[0].value[1]).toEqual(date2);
				
				//view value
				expect(paramsui.find("input").length).toBe(2);
				expect(paramsui.find("input").first().val()).toEqual("2013/04/05");
				expect(paramsui.find("input").eq(1).val()).toEqual("2013/04/07");
				
				
				//value to server
				var pvs=reportScope.params[0].getValueObject(0).values;
				expect(angular.isArray(pvs)).toBeTruthy();
				expect(pvs.length).toBe(2);
				expect(pvs[0]).toBe(date1.toISOString());
				expect(pvs[1]).toBe(date2.toISOString());
				
			});
			it("query parameter from query:Size", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="propdate";
				cond1.Operator="size"
				cond1.FirstValue="3";
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=establishFxReportAddScope();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$http.flush();
				$topScope.$digest();
				//---------html is ready here
				
				//model value
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isNumber(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value).toEqual(3);
				
				
				//view value
				expect(paramsui.find("input").length).toBe(1);
				expect(paramsui.find("input").val()).toEqual("3");
				
				
				//value to server
				var pvs=reportScope.params[0].getValueObject(0).values;
				expect(angular.isArray(pvs)).toBeTruthy();
				expect(pvs.length).toBe(1);
				expect(pvs[0]).toBe("3");
				
			});
			
			it("query parameter from query:Single time", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="proptime";
				cond1.Operator="gt";
				var time=new Date();
				time.setFullYear(1970, 0, 1);
				cond1.FirstValue=time.toISOString();
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=establishFxReportAddScope();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$http.flush();
				$topScope.$digest();
				//---------html is ready here
				
				//model value
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isDate(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value).toEqual(new Date(cond1.FirstValue));
				
				
				//view value
				expect(paramsui.find("input").length).toBe(2);
				//hour is complicated. By default, hour is using AM/PM
				//expect(paramsui.find("input").first().val()-0).toEqual(time.getHours()%12);
				expect(paramsui.find("input").eq(1).val()-0).toEqual(time.getMinutes());
				
				
				//value to server
				var pvs=reportScope.params[0].getValueObject(0).values;
				expect(angular.isArray(pvs)).toBeTruthy();
				expect(pvs.length).toBe(1);
				expect(pvs[0]).toBe(new Date(cond1.FirstValue).toISOString());
			});
			
			it("query parameter from query:Multiple time", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="proptime";
				cond1.Operator="oneof";
				var time1=new Date();
				var time2=new Date();
				time1.setFullYear(1970, 0, 1);
				time2.setFullYear(1970, 0, 1);
				time2.setHours(time2.getHours()+1);
				cond1.FirstValue=time1.toISOString()+","+time2.toISOString();
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=establishFxReportAddScope();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$http.flush();
				$topScope.$digest();
				//---------html is ready here
				
				//model value
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isArray(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value[0]).toEqual(time1);
				
				
				//view value
				expect(paramsui.find("input").length).toBe(4);
				//hour is complicated. By default, hour is using AM/PM
				//expect(paramsui.find("input").first().val()-0).toEqual(time1.getHours()%12);
				expect(paramsui.find("input").eq(1).val()-0).toEqual(time1.getMinutes());
				//expect(paramsui.find("input").eq(2).val()-0).toEqual(time2.getHours()%12);
				expect(paramsui.find("input").eq(3).val()-0).toEqual(time2.getMinutes());
				
				
				//value to server
				var pvs=reportScope.params[0].getValueObject(0).values;
				expect(angular.isArray(pvs)).toBeTruthy();
				expect(pvs.length).toBe(2);
				expect(pvs[0]).toBe(time1.toISOString());
				expect(pvs[1]).toBe(time2.toISOString());
			});
			it("query parameter from query:novalue", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="proptime";
				cond1.Operator="isnull"
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=establishFxReportAddScope();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$http.flush();
				$topScope.$digest();
				//---------html is ready here
				
				//model value
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1); //one parame

				//view value
				expect(paramsui.find("input").length).toBe(0); //but no inputs
			});
		
		it("query parameter from query:between", function() {
			//step 1 modify query to suit your needs
			var tracedown={};
			angular.copy(alltracedown, tracedown);
			var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
			cond1.Property="propint";
			cond1.Operator="between";
			cond1.FirstValue="5";
			cond1.SecondValue="10";
			$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
			
			
			//step2 create the needed html 
			var $topScope=establishFxReportAddScope();
			var $compile=$myInjector.get("$compile")
			var element=$compile(addFxTemplate)($topScope);
			$http.flush();
			$topScope.$digest();
			//---------html is ready here
			
			//model value
			var reportScope=element.find(".FxReport").scope();
			var paramsui=element.find(".queryparams")
			//check params model value is correct
			expect(reportScope.params.length).toBe(2); 
			expect(reportScope.params[0].value).toBe(5); //one parame
			expect(reportScope.params[1].value).toBe(10); //one parame
			
			//view value
			expect(paramsui.find("input").length).toBe(2); //but no inputs
			expect(paramsui.find("input").first().val()).toBe("5"); //but no inputs
			expect(paramsui.find("input").eq(1).val()).toBe("10"); //but no inputs
			
			//value to server
			var pvs=reportScope.params[0].getValueObject(0).values;
			expect(angular.isArray(pvs)).toBeTruthy();
			expect(pvs.length).toBe(1);
			expect(pvs[0]).toBe("5");
			expect(reportScope.params[1].getValueObject(0).values[0]).toBe("10");
		});
		it("query parameter from query:two condition", function() {
			var tracedown={};
			angular.copy(alltracedown, tracedown);
			var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
			//no value
			cond1.Property="shortstring";
			cond1.Operator="isnull";
			
			var cond2={};
			angular.copy(cond1, cond2);
			tracedown.entities.DefaultTypedQuery[0].Conditions.push(cond2);
			cond2.Property="propint";
			cond2.Operator="between";
			cond2.FirstValue="5";
			cond2.SecondValue="10";
			$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
			
			
			//step2 create the needed html 
			var $topScope=establishFxReportAddScope();
			var $compile=$myInjector.get("$compile")
			var element=$compile(addFxTemplate)($topScope);
			$http.flush();
			$topScope.$digest();
			//---------html is ready here
			
			//model value
			var reportScope=element.find(".FxReport").scope();
			var paramsui=element.find(".queryparams")
			//check params model value is correct
			expect(reportScope.params.length).toBe(3); 
			expect(reportScope.params[1].value).toBe(5); //one parame
			expect(reportScope.params[2].value).toBe(10); //one parame
			
			//view value
			expect(paramsui.find("input").length).toBe(2); //but no inputs
			expect(paramsui.find("input").first().val()).toBe("5"); //but no inputs
			expect(paramsui.find("input").eq(1).val()).toBe("10"); //but no inputs
			
			//value to server
			expect(reportScope.params[1].getValueObject(0).values[0]).toBe("5");
			expect(reportScope.params[2].getValueObject(0).values[0]).toBe("10");
		});
		
		it("query parameter from query:two condition, nested query", function() {
			var tracedown={};
			angular.copy(buildTraceDown, tracedown);
			$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10553").respond(tracedown, jsonHeaders);
			var $location=$myInjector.get("$location");
			spyOn($location, "search").andCallFake(function(params) {
				return {
					"Query":"10553"
				};
			});
			
			
			
			//step2 create the needed html 
			var $topScope=$rootScope.$new();
			var $compile=$myInjector.get("$compile")
			var element=$compile(addFxTemplate)($topScope);
			$http.flush();
			$topScope.$digest();
			//---------html is ready here
			
			//model value
			var reportScope=element.find(".FxReport").scope();
			var paramsui=element.find(".queryparams")
			//check params model value is correct
			expect(reportScope.params.length).toBe(2); 
			expect(reportScope.params[0].value).toBe("jason%"); //one parame
			expect(reportScope.params[1].value).toBe("build%"); //one parame
			
			//view value
			expect(paramsui.find("input").length).toBe(2); //but no inputs
			expect(paramsui.find("input").first().val()).toBe("jason%"); //but no inputs
			expect(paramsui.find("input").eq(1).val()).toBe("build%"); //but no inputs
			
			//value to server
			expect(reportScope.params[0].getValueObject(0).values[0]).toBe("jason%");
			expect(reportScope.params[1].getValueObject(0).values[0]).toBe("build%");
		});
			 
});
