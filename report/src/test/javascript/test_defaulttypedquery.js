describe(
		"defaulttypedquery.js: Defaulttypedquery instance UI",
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
				
			
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/querybytype/Mstudent").respond(alltracedown, jsonHeaders);
				$http.whenGET("rs/res/web/customtemplates/home.html").respond("<div>This is home</div>");
				
			}));
			
			afterEach(function() {
				$http.verifyNoOutstandingRequest();
				//$http.verifyNoOutstandingExpectation();
			});
			var addTemplate="<div><fx-inst-editor-form fx-typename='DefaultTypedQuery'/></div>";
			var editFxTemplate="<div><fx-inst-editor-form fx-typename='FxReport' fx-inst-id='10300'/></div>";
			
			it("TargetedType", function() {
				//step2 create the needed html 
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addTemplate)($topScope);
				$topScope.$digest();
				//$http.flush();
				
				var tt=element.find(".prop.TargetedType");
				expect(tt.find("select").first().find("option").length>5).toBeTruthy(); //we  have option.
				
			});
			
			it("Tracedown", function() {
				//step2 create the needed html 
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				
				var instScope=element.find(".DefaultTypedQuery").scope();
				var tt=element.find(".prop.TargetedType");
				tt.scope().field.$setViewValue("Mdoombuild");
				$topScope.$digest();
				
				//expect(tt.val()).toEqual("Mdoombuild"); 
				expect(instScope.inst.TargetedType).toEqual("Mdoombuild"); 
				//add one item
				var condelement=element.find(".prop.Conditions .multiple");
				condelement.scope().addItem();
				$topScope.$digest();
				
				var firstcond=condelement.find(".embedded");
				firstcond.scope().inst.Property="students";
				firstcond.scope().inst.Operator="tracedown";
				$topScope.$digest();
				$http.flush();
				
				//tracedown is loaded.
				expect(firstcond.find("select[name='TraceDown'] option").length).toBe(2);
				
				
			});
			
			
	
			
			//query parameter validation
			it("FirstValue: validation single value", function() {
				//step2 create the needed html 
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				
				var instScope=element.find(".DefaultTypedQuery").scope();
				var tt=element.find(".prop.TargetedType");
				tt.scope().field.$setViewValue("Basictype");
				$topScope.$digest();
				expect(instScope.inst.TargetedType).toEqual("Basictype"); 
				
				//add one item
				var condelement=element.find(".prop.Conditions .multiple");
				condelement.scope().addItem();
				$topScope.$digest();
				
				//----------------integer
				var firstcond=condelement.find(".embedded");
				firstcond.scope().inst.Property="propint";
				firstcond.scope().inst.Operator="eq";
				$topScope.$digest();
				
				var field=firstcond.scope().field.firstvaluestring;
				field.$setViewValue("xhg");
				$topScope.$digest();
				expect(field.$invalid).toBeTruthy();
				expect(field.$error.integer).toBeTruthy();
				
				//----------------float
				var firstcond=condelement.find(".embedded");
				firstcond.scope().inst.Property="propfloat";
				firstcond.scope().inst.Operator="eq";
				$topScope.$digest();
				
				var field=firstcond.scope().field.firstvaluestring;
				field.$setViewValue("xhg");
				$topScope.$digest();
				expect(field.$invalid).toBeTruthy();
				expect(field.$error.number).toBeTruthy();
				
				//----------------date
				var firstcond=condelement.find(".embedded");
				firstcond.scope().inst.Property="propdate";
				firstcond.scope().inst.Operator="eq";
				$topScope.$digest();
				
				var field=firstcond.scope().field.firstvaluestring;
				field.$setViewValue("xhg");
				$topScope.$digest();
				expect(field.$invalid).toBeTruthy();
				expect(field.$error.date).toBeTruthy();
				
				//the view value is a local time.
				//if mapped to UTC time, the hours could be nonzero value.
				field.$setViewValue("2012-02-14");
				$topScope.$digest();
				expect(field.$valid).toBeTruthy();
				var d=new Date("2012-02-14");
				d.setHours(0, 0, 0, 0);
				expect(firstcond.scope().inst.FirstValue).toEqual(d.toISOString());
				
				//----------------datetime
				var firstcond=condelement.find(".embedded");
				firstcond.scope().inst.Property="proptimestamp";
				firstcond.scope().inst.Operator="eq";
				$topScope.$digest();
				
				var field=firstcond.scope().field.firstvaluestring;
				field.$setViewValue("xhg");
				$topScope.$digest();
				expect(field.$invalid).toBeTruthy();
				expect(field.$error.datetime).toBeTruthy();
				
				field.$setViewValue("2012/02/14 03:05:25");
				$topScope.$digest();
				expect(field.$valid).toBeTruthy();
				expect(firstcond.scope().inst.FirstValue).toEqual(new Date("2012/02/14 03:05:25").toISOString());
				
				
				//----------------time
				var firstcond=condelement.find(".embedded");
				firstcond.scope().inst.Property="proptime";
				firstcond.scope().inst.Operator="eq";
				$topScope.$digest();
				
				var field=firstcond.scope().field.firstvaluestring;
				field.$setViewValue("xhg");
				$topScope.$digest();
				expect(field.$invalid).toBeTruthy();
				expect(field.$error.time).toBeTruthy();
				
				field.$setViewValue("03:05:27");
				$topScope.$digest();
				expect(field.$valid).toBeTruthy();
				d=new Date("1970-01-01T03:05:27");
				d.setFullYear(1970, 0, 1);
				expect(firstcond.scope().inst.FirstValue).toEqual(d.toISOString());
				
				
				
				
			});
		});