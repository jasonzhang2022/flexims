flexdms.isJsonHttpReturn=function(headers){
	if (headers('Content-Length')!=null && headers('Content-Length')==0 ){
		return false;
	}
	if (headers('Content-Type')!=null && headers('Content-Type').indexOf('application/json')===0){
		return true;
	}
	return false;
};

angular.module("flexdms.InstResource", ['ngResource']).factory("Inst", ["$resource", "$q" , "$http", function($resource, $q, $http) {
	
	function wrapResourceObject(data){
		
		var instobj={};
		
		var typename=data[flexdms.insttype];
    	//TODO check relation field.
    	for (var propname in data){
    		if (propname[0]!='$') {
    			instobj[propname]=data[propname];
    		}
    	}
    	var ret={};
    	ret[typename]=instobj;
    	//very important, a string is expected.
    	return angular.toJson(ret);
	}
	
	function processEmbeddedChildren(typename, inst){
		var props=flexdms.findType(typename).getProps();
		for (var i=0; i<props.length; i++){
			if (!props[i].getTypeObject().isEmbedded()){
				continue;
			}
			//single embedded
			if (props[i].isEmbedded() && inst[props[i].getName()]){
				var propinst=Inst.newInst(props[i].getTypeObject().value, inst[props[i].getName()]);
				//child can always trace back to parent.
				propinst[flexdms.parentinst]=inst;
				inst[props[i].getName()]=propinst;
				
				continue;
			} 
			//element collection right now
			if (props[i].isElementCollection() && inst[props[i].getName()]){
				for (var j=0; j<inst[props[i].getName()].length; j++){
					var propinst=Inst.newInst(props[i].getTypeObject().value, inst[props[i].getName()][j]);
					propinst[flexdms.parentinst]=inst;
					inst[props[i].getName()][j]=propinst;
				
				}
			}
		}
	}
	function unwrapObject(data, headers){
		if (!flexdms.isJsonHttpReturn(headers)){
			return data;
		}
		var inst=angular.fromJson(data);
		var typename=null;
		//only expect one name.
		for(var t in inst){
			typename=t;
		}
		//do not handle the error message
		if (typename=='rsMsg'){
			return inst;
		}
		
		inst[typename][flexdms.insttype]=typename;
		inst=inst[typename];
		
		processEmbeddedChildren(typename, inst);
		return inst;
		
	}
	
	var Inst = $resource(flexdms.instserviceurl + "/:typename", null, {
		get: {
			url:flexdms.instserviceurl+"/get/:typename/:id",
			method : 'GET', 
	         transformResponse: unwrapObject,
			isArray:false
		},
		refresh:  {
			url:flexdms.instserviceurl+"/get/:typename/:id",
			method : 'GET', 
			params :{
				refresh: 'true'
			},
	         transformResponse: unwrapObject,
			isArray:false
		},
		del: {
			url:flexdms.instserviceurl+"/delete/:typename/:id",
			method : 'GET', 
	         transformResponse: unwrapObject,
			isArray:false
		},
		'delete': {
			url:flexdms.instserviceurl+"/delete/:typename/:id",
			method : 'DELETE', 
	         transformResponse: unwrapObject,
			isArray:false
		},
		remove: {
			url:flexdms.instserviceurl+"/delete/:typename/:id",
			method : 'DELETE', 
	         transformResponse: unwrapObject,
			isArray:false
		},
		save: {
			url:flexdms.instserviceurl+"/save",
			method : 'POST', 
			transformRequest:wrapResourceObject,
			transformResponse: unwrapObject,
			isArray:false
		}
		
	});
	
	Inst.saveBatch=function(data){
		return $http.post(flexdms.instserviceurl+"/savebatch", data);
	};
	Inst.prototype.getSummary=function(){
		var typename=this[flexdms.insttype];
		
		if (!angular.isDefined(typename)){
			return this.id;
		}
		var inst=this;
		var props=flexdms.findType(typename).getProps();
		var properties=new Array();
		angular.forEach(props, function(prop){
			if (prop.isSummaryProp()){
				var propvalue=inst[prop.getName()];
				if (angular.isDefined(propvalue) && propvalue!=null){
					properties.push(propvalue);
				}
			}
		});
		if (properties.length==0){
			properties.push(this.id);
		}
		return properties.join(" ");
	};
	Inst.prototype.getTypeName=function(){
		return this[flexdms.insttype];
	};
	
	Inst.newInst=function(typename, value){
		var inst=null;
		if (angular.isDefined(value) && value!==null){
			inst=new Inst(value);
			//give a typename
			inst[flexdms.insttype]=typename;
		} else{
			inst=new Inst();
			
			//give a typename
			inst[flexdms.insttype]=typename;
			//initialize default 
			flexdms.findType(typename).initInstance(inst, true);
		}
		
		//make it looks like resource
		var defer=$q.defer();
		defer.resolve(inst);
		inst.$promise=defer.promise;
		inst.$resolved=true;
		
		processEmbeddedChildren(typename, inst);
		return inst;
	};
	return Inst;
}]).service("instCache", function(Inst){
	this.insts={};
	this.getInst=function(type, id){
		var key=type+":"+id;
		if (angular.isDefined(this.insts[key])){
			return 	this.insts[key];
		}
		var inst=Inst.get({typename:type, id:id});
		inst.id=id-0; //make id available before server returns
		this.insts[key]=inst;
		
		var cache=this;
		//remove instance from cache if error.
		inst.$promise.then(null, function(){
			delete(cache.insts[key]);
		});
		return 	this.insts[key];
	};
	this.deleteInst=function(type, id){
		var key=type+":"+id;
		delete(this.insts[key]);
	};
	this.updateInst=function(type, inst){
		var key=type+":"+inst.id;
		this.insts[key]=inst;
	};
	this.refreshInst=function(type, id){
		var key=type+":"+id;
		delete(this.insts[key]);
		this.insts[key]=Inst.refresh({typename:type, id:id});
		return 	this.insts[key];
	};
});

