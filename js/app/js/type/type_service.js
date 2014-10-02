angular.module("flexdms.TypeResource", ['ngResource', "dateParser"]).factory("Type", ["$resource", function($resource) {
	
	
	function _transformTypes(meta){
		var types=[];
		if (angular.isDefined(meta.rsMsg)){
			//has error. error handling will come in
			return meta;
		}
		
		angular.forEach(meta['entity-mappings']['entity'], function(e){
    		var json=  {"entity-mappings" : {
		      "mapped-superclass" : [ ],
		      "entity" : [],
		      'embeddable':[]  
    			}
    		};
    		json['entity-mappings']['entity'].push(e);
    		types.push(new Type(json));
    	});
    	angular.forEach(meta['entity-mappings']['embeddable'], function(e){
    		var json=  {"entity-mappings" : {
		      "mapped-superclass" : [ ],
		      "entity" : [],
		      'embeddable':[]  
    			}
    		};
    		json['entity-mappings']['embeddable'].push(e);
    		types.push(new Type(json));
    	});
    
    	return types;
    	
	}
	function transformTypes(data, headers) {
    	var meta=angular.fromJson(data);
    	return _transformTypes(meta);
    }
	var Type = $resource(flexdms.typeserviceurl + "/:typename", null, {
		'checkName' : {
			method : 'GET',
			url : flexdms.typeserviceurl + '/checkname/:typename/:propname'
		},
		'savetype' : {
			method : 'POST',
			url : flexdms.typeserviceurl + "/savetype", 
		},
		'updatetype' : {
			method : 'POST',
			url : flexdms.typeserviceurl + "/updatetype",
		},
		'saveprop' : {
			method : 'POST',
			url : flexdms.typeserviceurl + "/saveprop",
		},
		'minorsaveprop' : {
			method : 'POST',
			url : flexdms.typeserviceurl + "/minorsaveprop",
		},
		'deleteprop' : {
			method : 'GET',
			url : flexdms.typeserviceurl + '/deleteprop/:typename/:propname',
			 transformResponse: transformTypes,
			 isArray: true
		},
		'deletetype' : {
			method : 'GET',
			url : flexdms.typeserviceurl + '/deletetype/:typename',
			 transformResponse: transformTypes,
			 isArray: true
		},
		'getsingle' : {
			method : 'GET',
			url : flexdms.typeserviceurl + "/getsingle/:typename"
		},
		'query': {
            method: 'GET',
            url : flexdms.typeserviceurl + "/meta",
            transformResponse: transformTypes,
            isArray: true 
        }
	});
	
	Type.prototype.getParentTypeName=function(){
		var p=this.getTypeJson()['@parent-class'];
		
		var pname=p.substring(p.lastIndexOf(".")+1);
		if (pname=='FleximsDynamicEntityImpl'){
			return null;
		}
		return pname;
		
	};
	Type.prototype.getParentType=function(){
		var pname=this.getParentTypeName();
		if (pname==null){
			return null;
		}
		return flexdms.findType(pname);
	};
	Type.prototype.isChildSelf=function(type){
		var ptype=type.getParentType();
		while (ptype!==null){
			if (ptype===this){
				return true;
			}
			ptype=type.getParentType();
		}
		return false;
	};
	
	Type.prototype.getChildren=function(){
		if (angular.isDefined(this.$children)){
			return this.$children;
		}
		
		function mapSize(map){
			var count=0;
			for (var tname in map){
				count++;
			}
			newsize=count;
		}
		
		var ret={};
		ret[this.getName()]=this;
		var oldsize=1;
		var newsize=0;
		do {
			oldsize=mapSize(ret);
			for (var i=0; i<flexdms.types.length; i++){
				var t=flexdms.types[i];
				var pname=t.getParentTypeName();
				if (pname!=null && ret[pname]){
					ret[t.getName()]=t;
				}
			}
			newsize=mapSize(ret);
		} while (oldsize!=newsize);
		
		var ret1=[];
		for (var tname in ret){
			if (tname!=this.getName()){
				ret1.push(ret[tname]);
			}
		}
		
		this.$children=ret1;
		return ret1;
	};
	/*
	 * Call from parent to curren type
	 * for the function fn.
	 * fn has one optional argument result.
	 * context for function is the current type.
	 */
	Type.prototype._traceParent=function(result, fn){
		var ptype=this.getParentType();
		if (ptype!=null){
			ptype._traceParent(result, fn);
		}
		fn.apply(this, [result]);
	};
	
	
	Type.prototype.initProps=function(){
		if (angular.isDefined(this.$props)){
			return ;
		}
		this._traceParent(null, function(result){
			var props={};
			var type=this;
			for(var ctype in type.getAttributes()) {
				var ctypes=type.getAttributes()[ctype];
				for (var i=0; i<ctypes.length; i++){
					var propObj=new flexdms.Property(ctypes[i]);
					propObj.type=type;
					propObj.collectionType=ctype;
					props[propObj.getName()]=propObj;
					propObj.getTypeObject();
				}
			}
			this.$props=props;
		});
		
	};
	
	Type.prototype.cleanCache=function(){
		delete(this.$json);
		delete(this.$editProps);
		delete(this.$viewProps);
		delete(this.$props);
		delete(this.$children);
	};
	Type.prototype.getTypeJson=function(){
		if (this.$json!=null){
			return this.$json;
		}
		if (this['entity-mappings']['entity'].length==1){
			 this.$json=this['entity-mappings']['entity'][0];
		} else {
			this.$json=this['entity-mappings']['embeddable'][0];
		}
		return this.$json;
		
	};
	Type.prototype.getName=function(){
		return this.getTypeJson()["@class"];
	};
	Type.prototype.getDescription=function(){
		var d=this.getTypeJson()['description'];
		if(d){
			return d;
		}
		return this.getName();
	};
	
	
	Type.prototype.isEntity=function(){
		return this['entity-mappings']['entity'].length==1;
	};
	Type.prototype.isAbstract=function(){
		return this.isExtraProp("abstract", false);
	};
	Type.prototype.isSystem=function(){
		return this.isExtraProp("system", false);
	};
	Type.prototype.isExtraProp=function(propName, defaultValue){
		if (flexdms.getExtraProp(this.getTypeJson(), propName)){
			return flexdms.parseTrueFalse(flexdms.getExtraProp(this.getTypeJson(), propName));
		}
		return defaultValue;
	};
	Type.prototype.getExtraProp=function(propName){
		return flexdms.getExtraProp(this.getTypeJson(), propName);
	};
	
	Type.prototype.switchEntityEmbedded=function(){
		var json=this.getTypeJson();
		if(this['entity-mappings']['entity'].length==1){
			this['entity-mappings']['entity'].length=0;
			this['entity-mappings']['embeddable'].push(json);
			//if embedded, the parent type has to be DynamicEntity
			json['@parent-class']='com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl';
		} else {
			this['entity-mappings']['embeddable'].length=0;
			this['entity-mappings']['entity'].push(json);
		}
	};
	
	
	
	Type.prototype.getAttributes=function(){
		return this.getTypeJson()['attributes'];
	};
	Type.prototype.getProp=function(name){
		this.initProps();
		var propobj=this.$props[name];
		if (propobj!=null){
			return propobj;
		}
		var ptype=this.getParentType();
		if (ptype!=null){
			return ptype.getProp(name);
		}
		
		var i=name.indexOf(".");
		if (i==-1){
			return undefined;
		}
		
		var firstname=name.substring(0, i);
		var prop=this.getProp(firstname);
		if (prop!=null){
			var relatedType=flexdms.findType(prop.getTypeObject().value);
			return relatedType.getProp(name.substring(i+1));
		}
		
		return undefined;
	};
	
	
	Type.prototype.getPropNames=function(){
		var names=[];
		this.initProps();
		this._traceParent(names, function(result){
			for(var name in this.$props){
				names.push(name);
			}
		});
		return names;
	};
	

	Type.prototype.getProps=function(){
		this.initProps();
		var props=new Array();
		this._traceParent(props, function(result){
			for(var propname in this.$props){
				props.push(this.$props[propname]);
			}
		});
		
		return props;
	};
	Type.prototype.getSelfProps=function(){
		this.initProps();
		var props=new Array();
		for(var propname in this.$props){
			props.push(this.$props[propname]);
		}
		
		return props;
	};
	Type.prototype.filterProps=function(func){
		var ret=new Array();
		angular.forEach(this.getProps(), function(prop){
			if (func.call(prop)){
				ret.push(prop);
			}
		});
		return ret;
	};
	
	Type.prototype.getEditProps=function(){
		if (angular.isDefined(this.$editProps)){
			return this.$editProps;
		}
		var editProps=this.filterProps(function(){
			if (this.isIdOrVersion()){
				return false;
			}
			if (this.isRelation() && !this.isRelationOwner()){
				return false;
			}
			if (this.isEditable()){
				return true;
			}
			return false;
		});
		this.$editProps=editProps;
		return this.$editProps;
	};
	Type.prototype.getViewProps=function(){
		if (angular.isDefined(this.$viewProps)){
			return this.$viewProps;
		}
		this.initProps();
		var viewProps=this.filterProps(function(){
			if (this.isIdOrVersion()){
				return false;
			}
			if (this.isViewable()){
				return true;
			}
			return false;
		});
		this.$viewProps=viewProps;
		return this.$viewProps;
	};
	
	Type.prototype.hasClientFile=function(){
		var props=this.getProps();
		for (var i=0; i<props.length; i++){
			if (props[i].isFileUpload()){
				return true;
			}
			if (props[i].isEmbedded() || props[i].isEmbeddedElementCollection()){
				var embeddedtype=flexdms.findType(props[i].getTypeObject().value);
				if (embeddedtype.hasClientFile()){
					return true;
				}
			}
		}
		return false;
	};
	Type.prototype.hasServerFile=function(){
		var props=this.getProps();
		for (var i=0; i<props.length; i++){
			if (props[i].getTypeObject().isFileType() && !props[i].isFileUpload()){
				return true;
			}
			if (props[i].isEmbedded() || props[i].isEmbeddedElementCollection()){
				var embeddedtype=flexdms.findType(props[i].getTypeObject().value);
				if (embeddedtype.hasServerFile()){
					return true;
				}
			}
		}
		return false;
	};
	
	Type.prototype.initInstance=function(inst1, replace){
		this.initProps();
		if (!angular.isDefined(inst1[flexdms.insttype])){
			inst1[flexdms.insttype]=this.getName();
		}
		this._traceParent(inst1, function(inst){
			for(var propname in this.$props) {
				var propObj=this.$props[propname];
				if (propObj.getDefaultValue()!=null){
					if (replace || !angular.isDefined(inst[propname])){
						var d=propObj.getConvertedDefault();
						d=propObj.pickValueFromAlloweds(d);
						inst[propname]=d;
						continue;
					}
				}
				
				if (!angular.isDefined(inst[propname]) && propObj.isAutoGenerate()){
					inst[propname]=propObj.generateValue();
					continue;
				}
				
				if (!angular.isDefined(inst[propname])){
					if (propObj.getTypeObject().isTrueFalse()){
						//we give default value to boolean to false
						//we can not use null
						inst[propname]=false;
					} else {
						inst[propname]=null;
					}
					
				}
				
			}
		});
		
	};
	
	Type.prototype.isExtensible=function(){
		return this.getProp("fxExtraProp")!=null;
	};
	
	/**
	 * 
	 * @param skipMappedBy whether to type reached by mapped by relation
	 * @returns an object indexed by typename: all types dierctly reached by current type.
	 */
	Type.prototype.reachout=function(skipMappedBy){
		var type=this;
		var related={};
		var props=type.getSelfProps();
		for (var j=0; j<props.length; j++){
			var propobj=props[j];
			var proptype=propobj.getTypeObject();
			if (!proptype.isEmbedded() && !proptype.isRelation()){
				continue;
			}
			if (propobj.getMappedBy() && skipMappedBy){
				continue;
			}
			related[proptype.value]=flexdms.findType(proptype.value);
		}
		return related;
	};
	
	/**
	 * 
	 * @param skipMappedBy whether to type reached by mapped by relation
	 * @returns an object indexed by typename: all types that refer to this type directly.
	 */
	Type.prototype.reachin=function(skipMappedBy){
		var t=this;
		var related={};
		for (var i=0; i<flexdms.types.length; i++){
			var type=flexdms.types[i];
			var props=type.getSelfProps();
			for (var j=0; j<props.length; j++){
				var propobj=props[j];
				var proptype=propobj.getTypeObject();
				if (!proptype.isEmbedded() && !proptype.isRelation()){
					continue;
				}
				if (propobj.getMappedBy() && skipMappedBy){
					continue;
				}
				if (proptype.value==t.getName()){
					related[type.getName()]=type;
				}
			}
		}
		return related;
	};
	
	/**
	 * 
	 * @returns an object indexed by typename: all ancenstors and descendents.
	 */
	Type.prototype.upanddown=function(){
		var t=this;
		var related={};
		var childs=t.getChildren();
		for (var i=0; i<childs.length; i++){
			related[childs[i].getName()]=childs[i];
		}
		var p=t.getParentType();
		while(p!=null){
			related[p.getName()]=p;
			p=p.getParentType();
		}
		return related;
	};
	
	
	function _cluster(type, processeds){
		var currentname=type.getName();
		
		var newqueue={};
		var line=type.upanddown();
		for (var tname in line){
			if (tname!=currentname && !processeds[tname]){
				newqueue[tname]=line[tname];
			}
		}
		
		var outs=type.reachout();
		for (var tname in outs){
			if (tname!=currentname && !processeds[tname]){
				newqueue[tname]=outs[tname];
			}
		}
		var ins=type.reachin();
		for (var tname in ins){
			if (tname!=currentname && !processeds[tname]){
				newqueue[tname]=ins[tname];
			}
		}
		processeds[currentname]=type;
		
		for (var newtname in newqueue){
			_cluster(newqueue[newtname], processeds);
		}
	}
	
	/**
	 * 
	 * @returns all types that can are related to current type
	 * 1. ancestors, descentdants, self
	 * 2. reached by self directly or indirectly.
	 * 3. reached to self directly ot indirectly.
	 */
	Type.prototype.cluster=function(){
		var processeds={};
		processeds[this.getName()]=this;
		_cluster(this, processeds);
		return processeds;
	};
	
	
	
	//load types.
	Type.processtypemeta=function($q){
		 var types=_transformTypes(flexdms.typemeta);
		 var defer=$q.defer();
		 defer.resolve(types);
		 types.$promise=defer.promise;
		 types.$resolved=true;
		 Type.assignnewmeta(types);
	};
	
	Type.assignnewmeta=function(data){
		flexdms.types=data;
		flexdms.typemap={};
		for(var i=0; i<flexdms.types.length; i++){
			flexdms.typemap[flexdms.types[i].getName()]=flexdms.types[i];
		}
	};

	return Type;
}]).controller("typesCtrl", function($scope, Type){
	$scope.types=flexdms.types;
	$scope.byName=function(type){
	    	return type.getName();
	 };
	$scope.isEntity=function(type){
		return type.isEntity();
	};
	$scope.isSystem=function(type){
		return type.isSystem();
	};
	$scope.showInAdd=function(type){
		var showAdd=type.isExtraProp("showInAdd", true);
		if (!showAdd){
			return false;
		}
		return type.isEntity()  &&!type.isAbstract() && !type.isSystem();
	};
	$scope.showInAllReport=function(type){
		var showInAllReport=type.isExtraProp("showInAllReport", true);
		if (!showInAllReport){
			return false;
		}
		return type.isEntity()  && !type.isSystem();
	};
	
}).run(["Type", "$q", "$filter",  "$dateParser", function(Type, $q, $filter, $dateParser){
	//hjave filter available for all type
	flexdms.filter=$filter;
	flexdms.$dateParser=$dateParser;
	Type.processtypemeta($q);
}]);

angular.module("flexdms.securityInfo", []).service("securityInfoService", function(){
	

		this.isAdmin=function(username){
			var deferred=$q.defer();
			deferred.resolve(false);
			return deferred.promise;
		};
		
		this.getPermissions=function(username, type, id){
			var deferred=$q.defer();
			deferred.resolve({});
			return deferred.promise;
		};
		
		this.getTypePermissions=function(username, type){
			var deferred=$q.defer();
			deferred.resolve({});
			return deferred.promise;
		};
		
		this.setFxUser=function(user){
			flexdms.fxuser=user;
		};
		this.getFxUser=function(){
			return flexdms.fxuser;
		};
		
		this.logout=function(){
			//donithing.
		};
});



flexdms.cacheTypeMeta=function(meta){
	flexdms.typemeta=meta;
};
flexdms.findType=function(typename){
	return flexdms.typemap[typename];
};
flexdms.reloadTypes=function(Type){
	flexdms.types=null;
	flexdms.types=Type.query();
	flexdms.types.$promise.then(function(){
		flexdms.typemap={};
		for(var i=0; i<flexdms.types.length; i++){
			flexdms.typemap[flexdms.types[i].getName()]=flexdms.types[i];
		}
	});
	return flexdms.types;
};

