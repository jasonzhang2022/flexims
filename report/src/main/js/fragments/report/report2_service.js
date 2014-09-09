flexdms.reportserviceurl=appctx.modelerprefix+"/reportrs/query";
angular.module("flexdms.report").service("reportService", function($http, Inst, instCache){

	function processEmbeddedAndRelationChildren(typename, inst){
		var props=flexdms.findType(typename).getProps();
		for (var i=0; i<props.length; i++){
			if (!props[i].getTypeObject().isEmbedded() && !props[i].getTypeObject().isRelation() ){
				continue;
			}
			if (!angular.isDefined(inst[props[i].getName()])){
				continue;
			}
			//single embedded
			if (props[i].isEmbedded()){
				var propinst=Inst.newInst(props[i].getTypeObject().value, inst[props[i].getName()]);
				//child can always trace back to parent.
				propinst[flexdms.parentinst]=inst;
				inst[props[i].getName()]=propinst;
				
				continue;
			} 
			//element collection right now
			if (props[i].isElementCollection() ){
				for (var j=0; j<inst[props[i].getName()].length; j++){
					var propinst=Inst.newInst(props[i].getTypeObject().value, inst[props[i].getName()][j]);
					propinst[flexdms.parentinst]=inst;
					inst[props[i].getName()][j]=propinst;
				
				}
				continue;
			}
			
			//relation of collection
			if ( props[i].isCollection() ){
				for (var j=0; j<inst[props[i].getName()].length; j++){
					var propinst=Inst.newInst(props[i].getTypeObject().value, inst[props[i].getName()][j]);
					inst[props[i].getName()][j]=propinst;
				
				}
				continue;
			} 
			
			//relation of single 
			{
				var propinst=Inst.newInst(props[i].getTypeObject().value, inst[props[i].getName()]);
				inst[props[i].getName()]=propinst;
			}
			
		}
	}
	function createDataInsts(data){
		var ret=angular.fromJson(data);
		var insts=new Array();
		for (var typename in ret.entities){
			angular.forEach(ret.entities[typename], function(instJson){
				var inst=Inst.newInst(typename, instJson);
				processEmbeddedAndRelationChildren(typename, inst);
				insts.push(inst);
			
			});
		}
		return insts;
	}
	
	function createInsts(data, typename){
		var json=angular.fromJson(data);
		var insts=new Array();
		angular.forEach(json.entities[typename], function(queryInst){
			var inst=Inst.newInst(typename, queryInst);
			instCache.updateInst(typename, inst);
			insts.push(inst);
		});
		
		return insts;
	}
	
	function createInst(data, typename){
		var json=angular.fromJson(data);
		var inst=Inst.newInst(typename, json[typename]);
		instCache.updateInst(typename, inst);
		return inst
	}
	
	function createMultiReports(data) {
		var json=angular.fromJson(data);
		var insts=new Array();
		for (var typename in json.entities){
			angular.forEach(json.entities[typename], function(queryInst){
				var inst=Inst.newInst(typename, queryInst);
				instCache.updateInst(typename, inst);
				if (typename==='FxReport'){
					insts.push(inst);
				}
			});
		}
		angular.forEach(insts, function(report){
			report['$queryobj']=new flexdms.query.defaulttypedquery(instCache.getInst("DefaultTypedQuery", report.Query));
		});
		
		return insts;
	}
	function createSingleReport(data) {
		
		var report=null;
		var json=angular.fromJson(data);
		var insts=new Array();
		for (var typename in json.entities){
			angular.forEach(json.entities[typename], function(instJson){
				var inst=Inst.newInst(typename, instJson);
				instCache.updateInst(typename, inst);
				if (typename==='FxReport'){
					report=inst;
				}
			});
		}
		if (report!==null){
			report['$queryobj']=new flexdms.query.defaulttypedquery(instCache.getInst("DefaultTypedQuery", report.Query));
		}
		return report;
	}
	this.getQueryByName=function(queryname){
		return $http.get(flexdms.reportserviceurl+"/querybyname/"+queryname,  {transformResponse:  function(data, headers){
			if(!flexdms.isJsonHttpReturn(headers)){
				return data;
			}
			return createInst(data, "DefaultTypedQuery");
		}}).then(function(xhr){
			return xhr.data; //this is the report resource object
		});
	};
	this.getQueriesByType=function(typename){
		return $http.get(flexdms.reportserviceurl+"/querybytype/"+typename,  {transformResponse:  function(data, headers){
			if(!flexdms.isJsonHttpReturn(headers)){
				return data;
			}
			return createInsts(data, "DefaultTypedQuery");
		}}).then(function(xhr){
			return xhr.data; //this is the report resource object
		});
	};
	this.getReportsByType=function(typename){
		return $http.get(flexdms.reportserviceurl+"/reportbytype/"+typename,  {transformResponse:  function(data, headers){
			if(!flexdms.isJsonHttpReturn(headers)){
				return data;
			}
			return createMultiReports(data);
		}}).then(function(xhr){
			return xhr.data; //this is the report resource object
		});
	};
	
	//three ways to retrieve a single report.
	this.getReportByName=function(reportname){
		return $http.get(flexdms.reportserviceurl+"/reportbyname/"+reportname,  {transformResponse:  function(data, headers){
			if(!flexdms.isJsonHttpReturn(headers)){
				return data;
			}
			return createSingleReport(data);
		}}).then(function(xhr){
			return xhr.data; //this is the report resource object
		});
	};
	/**
	 * Different from getInst
	 * we make sure report and all asspociated queries are loaded: load the report instance amd alltracedown.
	 */
	this.getReportById=function(reportId){
		return $http.get(flexdms.reportserviceurl+"/reportbyid/"+reportId,  {transformResponse:  function(data, headers){
			if(!flexdms.isJsonHttpReturn(headers)){
				return data;
			}
			return createSingleReport(data);
		}}).then(function(xhr){
			return xhr.data; //this is the report resource object
		});
	};
	//a report that list all entity in one type
	this.getAllReport=function(typename){
		return $http.get(flexdms.reportserviceurl+"/allreportbytype/"+typename, {transformResponse:  function(data, headers){
			if(!flexdms.isJsonHttpReturn(headers)){
				return data;
			}
			return createSingleReport(data);
		}}).then(function(xhr){
			return xhr.data; //this is the report resource object
		});
	};
	this.getAllTraceDown=function(queryid){
		return $http.get(flexdms.reportserviceurl+"/alltracedown/"+queryid,  {transformResponse:  function(data, headers){
			if(!flexdms.isJsonHttpReturn(headers)){
				return data;
			}
			return createInsts(data, "DefaultTypedQuery");
		}}).then(function(xhr){
			return xhr.data; //this is the report resource object
		});
	};
	
	
	this.prepare=function(reportWrapper){
        var fxReportWrapper=angular.copy(reportWrapper);
        /*
		fxReportWrapper.entity=fxReportWrapper.FxReport;
		fxReportWrapper.entity.type="fxReport";
		delete(fxReportWrapper.FxReport);
        */
	
		return $http.post(flexdms.reportserviceurl+"/prepare",{"FxReportWrapper":fxReportWrapper}, {
			transformResponse: function(data, headers){
				if(!flexdms.isJsonHttpReturn(headers)){
					return data;
				}
			
				//TODO error handling
				var fxReportWrapper=angular.fromJson(data).FxReportWrapper;
				/*
				fxReportWrapper.FxReport=fxReportWrapper.entity;
				delete(fxReportWrapper.entity);
				*/
				return fxReportWrapper;
			}
		}).then(function(xhr){
			return xhr.data; //this is the report resource object
		});
		
	};
	this.destory=function(reportWrapper){
		return $http.delete(flexdms.reportserviceurl+"/destory/"+ reportWrapper.uuid, {fxAlertNoContent:false});
	};
	this.fetchAll=function(reportWrapper){
		return $http.get(flexdms.reportserviceurl+"/fetchall/"+reportWrapper.uuid, {transformResponse:  createDataInsts}).then(function(xhr){
			return xhr.data; //this is the report resource object
		});
	};
	this.fetchPartial=function(reportWrapper, offset, len){
		return $http.get(flexdms.reportserviceurl+"/fetch/"+reportWrapper.uuid+"/"+offset+"/"+len,  {transformResponse:  createDataInsts}).then(function(xhr){
			return xhr.data; //this is the report resource object
		});
	};

});

