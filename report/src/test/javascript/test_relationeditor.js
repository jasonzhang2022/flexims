describe(
		"relationeditor.js: relation editor for relation property",
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
			var addFxTemplate="<div><fx-inst-editor-form fx-typename='Mstudent'/></div>";
			var editFxTemplate="<div><fx-inst-editor-form fx-typename='Mstudent' fx-inst-id='10300'/></div>";
			
			it("MStudent one-to-one select default", function() {
				
				var student=flexdms.findType("Mstudent");
				var propobj=student.getProp("doomroom");
				flexdms.setExtraPropObj(propobj.prop,"relationui", {});
				
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(addFxTemplate)($topScope);
				$topScope.$digest();
				
				expect(element.find("input[name=doomroom][type=text]").length).toBe(1);
				
			});
			
			it("MStudent single select: no dependency", function() {
				var relationUI={
						report:'10055',
						uitype:flexdms.query.relationui.uitype[0],
						params: [],
						showselected:true
				};
				var student=flexdms.findType("Mstudent");
				var propobj=student.getProp("doomroom");
				flexdms.setExtraPropObj(propobj.prop,"relationui", relationUI);
				var uuid="8e2b6352-31ba-45c9-8865-23b90193b388";
				var preparePost= {
					'FxReportWrapper':{
						FxReport: reportbytypForMdoomroom.entities.FxReport[0]
					}	
				};
				preparePost.FxReportWrapper.FxReport.type='fxReport';
				var respo={
						   "FxReportWrapper" : {
							      "uuid" : uuid,
							      "count" : 10,
							      FxReport:  reportbytypForMdoomroom.entities.FxReport[0],
						   }
				};
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/reportbyid/10055").respond({
					entities: {
						FxReport:[reportbytypForMdoomroom.entities.FxReport[0]],
						DefaultTypedQuery:[reportbytypForMdoomroom.entities.DefaultTypedQuery[0]]
					}
				}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Mstudent/10300").respond({Mstudent: {
					doomroom: rooms.entities.Mdoomroom[0].id
				}}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Mdoomroom/794").respond({
					Mdoomroom:rooms.entities.Mdoomroom[0]
				}, jsonHeaders);
						
				$http.expectPOST(appctx.modelerprefix+"/reportrs/query/prepare", preparePost).respond(200, respo, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/fetchall/"+uuid).respond(rooms, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/TypedQuery/10057").respond({
					'DefaultTypedQuery': reportbytypForMdoomroom.entities.DefaultTypedQuery[0]
				}, jsonHeaders);
				
			
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(editFxTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				//---------html is ready here
				
				expect(element.find(".prop.doomroom select option").length).toBe(10);
				//select is initialized correctly from original inst value.
				expect(element.find(".prop.doomroom select").val()-0).toEqual(rooms.entities.Mdoomroom[0].id);
				
				
				var formScope=element.find("form").scope();
				//checked show select
				//selection
				var select=element.find(".prop.doomroom select");
				select.controller("ngModel").$setViewValue(rooms.entities.Mdoomroom[1]);
				$topScope.$digest();
				//no select is shown for simple selection
				expect(element.find(".prop.doomroom div.selected a").length).toBe(0);
				expect(formScope.inst.doomroom).toBe(rooms.entities.Mdoomroom[1].id);
				flexdms.setExtraPropObj(propobj.prop,"relationui", {});
				
			});
			
			it("MStudent singel select: with dependency", function() {
				var relationUI={
						report:'10350',
						uitype:flexdms.query.relationui.uitype[0],
						params: ['number'],
						showselected:true
				};
				var student=flexdms.findType("Mstudent");
				var propobj=student.getProp("doomroom");
				flexdms.setExtraPropObj(propobj.prop,"relationui", relationUI);
				var uuid="8e2b6352-31ba-45c9-8865-23b90193b388";
				var preparePost= {
					'FxReportWrapper':{
						FxReport: reportbytypForMdoomroom.entities.FxReport[1]
					}	
				};
				preparePost.FxReportWrapper.FxReport.type='fxReport';
				preparePost.FxReportWrapper.FxReport.ParamValues.paramValues[0].values[0]=50;
				var respo={
						   "FxReportWrapper" : {
							      "uuid" : uuid,
							      "count" : 10,
							      FxReport:  reportbytypForMdoomroom.entities.FxReport[1],
						   }
				};
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/reportbyid/10350").respond({
					entities: {
						FxReport:[reportbytypForMdoomroom.entities.FxReport[1]],
						DefaultTypedQuery:[reportbytypForMdoomroom.entities.DefaultTypedQuery[1]]
					}
				}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Mstudent/10300").respond({Mstudent: {
					doomroom: rooms.entities.Mdoomroom[0].id
				}}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Mdoomroom/794").respond({
					Mdoomroom:rooms.entities.Mdoomroom[0]
				}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/fetchall/"+uuid).respond(rooms, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/TypedQuery/10400").respond({
					'DefaultTypedQuery': reportbytypForMdoomroom.entities.DefaultTypedQuery[1]
				}, jsonHeaders);
				
			
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(editFxTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				//---------html is ready here
				
				//no select since report is not executable
				expect(element.find(".prop.doomroom select").length).toBe(0);
				
				var formScope=element.find("form").scope();
				formScope.instform.number.$setViewValue("50");
				$topScope.$digest();
				//request
				$myInjector.get("$timeout").flush();
				$http.expectPOST(appctx.modelerprefix+"/reportrs/query/prepare", preparePost).respond(200, respo, jsonHeaders);
				$http.flush();
				$topScope.$digest();
				
				expect(element.find(".prop.doomroom select option").length).toBe(10);
				//select is initialized correctly from original inst value.
				expect(element.find(".prop.doomroom select").val()-0).toEqual(rooms.entities.Mdoomroom[0].id);
				
				
				//selection
				var select=element.find(".prop.doomroom select");
				select.controller("ngModel").$setViewValue(rooms.entities.Mdoomroom[1]);
				$topScope.$digest();
				//no select is shown for simple selection
				expect(element.find(".prop.doomroom div.selected a").length).toBe(0);
				expect(formScope.inst.doomroom).toBe(rooms.entities.Mdoomroom[1].id);
				flexdms.setExtraPropObj(propobj.prop,"relationui", {});
			});
			
			it("Monemany multi select: no dependency", function() {
				var relationUI={
						report:'10400',
						uitype:flexdms.query.relationui.uitype[0],
						params: [],
						showselected:true
				};
				var student=flexdms.findType("Mstudent");
				var propobj=student.getProp("OneManys");
				flexdms.setExtraPropObj(propobj.prop,"relationui", relationUI);
				var uuid="8e2b6352-31ba-45c9-8865-23b90193b388";
				var preparePost= {
					'FxReportWrapper':{
						FxReport: onemanyreport.entities.FxReport[0]
					}	
				};
				preparePost.FxReportWrapper.FxReport.type='fxReport';
				var respo={
						   "FxReportWrapper" : {
							      "uuid" : uuid,
							      "count" : 10,
							      FxReport:  reportbytypForMdoomroom.entities.FxReport[0],
						   }
				};
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/reportbyid/10400").respond({
					entities: {
						FxReport:[onemanyreport.entities.FxReport[0]],
						DefaultTypedQuery:[onemanyreport.entities.DefaultTypedQuery[0]]
					}
				}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Mstudent/10300").respond({Mstudent: {
					OneManys: [onemanys.entities.MOneMany[2].id, onemanys.entities.MOneMany[3].id]
				}}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/MOneMany/"+onemanys.entities.MOneMany[2].id).respond({
					MOneMany:onemanys.entities.MOneMany[2]
				}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/MOneMany/"+onemanys.entities.MOneMany[3].id).respond({
					MOneMany:onemanys.entities.MOneMany[3]
				}, jsonHeaders);
				$http.expectPOST(appctx.modelerprefix+"/reportrs/query/prepare", preparePost).respond(200, respo, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/fetchall/"+uuid).respond(onemanys, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/TypedQuery/10450").respond({
					'DefaultTypedQuery': onemanyreport.entities.DefaultTypedQuery[0]
				}, jsonHeaders);
				
			
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(editFxTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				//---------html is ready here
				
				expect(element.find(".prop.OneManys select option").length).toBe(7);
				//select value is initialized correctly with prop values
				expect(element.find(".prop.OneManys select").val()[0]-0).toBe(onemanys.entities.MOneMany[2].id);
				expect(element.find(".prop.OneManys select").val()[1]-0).toBe(onemanys.entities.MOneMany[3].id)
				
				var formScope=element.find("form").scope();
				//checked show select
				//selection
				var select=element.find(".prop.OneManys select");
				select.controller("ngModel").$setViewValue([onemanys.entities.MOneMany[0], onemanys.entities.MOneMany[1] ]);
				$topScope.$digest();
				//no select is shown for simple selection
				expect(element.find(".prop.OneManys div.selected a").length).toBe(2);
				expect(element.find(".prop.OneManys div.selected a").first().text()).toEqual(onemanys.entities.MOneMany[0].name);
				expect(formScope.inst.OneManys.length).toBe(2);
				expect(formScope.inst.OneManys[0]).toBe(onemanys.entities.MOneMany[0].id);
				expect(formScope.inst.OneManys[1]).toBe(onemanys.entities.MOneMany[1].id);
				flexdms.setExtraPropObj(propobj.prop,"relationui", {});
				
			});
			
			it("MStudent single selection inline table:depends on prop number", function() {
				var relationUI={
						report:'10350',
						uitype:flexdms.query.relationui.uitype[1],
						params: ['number'],
						showselected:true
				};
				var student=flexdms.findType("Mstudent");
				var propobj=student.getProp("doomroom");
				flexdms.setExtraPropObj(propobj.prop,"relationui", relationUI);
				var uuid="8e2b6352-31ba-45c9-8865-23b90193b388";
				var preparePost= {
					'FxReportWrapper':{
						FxReport: reportbytypForMdoomroom.entities.FxReport[1]
					}	
				};
				preparePost.FxReportWrapper.FxReport.type='fxReport';
				preparePost.FxReportWrapper.FxReport.ParamValues.paramValues[0].values[0]=50;
				var respo={
						   "FxReportWrapper" : {
							      "uuid" : uuid,
							      "count" : 10,
							      FxReport:  reportbytypForMdoomroom.entities.FxReport[1],
						   }
				};
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/reportbyid/10350").respond({
					entities: {
						FxReport:[reportbytypForMdoomroom.entities.FxReport[1]],
						DefaultTypedQuery:[reportbytypForMdoomroom.entities.DefaultTypedQuery[1]]
					}
				}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Mstudent/10300").respond({Mstudent: {
					doomroom: rooms.entities.Mdoomroom[0].id
				}}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Mdoomroom/794").respond({
					Mdoomroom:rooms.entities.Mdoomroom[0]
				}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/fetch/"+uuid+"/0/100").respond(rooms, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/TypedQuery/10400").respond({
					'DefaultTypedQuery': reportbytypForMdoomroom.entities.DefaultTypedQuery[1]
				}, jsonHeaders);
				
			
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(editFxTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				//---------html is ready here
				
				//no select since report is not executable
				expect(element.find(".prop.doomroom reportGrid").length).toBe(0);
				
				var formScope=element.find("form").scope();
				formScope.instform.number.$setViewValue("50");
				$topScope.$digest();
				//request
				$myInjector.get("$timeout").flush();
				$http.expectPOST(appctx.modelerprefix+"/reportrs/query/prepare", preparePost).respond(200, respo, jsonHeaders);
				$http.flush();
				$topScope.$digest();
				
				expect(element.find(".prop.doomroom .reportGrid .ngRow").length).toBe(10);
				expect(element.find(".prop.doomroom .reportGrid .ngRow.selected").length).toBe(1);
				
				//single-selection
				var grid=element.find(".prop.doomroom .reportGrid").scope().gridOptions
				grid.selectRow(1, true);
				$topScope.$digest();
				//no select is shown for simple selection
				expect(element.find(".prop.doomroom div.selected a").length).toBe(1);
				expect(element.find(".prop.doomroom div.selected a").text()).toEqual(rooms.entities.Mdoomroom[1].number+"");
				expect(formScope.inst.doomroom).toBe(rooms.entities.Mdoomroom[1].id);
				
				//change selection
				grid.selectRow(2, true);
				$topScope.$digest();
				//no select is shown for simple selection
				expect(element.find(".prop.doomroom div.selected a").length).toBe(1);
				expect(element.find(".prop.doomroom div.selected a").text()).toEqual(rooms.entities.Mdoomroom[2].number+"");
				expect(formScope.inst.doomroom).toBe(rooms.entities.Mdoomroom[2].id);
				
				flexdms.setExtraPropObj(propobj.prop,"relationui", {});
			});
			
			it("Monemany multi selection inline table: all one manys ", function() {
				var relationUI={
						report:'10400',
						uitype:flexdms.query.relationui.uitype[1],
						params: [],
						showselected:true
				};
				var student=flexdms.findType("Mstudent");
				var propobj=student.getProp("OneManys");
				flexdms.setExtraPropObj(propobj.prop,"relationui", relationUI);
				var uuid="8e2b6352-31ba-45c9-8865-23b90193b388";
				var preparePost= {
					'FxReportWrapper':{
						FxReport: onemanyreport.entities.FxReport[0]
					}	
				};
				preparePost.FxReportWrapper.FxReport.type='fxReport';
				var respo={
						   "FxReportWrapper" : {
							      "uuid" : uuid,
							      "count" : 10,
							      FxReport:  reportbytypForMdoomroom.entities.FxReport[0],
						   }
				};
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/reportbyid/10400").respond({
					entities: {
						FxReport:[onemanyreport.entities.FxReport[0]],
						DefaultTypedQuery:[onemanyreport.entities.DefaultTypedQuery[0]]
					}
				}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/Mstudent/10300").respond({Mstudent: {
					OneManys: [onemanys.entities.MOneMany[2].id, onemanys.entities.MOneMany[3].id]
				}}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/MOneMany/"+onemanys.entities.MOneMany[2].id).respond({
					MOneMany:onemanys.entities.MOneMany[2]
				}, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/MOneMany/"+onemanys.entities.MOneMany[3].id).respond({
					MOneMany:onemanys.entities.MOneMany[3]
				}, jsonHeaders);
				$http.expectPOST(appctx.modelerprefix+"/reportrs/query/prepare", preparePost).respond(200, respo, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/reportrs/query/fetch/"+uuid+"/0/100").respond(onemanys, jsonHeaders);
				$http.whenGET(appctx.modelerprefix+"/rs/inst/get/TypedQuery/10450").respond({
					'DefaultTypedQuery': onemanyreport.entities.DefaultTypedQuery[0]
				}, jsonHeaders);
				
			
				var $topScope=$rootScope.$new();
				var $compile=$myInjector.get("$compile")
				var element=$compile(editFxTemplate)($topScope);
				$topScope.$digest();
				$http.flush();
				//---------html is ready here
				
				expect(element.find(".prop.OneManys .reportGrid .ngRow").length).toBe(7);
				expect(element.find(".prop.OneManys .reportGrid .ngRow.selected").length).toBe(2);
				var formScope=element.find("form").scope();
				var grid=element.find(".prop.OneManys .reportGrid").scope().gridOptions
				grid.selectRow(0, true);
				grid.selectRow(1, true);
				grid.selectRow(2, false);
				grid.selectRow(3, false);
				$topScope.$digest();
				//no select is shown for simple selection
				expect(element.find(".prop.OneManys div.selected a").length).toBe(2);
				expect(element.find(".prop.OneManys div.selected a").first().text()).toEqual(onemanys.entities.MOneMany[0].name);
				expect(formScope.inst.OneManys.length).toBe(2);
				expect(formScope.inst.OneManys[0]).toBe(onemanys.entities.MOneMany[0].id);
				expect(formScope.inst.OneManys[1]).toBe(onemanys.entities.MOneMany[1].id);
				flexdms.setExtraPropObj(propobj.prop,"relationui", {});
				
			});
			
			
			//do not know how to test popup table.
		});