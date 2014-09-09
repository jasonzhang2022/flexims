describe(
		"fxreport.js: query params editting existing report Instance UI",
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
			var addFxTemplate="<div><fx-inst-editor-form fx-typename='FxReport'/></div>";
			var editFxTemplate="<div><fx-inst-editor-form fx-typename='FxReport' fx-inst-id='10300'/></div>";
			
			it("query parameter from query:single int", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="propint";
				cond1.FirstValue="50";
				
				var myfxreport={};
				angular.copy(fxReport, myfxreport)
				myfxreport.FxReport.ParamValues.paramValues=[ {
					 "index" : 0,
					 "values":["70"]
				}];
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/FxReport/10300").respond(myfxreport, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(editFxTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				//---------html is ready here
				
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isNumber(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value).toBe(70);
				
				//check visble form value
				expect(paramsui.find("input").length).toBe(1);
				expect(paramsui.find("input").val()).toEqual("70");
				
				//change UI value
				var paramsform=reportScope.instpscope.instform.queryparamsform;
				paramsform.param0.$setViewValue("x");
				reportScope.$digest();
				expect(paramsform.param0.$invalid).toBeTruthy();
				
				paramsform.param0.$setViewValue("90");
				reportScope.$digest();
				expect(paramsform.param0.$valid).toBeTruthy();
				
				expect(reportScope.params[0].getValueObject(0).values[0]).toEqual("90");
				
			});
			
			
			it("query parameter from query:single date", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="propdate";
				var date=new Date("2013/04/05");
				cond1.FirstValue=date.toISOString();
				
				var myfxreport={};
				angular.copy(fxReport, myfxreport)
				var rdate=new Date("2013/04/07");
				myfxreport.FxReport.ParamValues.paramValues=[ {
					 "index" : 0,
					 "values":[rdate.toISOString()]
				}];
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/FxReport/10300").respond(myfxreport, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(editFxTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				//---------html is ready here
				
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isDate(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value).toEqual(rdate)
				
				//check visble form value
				expect(paramsui.find("input").length).toBe(3);
				expect(paramsui.find("input").eq(2).val()).toEqual("2013/04/07");
				
				//change UI value
				var paramsform=reportScope.instpscope.instform.queryparamsform;
				paramsform.param0.$setViewValue("x");
				reportScope.$digest();
				expect(paramsform.param0.$invalid).toBeTruthy();
				
				paramsform.param0.$setViewValue("2013/05/07");
				reportScope.$digest();
				expect(paramsform.param0.$valid).toBeTruthy();
				
				expect(reportScope.params[0].getValueObject(0).values[0]).toEqual(new Date("2013/05/07").toISOString());
			});
			
			
			it("query parameter from query:multiple dates", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="propdate";
				cond1.Operator="oneof"
				cond1.FirstValue=new Date("2013/04/05").toISOString()+","+new Date("2013/04/06")
				
				var myfxreport={};
				angular.copy(fxReport, myfxreport)
				var rdate=new Date("2013/04/07");
				myfxreport.FxReport.ParamValues.paramValues=[ {
					 "index" : 0,
					 "values":[new Date("2013/04/07").toISOString(), new Date("2013/04/08").toISOString()]
				}];
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/FxReport/10300").respond(myfxreport,jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(editFxTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				//---------html is ready here
				
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(1);
				expect(angular.isArray(reportScope.params[0].value)).toBeTruthy();
				expect(angular.isDate(reportScope.params[0].value[0])).toBeTruthy();
				expect(reportScope.params[0].value[0]).toEqual(new Date("2013/04/07"))
				
				//check visble form value
				expect(paramsui.find("input").length).toBe(2);
				expect(paramsui.find("input").eq(0).val()).toEqual("2013/04/07");
				expect(paramsui.find("input").eq(1).val()).toEqual("2013/04/08");
				
				//change UI value
				var paramsform=reportScope.instpscope.instform.queryparamsform;
				paramsform.param0.param_internal0.$setViewValue("x");
				reportScope.$digest();
				expect(paramsform.param0.$invalid).toBeTruthy();
				
				paramsform.param0.param_internal0.$setViewValue("2013/05/07");
				paramsform.param0.param_internal1.$setViewValue("2013/05/08");
				reportScope.$digest();
				expect(paramsform.param0.$valid).toBeTruthy();
				
				expect(reportScope.params[0].getValueObject(0).values[0]).toEqual(new Date("2013/05/07").toISOString());
				expect(reportScope.params[0].getValueObject(0).values[1]).toEqual(new Date("2013/05/08").toISOString());
			});
			
			
			it("query parameter from query:relative date", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="propdate";
				cond1.Operator="between"
				cond1.FirstValue=new Date("2013/04/05").toISOString();
				cond1.SecondValue=new Date("2013/04/06").toISOString();
				
				var myfxreport={};
				angular.copy(fxReport, myfxreport)
				var rdate=new Date("2013/04/07");
				myfxreport.FxReport.ParamValues.paramValues=[ {
					 "index" : 0,
					 "values":[new Date("2013/04/07").toISOString()]
				},  {
					 "index" : 1,
					 "values":[new Date("2013/04/08").toISOString()]
				},
				];
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown,jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/FxReport/10300").respond(myfxreport, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(editFxTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				//---------html is ready here
				
				var reportScope=element.find(".FxReport").scope();
				var paramsui=element.find(".queryparams")
				//check params model value is correct
				expect(reportScope.params.length).toBe(2);
				expect(angular.isDate(reportScope.params[0].value)).toBeTruthy();
				expect(reportScope.params[0].value).toEqual(new Date("2013/04/07"))
				
				//check visble form value
				expect(paramsui.find("input").length).toBe(6);
				expect(paramsui.find("input").eq(2).val()).toEqual("2013/04/07");
				expect(paramsui.find("input").eq(5).val()).toEqual("2013/04/08");
				
				
				//change UI value
				var paramsform=reportScope.instpscope.instform.queryparamsform;
				//paramsform.param0..$setViewValue("");
				//paramsform.param1..$setViewValue("");
				paramsform.rtime0.$setViewValue("-1");
				paramsform.rtime1.$setViewValue("1");
				reportScope.$digest();
				
				expect(reportScope.params[0].getValueObject(0).values[0]).toEqual("-1:MONTH:false");
				expect(reportScope.params[1].getValueObject(0).values[0]).toEqual("1:MONTH:false");
			});
			
		});