flexdms.copyFromStringValued=function(destInst, srcInst, Inst){
	function initEmbedded(prop) {
		//var embeddedType=flexdms.findType(prop.getTypeObject().value);
		var embeddedInst=Inst.newInst(prop.getTypeObject().value);
		//embeddedType.initInstance(embeddedInst);
		return embeddedInst;
	}
	
	var type=flexdms.findType(destInst[flexdms.insttype]);
	var thisinst=destInst;
	angular.forEach(type.getProps(), function(prop){
		
		if (!angular.isDefined(srcInst[prop.getName()]) || srcInst[prop.getName()]==null){
			return;
		}
		

		if (prop.isCollection()){
			if (prop.isRelation()){
				var vs=[];
				angular.forEach(srcInst[prop.getName()].split(","), function(v){
					if (v){
						vs.push(v-0);
					}
				});
				thisinst[prop.getName()]=vs;
			} else if (prop.getTypeObject().isEmbedded()){
				//intialize an array if needed
				if (!angular.isDefined(thisinst[prop.getName()]) || thisinst[prop.getName()]==null){
					thisinst[prop.getName()]=[];
				}
				var vs=thisinst[prop.getName()];
				
				for(var i=0; i<srcInst[prop.getName()].length; i++){
					var stringInst=srcInst[prop.getName()][i];
					if (i>=vs.length){
						vs.push(initEmbedded(prop));
					}
					var embeddedInst=vs[i];
					if (! embeddedInst){
						 embeddedInst=initEmbedded(prop);
						vs[i]= embeddedInst;
					}
					if (stringInst){
						flexdms.copyFromStringValued( embeddedInst, stringInst, Inst);
					}
				}
			} else {
				var vs=[];
				angular.forEach(srcInst[prop.getName()].split(","), function(v){
					if (v){
						vs.push(prop.getTypeObject().parse(v));
					}
				});
				thisinst[prop.getName()]=vs;
			}
		} else{
			//we expect one value
			if (prop.isRelation()){
				thisinst[prop.getName()]=srcInst[prop.getName()]-0;
			} else if (prop.getTypeObject().isEmbedded()){
				if (!angular.isDefined(thisinst[prop.getName()]) ||thisinst[prop.getName()]===null){
					thisinst[prop.getName()]=initEmbedded(prop);
				}
				var embeddedInst=thisinst[prop.getName()];
				flexdms.copyFromStringValued(embeddedInst,srcInst[prop.getName()], Inst);
			} else {
				thisinst[prop.getName()]=prop.getTypeObject().parse(srcInst[prop.getName()]);
			}
			
		}
		
	});
};
flexdms.instToFlatObject=function(srcInst, outputObject, prefix){
	var type=flexdms.findType(srcInst[flexdms.insttype]);
	angular.forEach(type.getProps(), function(prop){
		
		var propvalue=srcInst[prop.getName()];
		if (!angular.isDefined(propvalue)|| propvalue==null){
			return;
		}
		var key=prefix?prefix+"."+prop.getName():prop.getName();
		if (prop.isCollection()){
			if (prop.isRelation()){
				outputObject[key]=propvalue.join(",");
			} else if (prop.getTypeObject().isEmbedded()){
				for (var i=0; i<propvalue.length; i++){
					var embeddedInst=propvalue[i];
					flexdms.copyFromStringValued( embeddedInst, key+"."+i);
				}
			} else {
				//multiple string issue?
				outputObject[key]=srcInst[prop.getName()].join(",");
			}
		} else{
			//we expect one value
			if (prop.isRelation()){
				outputObject[key]=propvalue;
			} else if (prop.getTypeObject().isEmbedded()){
				flexdms.toStringValued(propvalue, key);
			} else {
				outputObject[key]=propvalue;
			}
			
		}
	});
};



