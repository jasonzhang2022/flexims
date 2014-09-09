describe(
		"report8.js: report Instance edit ui",
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
			
			it("OrderBy", function() {
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
				myfxreport.FxReport.OrderBy= {
						"entry" : [ {
							"key" : "name",
							"value" : "ASC"
						} ]
				};
				
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/FxReport/10300").respond(myfxreport, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(editFxTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				//---------html is ready here
				
				var orderby=element.find(".prop.OrderBy");
				expect(orderby.find("select").first().find("option").length>5).toBeTruthy(); //we  have option.
				expect(orderby.find("input[type=radio]")[0].checked).toBeTruthy();
				expect(orderby.find("input[type=radio]")[1].checked).toBeFalsy();
				
				
				//add one item
				orderby.find("div[data-fx-name-value-list]").scope().addItem();
				$topScope.$digest();
				expect(orderby.find("select").length).toBe(2);
				expect(orderby.find("input[type=radio]")[0].checked).toBeTruthy();
				expect(orderby.find("input[type=radio]")[1].checked).toBeFalsy();
				expect(orderby.find("input[type=radio]")[2].checked).toBeFalsy();
				expect(orderby.find("input[type=radio]")[3].checked).toBeFalsy();
				
			
				
				//change orderby
				var reportScope=element.find(".panel.ng-scope").scope();
				reportScope.instform.OrderBy.OrderBy_1_value.$setViewValue("DESC");
				reportScope.instform.OrderBy.OrderBy_1_key.$setViewValue("propdate");
				$topScope.$digest();
				expect(reportScope.inst.OrderBy.entry.length).toBe(2);
				expect(reportScope.inst.OrderBy.entry[1].key).toBe("propdate");
				expect(reportScope.inst.OrderBy.entry[1].value).toBe("DESC");
				
				
			});
				
		// data-picklist-src
			it("Property", function() {
				//step 1 modify query to suit your needs
				var tracedown={};
				angular.copy(alltracedown, tracedown);
				var cond1=tracedown.entities.DefaultTypedQuery[0].Conditions[0];
				cond1.Property="propint";
				delete(cond1.FirstValue);

				
				var myfxreport={};
				angular.copy(fxReport, myfxreport)
				myfxreport.FxReport.ParamValues.paramValues=[];
				myfxreport.FxReport.Properties= {
						"entry" : [ {
							"key" : "0",
							"value" : "shortstring"
						} , {
							"key" : "1",
							"value" : "propint"
						} ]
				};
				
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/alltracedown/10350").respond(tracedown, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/FxReport/10300").respond(myfxreport, jsonHeaders);
				
				
				//step2 create the needed html 
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(editFxTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				$topScope.$digest();

				var props=element.find(".prop.Properties");
				expect(props.find("select").first().find("option").length>5).toBeTruthy(); //we  have option.
				expect(props.find("select").eq(1).find("option").length).toBe(2);	
			});
		});