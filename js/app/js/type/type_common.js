/**
 * Declar namespace;
 * have global constants, used everywhere
 */
if (typeof(appctx)==='undefined'){
	appctx={
		modelerprefix:"/flexims",
		formprefix:"/flexims",
	};
}
flexdms = {

		
		
	
	
	
	iso8601Format : "yyyy-MM-ddTHH:mm:ssZ",
	alert:function(error){
		if (typeof(error)=='string'){
			alert(error);
			return;
		}
		alert(error.msg);
	},
	EMBEDED_TYPE_IDX:-1,
	RELATION_TYPE_IDX:-2,
	typeserviceurl:appctx.modelerprefix+"/rs/type",
	instserviceurl:appctx.modelerprefix+"/rs/inst",
	utilserviceurl:appctx.modelerprefix+"/rs/util",
	insttype:"$_fxtype",
	parentinst:"$_fxparentinst",
	typemodules:['ui.router', 'ui.bootstrap', 'flexdms.common.error', 'flexdms.common.fxAlert', 'flexdms.common', 'flexdms.serverConfig',
	             'ngResource', "dateParser",
	             "flexdms.TypeResource", "flexdms.securityInfo", 
	             "flexdms.InstResource", 'ngAnimate'],
	 config: {
		 dateFormat : "yyyy/MM/dd",
		 dateTimeFormat : "yyyy/MM/dd HH:mm:ss",
		 timeFormat : "HH:mm:ss",
			
		 getConfig: function(){
				var obj=this;
				for (var i=0; i<arguments.length;i++){
					var propname=arguments[i];
					if (angular.isDefined(obj[propname])){
						obj=obj[propname];
					} else{
						return null;
					}
				}
				return obj;
			},
			addConfig:function(){
				var obj=this;
				for (var i=0; i<arguments.length-2;i++){
					var propname=arguments[i];
					if (angular.isDefined(obj[propname])){
						obj=obj[propname];
					} else{
						obj[propname]={};
						obj=obj[propname];
					}
				}
				obj[arguments[arguments.length-2]]=arguments[arguments.length-1];
				
			},
	 }
};
flexdms.INTEGER_REGEXP = /^\-?\d+$/;
flexdms.URL_REGEXP = /^(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?$/;
flexdms.EMAIL_REGEXP = /^[a-z0-9!#$%&'*+/=?^_`{|}~.-]+@[a-z0-9-]+(\.[a-z0-9-]+)*$/i;
flexdms.NUMBER_REGEXP = /^\s*(\-|\+)?(\d+|(\d*(\.\d*)))\s*$/;



//flexdms class.
flexdms.BasicPropType = function(attrs) {
	for ( var p in attrs) {
		this[p] = attrs[p];
	}
};

flexdms.parseTrueFalse=function(value){
	if (typeof(value)=='undefined'){
		return false;
	}
	if (value==null){
		return fasle;
	}
	if (typeof(value)=='boolean'){
		return value;
	}
	if (value.toUpperCase() == 'T' || value.toUpperCase() == 'Y' || value.toUpperCase() == 'TRUE'
		|| value.toUpperCase() == 'YES' || value.toUpperCase() == 'ON') {
		return true;
	}
	return false;
};


//-----------------important functions

flexdms.BasicPropType.prototype.canHaveMultiple = function() {
	if (this.isNumber()) {
		return true;
	}
	if (this.isStringType() && (this.idx != 1 && this.idx != 2 && this.idx != 10)) {
		return true;
	}
	//do not allow this right now.
	//		if (this.isDateTime()){
	//			return true;
	//		}
	return false;
};

flexdms.BasicPropType.prototype.canUnique = function() {
	if (this.isNumber()) {
		return true;
	}
	if (this.isStringType() && (this.idx != 1 && this.idx != 2 && this.idx != 10)) {
		return true;
	}
	if (this.isDateTime()) {
		return true;
	}
	return false;
};

flexdms.BasicPropType.prototype.generateDefault = function(config) {
	if (this.isDateTime()) {
		return new Date();
	}
	return null;
};
flexdms.BasicPropType.prototype.parse = function(value) {
	//do not need parse for typed value
	if (!angular.isString(value)){
		return value;
	}
	if (this.isNumber()|| this.isRelation()) {
		var v=value - 0;
		if (isNaN(v)){
			return null;
		}
		return v;
	}
	if (this.isTrueFalse()) {
		return flexdms.parseTrueFalse(value);
	}
	if (this.isDateTime()) {
		if (value instanceof Date){
			return value;
		}
		
		if (!angular.isNumber(value) && isNaN(Date.parse(value))){
			return null;
		}
		return new Date(value);
	}

	return value;
};
flexdms.BasicPropType.prototype.isEqual = function(left, right) {
	if (typeof(left)=='undefined' && typeof(right)=='undefined'){
		return true;
	}
	if (typeof(left)=='undefined' && typeof(right)!='undefined'){
		return false;
	}
	if (typeof(left)!='undefined' && typeof(right)=='undefined'){
		return false;
	}
	if (left==null && right==null){
		return true;
	}
	if ((left==null && right!=null) || (left!=null && right==null)){
		return false;
	}
	if (this.isDateTime()) {
		return left.getTime()==right.getTime();
	}
	return left==right;
};

//------------convenient utility methods

flexdms.BasicPropType.prototype.isEmbedded = function() {
	return this.idx==flexdms.EMBEDED_TYPE_IDX;
};
flexdms.BasicPropType.prototype.isRelation = function() {
	return this.idx==flexdms.RELATION_TYPE_IDX;
};
flexdms.BasicPropType.prototype.isStringType = function() {
	return this.value == 'java.lang.String';
};
flexdms.BasicPropType.prototype.isLongString = function() {
	return this.idx == 1||this.idx == 2||this.idx == 10; 
};
flexdms.BasicPropType.prototype.isTrueFalse = function() {
	return this.idx == 9;
};
flexdms.BasicPropType.prototype.isBinary = function() {
	return this.idx == 8;
};


flexdms.BasicPropType.prototype.isFileType = function() {
	if (this.idx == 16 || this.idx == 17) {
		return true;
	}
	return false;
};
flexdms.BasicPropType.prototype.isCustomObject = function() {
	return this.idx == 10;
};
flexdms.BasicPropType.prototype.isDateTime = function() {
	return this.idx == 11 || this.idx == 12 || this.idx == 13;
};
flexdms.BasicPropType.prototype.isTimeOnly = function() {
	return this.idx == 13;
};
flexdms.BasicPropType.prototype.isTimestamp = function() {
	return this.idx == 12;
};
flexdms.BasicPropType.prototype.isDateOnly = function() {
	return this.idx == 11;
};
flexdms.BasicPropType.prototype.isNumber = function() {
	return this.idx >= 3 && this.idx <= 7;
};
flexdms.BasicPropType.prototype.isInteger = function() {
	return this.idx == 3 || this.idx == 4;
};
flexdms.BasicPropType.prototype.isFloat = function() {
	return this.idx == 5 || this.idx == 6 || this.idx == 7;
};
flexdms.BasicPropType.prototype.isURL = function() {
	return this.idx == 15;
};
flexdms.BasicPropType.prototype.isEmail = function() {
	return this.idx == 14;
};
flexdms.BasicPropType.prototype.isCurrency = function() {
	return this.idx == 7;
};

flexdms.BasicPropType.prototype.isPrimitive = function() {
	return this.idx !=flexdms.EMBEDED_TYPE_IDX && this.idx!=flexdms.RELATION_TYPE_IDX; 
};

flexdms.createEmbeddedPropType=function(type ){
	return new  flexdms.BasicPropType({
		idx : flexdms.EMBEDED_TYPE_IDX,
		value : type.getName(),
		display : type.getDescription(),
		type : 'Embedded',
	});
};
	
flexdms.createRelationPropType=function(type ){
	return  new flexdms.BasicPropType({
		idx : flexdms.RELATION_TYPE_IDX,
		value : type.getName(),
		display : type.getDescription(),
		type : 'Relation',
	});
};

flexdms.guessProptype=function(typestr){
	var pat=new RegExp("\\."+typestr+"$", "i");
	for(var i=0; i<flexdms.basic_types.length; i++){
		if (flexdms.basic_types[i].value==typestr||pat.test(flexdms.basic_types[i].value)){
			return flexdms.basic_types[i];
		}
	}
	var type=flexdms.findType(typestr);
	if (type!==null){
		if (type.isEntity()){
			return flexdms.createRelationPropType(type);
		} 
		return flexdms.createEmbeddedPropType(type);
	}
	return null;
};
flexdms.basic_types = [ new flexdms.BasicPropType({
	idx : 0,
	value : 'java.lang.String',
	display : 'Short Text',
	type : 'Basic',
}), new flexdms.BasicPropType({
	idx : 1,
	value : 'java.lang.String',
	display : 'Medium-sized Text',
	type : 'Basic',
	column : {
		"@length" : 4096
	}
}), new flexdms.BasicPropType({
	idx : 2,
	value : 'java.lang.String',
	display : 'Long Text',
	type : 'Basic',
	lob : true,
	column : {
		"@length" : 4096,
		"@columnDefinition" : "text"
	},
}), new flexdms.BasicPropType({
	idx : 3,
	value : 'java.lang.Integer',
	display : 'Integer',
	type : 'Basic',
}), new flexdms.BasicPropType({
	idx : 4,
	value : 'java.lang.Long',
	display : 'Long',
	type : 'Basic',
}), new flexdms.BasicPropType({
	idx : 5,
	value : 'java.lang.Float',
	display : 'Float',
	type : 'Basic',
}), new flexdms.BasicPropType({
	idx : 6,
	value : 'java.lang.Double',
	display : 'Double',
	type : 'Basic',
}), new flexdms.BasicPropType({
	idx : 7,
	value : 'java.math.BigDecimal',
	display : 'Currency',
	type : 'Basic',
	column : {
		"@precision" : 20,
		"@scale" : 5
	},
}), new flexdms.BasicPropType({
	idx : 8,
	value : 'com.flexdms.flexims.jpa.helper.ByteArray',
	display : 'binary',
	type : 'Basic',
	lob : true, 
}), new flexdms.BasicPropType({
	idx : 9,
	value : 'boolean',
	display : 'True Or False',
	type : 'Basic',
}), new flexdms.BasicPropType({
	idx : 10,
	value : 'java.lang.String',
	display : 'Custom Object',
	type : 'Basic',
	lob : true,
	column : {
		"@length" : 4096,
		"@columnDefinition" : "text"
	},
}), new flexdms.BasicPropType({
	idx : 11,
	value : 'java.util.Calendar',
	display : 'Date Only',
	temporal : 'DATE',
	type : 'Date',
	"property" : [ {
		"@name" : "temporal",
		"@value" : "DATE"
	} ]
}), new flexdms.BasicPropType({
	idx : 12,
	value : 'java.util.Calendar',
	display : 'Date And Time',
	temporal : 'TIMESTAMP',
	type : 'Date',
	"property" : [ {
		"@name" : "temporal",
		"@value" : "TIMESTAMP"
	} ]
}), new flexdms.BasicPropType({
	idx : 13,
	value : 'java.util.Calendar',
	display : 'Time Only',
	temporal : 'TIME',
	type : 'Date',
	"property" : [ {
		"@name" : "temporal",
		"@value" : "TIME"
	} ]
}), new flexdms.BasicPropType({
	idx : 14,
	value : 'java.lang.String',
	display : 'Email',
	type : 'Extended',
	"property" : [ {
		"@name" : "strtype",
		"@value" : "Email"
	} ]
}), new flexdms.BasicPropType({
	idx : 15,
	value : 'java.lang.String',
	display : 'URL',
	type : 'Extended',
	"property" : [ {
		"@name" : "strtype",
		"@value" : "URL"
	} ]
}), new flexdms.BasicPropType({
	idx : 16,
	value : 'java.lang.String',
	display : 'Directory',
	type : 'Extended',
	"property" : [ {
		"@name" : "strtype",
		"@value" : "Directory"
	} ]
}), new flexdms.BasicPropType({
	idx : 17,
	value : 'java.lang.String',
	display : 'File',
	type : 'Extended',
	"property" : [ {
		"@name" : "strtype",
		"@value" : "File"
	} ]
}) ];

flexdms.createEmptyType=function() {
	return {
		"entity-mappings" : {
			"mapped-superclass" : [],
			"entity" : [ {
				"@name" :"", //all attributes have to be at the beginning. Otherwise, moxy may not see it
				"@access" : "VIRTUAL",
				"@class" : "",
				"@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
				"secondary-table" : [],
				"primary-key-join-column" : [],
				"index" : [],
				"cache-index" : [],
				"fetch-group" : [],
				"converter" : [],
				"type-converter" : [],
				"object-type-converter" : [],
				"struct-converter" : [],
				"named-query" : [],
				"named-native-query" : [],
				"named-stored-procedure-query" : [],
				"named-stored-function-query" : [],
				"named-plsql-stored-procedure-query" : [],
				"named-plsql-stored-function-query" : [],
				"oracle-object" : [],
				"oracle-array" : [],
				"plsql-record" : [],
				"plsql-table" : [],
				"sql-result-set-mapping" : [],
				"entity-listeners" : {
					"entity-listener" : []
				},
				"property" : [],
				"attribute-override" : [],
				"association-override" : [],
				"convert" : [],
				"named-entity-graph" : [],
				"attributes" : {
					"basic" : [],
					"basic-collection" : [],
					"basic-map" : [],
					"many-to-one" : [],
					"one-to-many" : [],
					"one-to-one" : [],
					"many-to-many" : [],
					"element-collection" : [],
					"embedded" : [],
					"array" : []
				}
			} ],
			"embeddable" : []
		}
	};
};

flexdms.createEmptyBasicProperty=function() {
	//attribute has to be the beginning of the json. otherwise, the jaxb will ignore attributes at the end.
	return {
		"@name" : "",
		"@optional" : true,
		"@attribute-type" : "String",
		"@target-class" : "", //used by collection
		"@target-entity":"", //user by relationship.
		"@mapped-by":null,
		"@orphan-removal" :false,
		"column" : {
			"@unique" : false,
			"@nullable" : true,
			"@length" : 254
		},
		"convert" : [],
		"converter" : [],
		"type-converter" : [],
		"object-type-converter" : [],
		"struct-converter" : [],
		"cascade-on-delete": false,
		"private-owned": false,
		"property" : [ ]
	};
};

flexdms.createExtensionProperty=function() {
	//attribute has to be the beginning of the json. otherwise, the jaxb will ignore attributes at the end.
	return {
		"@name" : "fxExtraProp",
		"@optional" : true,
		"@attribute-type" : "java.lang.String",
		"@target-class" : "", //used by collection
		"column" : {
			"@unique" : false,
			"@nullable" : true,
			"@length" : 4096
		},
		"lob": {},
		"convert" : [],
		"converter" : [],
		"type-converter" : [],
		"object-type-converter" : [],
		"struct-converter" : [],
		"property" : [ {
			"@name" : "editable",
			"@value" : "false"
		}, {
			"@name" : "viewable",
			"@value" : "false"
		}, {
			"@name" : "displaytext'",
			"@value" : "extensible properties"
		}, {
			"@name" : "system'",
			"@value" : "true"
		}, {
			"@name" : "typeidx'",
			"@value" : "10"
		}, {
			"@name" : "rootClass",
			"@value" : "com.flexdms.flexims.jpa.helper.NameValueList"
		} ]
	};
};

flexdms.addExtraProp=function(obj, name, value) {
	var extraProp = null;
	angular.forEach(obj['property'], function(prop) {
		if (prop['@name'] == name) {
			extraProp = prop;
		}
	});

	if (extraProp != null) {
		extraProp['@value'] = value;
	} else {
		obj['property'].push({
			'@name' : name,
			'@value' : value
		});
	}
};
flexdms.deleteExtraProp=function(obj, name) {
	var idx = -1;
	for (var i = 0; i < obj['property'].length; i++) {
		if (obj['property'][i]['@name'] == name) {
			idx = i;
			break;
		}
	}
	if (idx != -1) {
		obj['property'].splice(idx, 1);
	}
};
flexdms.getExtraProp=function(obj, name) {
	var v = null;
	angular.forEach(obj['property'], function(prop) {
		if (prop['@name'] == name) {
			v = prop['@value'];
		}
	});
	return v;
};
flexdms.getExtraPropObj=function(obj, name){
	var pat=new RegExp("^"+name+"\\.");
	var inputobj={};
	angular.forEach(obj['property'], function(prop) {
		if (pat.test(prop['@name'])) {
			inputobj[prop['@name']]=prop['@value'];
		}
	});
	
	var unflatted=flexdms.unflatObject(inputobj);
	if (angular.isDefined(unflatted[name])){
		return unflatted[name];
	}
	return null;
};
flexdms.setExtraPropObj=function(obj, name, value){
	var pat=new RegExp("^"+name+"\\.");
	for (var i =obj['property'].length-1 ; i>=0; i--) {
		if (pat.test(obj['property'][i]['@name'])){
			obj['property'].splice(i, 1);
		}
	}
	var results={};
	flexdms.flatObject(value, results, name);
	for (var prop in results){
		obj['property'].push({
			'@name' : prop,
			'@value' : results[prop]
		});
	}
};
/**
 * Encapsulate all property -related function to this class.
 */
flexdms.Property = function(propJson) {
	this.type = null;

	this.collectionType = "basic"; //what collection this property belongs to

	this.prop = propJson; //the json data for this property
	if (!angular.isDefined(this.prop.column)){
		this.prop.column={};
	}
};

flexdms.Property.prototype.getBelongingType=function(){
	return this.type;
};

/**
 * retrieve default from allowed value.
 * JS may use objct identity for comparison
 */
flexdms.Property.prototype.getConvertedDefault = function() {
	var i = flexdms.getExtraProp(this.prop, 'defaultvalue');
	if (i == null) {
		if (this.isElementCollection()) {
			return [];
		}
		return null;
	}
	var prop=this;
	if (this.isElementCollection()) {
		var vs = new Array();
		angular.forEach(i.split(","), function(v) {
			vs.push(prop.getTypeObject().parse(v));
		});
		return vs;
	}
	return this.getTypeObject().parse(i);
};

/**
 * 
 * why this: angular js use object identity for comparison. If we have twos date that is the same time, angularjs can not recognize it.
 * we have to use the date from allowed value.
 * @param vs a series values or single value.
 * @returns value from allowed values that equal one of input.
 * input is returned if this property has no allowed value.
 * 
 */
flexdms.Property.prototype.pickValueFromAlloweds=function(vs){
	var as=this.getAllowedValues();
	if (as==null){
		return vs;
	}
	
	
	var p=this;
	function foundinAlloweds(v){
		for(var i=0; i<as.length; i++){
			if (p.getTypeObject().isEqual(as[i].value, v)){
				return as[i].value;
			}
		}
		return null;
	}
	if (!(vs instanceof Array)){
		return foundinAlloweds(vs);
	}
	var ret=[];
	for (var i=0; i<vs.length; i++){
		var found=foundinAlloweds(vs[i]);
		if (found!=null){
			ret.push(found);
		}
	}
	return ret;
};

flexdms.Property.prototype.getAllowedValues = function() {

	if (angular.isDefined(this.allowedvalues)) {
		return this.allowedvalues;
	}
	
	this.allowedvalues= flexdms.getExtraPropObj(this.prop, 'allowedvalues');
	if (this.allowedvalues){
		var prop1 = this;
		angular.forEach(this.allowedvalues, function(o) {
			if (o.display == null || o.display.length == 0) {
				o.display = o.value;
			}
			o.value = prop1.getTypeObject().parse(o.value);
		});
	}
	
	return this.allowedvalues;
};
flexdms.Property.prototype.getPatExp = function() {
	var pat = this.getPattern();
	if (pat == null) {
		return null;
	}
	if (this.isIgnoreCaseForPattern()) {
		return new RegExp(pat, "i");
	}
	return new RegExp(pat);
};
flexdms.Property.prototype.generateValue=function(){
	var v = this.getTypeObject().generateDefault();
	if (this.isElementCollection()) {
		return [ v ];
	} 
	return v;
};

//------------------utility
flexdms.Property.prototype.getName = function() {
	return this.prop['@name'];
};
flexdms.Property.prototype.getMappedBy = function() {
	return this.prop['@mapped-by'];
};

// what kinds of property
flexdms.Property.prototype.isBasic = function() {
	return this.collectionType == 'basic';
};
flexdms.Property.prototype.isIdOrVersion = function() {
	return this.collectionType == 'id' || this.collectionType == 'version';
};
flexdms.Property.prototype.isBasicMap = function() {
	return this.collectionType == 'basic-map';
};
flexdms.Property.prototype.isManyToOne = function() {
	return this.collectionType == 'many-to-one';
};
flexdms.Property.prototype.isOneToMany = function() {
	return this.collectionType == 'one-to-many';
};
flexdms.Property.prototype.isOneToOne = function() {
	return this.collectionType == 'one-to-one';
};
flexdms.Property.prototype.isManyToMany = function() {
	return this.collectionType == 'many-to-many';
};
flexdms.Property.prototype.isElementCollection = function() {
	return this.collectionType == 'element-collection';
};
flexdms.Property.prototype.isEmbedded = function() {
	return this.collectionType == 'embedded';
};
flexdms.Property.prototype.isEmbeddedElementCollection = function() {
	if (this.collectionType != 'element-collection'){
		return false;
	}
	if (this.getTypeObject().isEmbedded()){
		return true;
	}
	return false;
};
flexdms.Property.prototype.isCollection = function() {
	return this.isElementCollection() || this.isOneToMany()
			|| this.isManyToMany();
};
flexdms.Property.prototype.isRelation = function() {
	return this.isManyToOne() || this.isOneToMany() || this.isOneToOne()
			|| this.isManyToMany();
};

//extrat properties for string
flexdms.Property.prototype._internalSetExtraProp = function(name, value) {
	if (value == null) {
		flexdms.deleteExtraProp(this.prop, name);
	} else {
		flexdms.addExtraProp(this.prop, name, value);
	}
};
flexdms.Property.prototype.getExtraProp=function(propName){
	return flexdms.getExtraProp(this.prop, propName);
};
flexdms.Property.prototype.getDisplayText = function() {
	var i = flexdms.getExtraProp(this.prop, 'displaytext');
	if (i == null || i.trim().length == 0) {
		return this.getName();
	}
	return i;
};
flexdms.Property.prototype.setDisplayText = function(value) {
	this._internalSetExtraProp('displaytext', value);
};
flexdms.Property.prototype.getTooltip = function() {
	return flexdms.getExtraProp(this.prop, 'tooltip');
};
flexdms.Property.prototype.setTooltip = function(value) {
	this._internalSetExtraProp('tooltip', value);
};
flexdms.Property.prototype.getMinLen = function() {
	return flexdms.getExtraProp(this.prop, 'minlen');
};
flexdms.Property.prototype.setMinLen = function(value) {
	this._internalSetExtraProp('minlen', value);
};
flexdms.Property.prototype.getMaxLen = function() {
	var i=flexdms.getExtraProp(this.prop, 'maxlen');
	if (i!=null){
		return i;
	}
	if (angular.isDefined(this.prop.column) && angular.isDefined(this.prop.column['@length'])){
		return this.prop.column['@length'];
	}
	return null;
};
flexdms.Property.prototype.setMaxLen = function(value) {
	this._internalSetExtraProp('maxlen', value);
};
flexdms.Property.prototype.getMinValue = function() {
	return flexdms.getExtraProp(this.prop, 'minvalue');
};
flexdms.Property.prototype.setMinValue = function(value) {
	this._internalSetExtraProp('minvalue', value);
};
flexdms.Property.prototype.getMaxValue = function() {
	return flexdms.getExtraProp(this.prop, 'maxvalue');
};
flexdms.Property.prototype.setMaxValue = function(value) {
	this._internalSetExtraProp('maxvalue', value);
};
flexdms.Property.prototype.getPattern = function() {
	return flexdms.getExtraProp(this.prop, 'pattern');
};


flexdms.Property.prototype.setPattern = function(value) {
	this._internalSetExtraProp('pattern', value);
};
flexdms.Property.prototype.isIgnoreCaseForPattern = function() {
	var i = flexdms.getExtraProp(this.prop, 'ignorepatterncase');
	return i == null ? true : flexdms.parseTrueFalse(i);
};
flexdms.Property.prototype.setIgnoreCaseForPattern = function(value) {
	this._internalSetExtraProp('ignorepatterncase', value);
};

flexdms.Property.prototype.isAutoGenerate = function() {
	var i = flexdms.getExtraProp(this.prop, 'autogenerate');
	return i == null ? false : flexdms.parseTrueFalse(i);
};
flexdms.Property.prototype.setAutogenerate = function(value) {
	this._internalSetExtraProp('autogenerate', value);
};
flexdms.Property.prototype.isSummaryProp = function() {
	var i = flexdms.getExtraProp(this.prop, 'summaryprop');
	return i == null ? false : flexdms.parseTrueFalse(i);
};
flexdms.Property.prototype.setSummaryProp = function(value) {
	this._internalSetExtraProp('summaryprop', value);
};

flexdms.Property.prototype.getDefaultValue = function() {
	var i = flexdms.getExtraProp(this.prop, 'defaultvalue');
	return i;
};
flexdms.Property.prototype.isEqual = function(left, right) {
	return this.getTypeObject().isEqual(left, right);
};

flexdms.Property.prototype.setDefaultValue = function(value) {
	this._internalSetExtraProp('defaultvalue', value);
};

flexdms.Property.prototype.isFileUpload = function() {
	var i = flexdms.getExtraProp(this.prop, 'clientfile');
	return i == null ? false : flexdms.parseTrueFalse(i);
};
flexdms.Property.prototype.setFileUpload = function(value) {
	this._internalSetExtraProp('clientfile', value);
};

flexdms.Property.prototype.isViewable = function() {
	var i = flexdms.getExtraProp(this.prop, 'viewable');
	return i == null ? true : flexdms.parseTrueFalse(i);
};
flexdms.Property.prototype.setViewable = function(value) {
	this._internalSetExtraProp('viewable', value);
};
flexdms.Property.prototype.isEditable = function() {
	var i = flexdms.getExtraProp(this.prop, 'editable');
	return i == null ? true : flexdms.parseTrueFalse(i);
};
flexdms.Property.prototype.isRelationOwner = function() {
	return this.prop['@mapped-by']?false:true;
};
flexdms.Property.prototype.setEditable = function(value) {
	this._internalSetExtraProp('editable', value);
};
flexdms.Property.prototype.getRootClass = function() {
	var i = flexdms.getExtraProp(this.prop, 'rootClass');
	return i;
};
flexdms.Property.prototype.getShortRootClass = function() {
	var i=this.getRootClass();
	if (i==null){
		return null;
	}
	return i.substring(i.lastIndexOf(".")+1);
};

flexdms.Property.prototype.setRootClass = function(value) {
	this._internalSetExtraProp('rootClass', value);
};

flexdms.Property.prototype.setAllowedValues = function(value) {
	delete(this.allowedvalues);
	if (value===null){
		return;
	}
	flexdms.setExtraPropObj(this.prop,"allowedvalues", value);
};

flexdms.Property.prototype.isRequired = function() {
	if (angular.isDefined(this.prop['@optional'])){
		return !this.prop['@optional'];
	}
	if(!angular.isDefined(this.prop.column) || !angular.isDefined(this.prop.column['@nullable'])){
		return false;
	}
	return !this.prop.column['@nullable'];
};
flexdms.Property.prototype.setRequired = function(value) {
	var r = (value == null ? false : value);
	var owingType=this.getBelongingType();
	if (owingType.getParentTypeName()!==null){
		//always false for subtype
		if (angular.isDefined(this.prop.column)){
			this.prop.column['@nullable'] = false;
		}
	} else{
		this.prop.column['@nullable'] = !r;
	}
	this.prop['@optional'] = !r;
};
flexdms.Property.prototype.isSystem = function() {
	var i = flexdms.getExtraProp(this.prop, 'system');
	return i == null ? false : flexdms.parseTrueFalse(i);
};
flexdms.Property.prototype.setSystem = function(value) {
	var r = (value == null ? false : value);
	this._internalSetExtraProp('system', r);
};
flexdms.Property.prototype.isUnique = function() {
	return this.prop.column['@unique'];
};

flexdms.Property.prototype.setTypeObject = function(proptype) {
	this.proptype = proptype;
	this.flushType();
};
flexdms.Property.prototype.getTypeObject = function() {
	if (this.proptype == null) {
		var typeidx = flexdms.getExtraProp(this.prop, 'typeidx');
		
		
		if (typeidx==null){
			if (this.collectionType=='id') {
				typeidx=4; //long
				this.proptype=flexdms.basic_types[typeidx];
			} else if (this.collectionType=='version'){
				typeidx=12; //timestamp
				this.proptype=flexdms.basic_types[typeidx];
			} else if (this.collectionType=='embedded'){
				typeidx=flexdms.EMBEDED_TYPE_IDX;
				this.proptype=flexdms.createEmbeddedPropType(flexdms.findType(this.prop['@attribute-type']));
			} else if (this.collectionType=='basic'){
				this.proptype=flexdms.guessProptype(this.prop['@attribute-type']);
				if (this.proptype==null){
					this.proptype=flexdms.basic_types[0];
				}
				if (this.proptype.value=="java.util.Calendar" && angular.isDefined(this.prop.temporal)){
					if (this.prop.temporal.toUpperCase()==flexdms.basic_types[11].temporal){
						this.proptype=flexdms.basic_types[11];
					} else 	if (this.prop.temporal.toUpperCase()==flexdms.basic_types[12].temporal){
						this.proptype=flexdms.basic_types[12];
					} else 	if (this.prop.temporal.toUpperCase()==flexdms.basic_types[13].temporal){
						this.proptype=flexdms.basic_types[13];
					} 
					
				}
			} else if (this.collectionType=='element-collection'){
				this.proptype=flexdms.guessProptype(this.prop['@target-class']);
				if (this.proptype==null){
					this.proptype=flexdms.basic_types[0];
				}
			} else {
				typeidx=flexdms.RELATION_TYPE_IDX;
				this.proptype=flexdms.createRelationPropType(flexdms.findType(this.prop['@target-entity']));
			}
		}
		var idx=typeidx-0;
		if (this.proptype!=null){
			return this.proptype;
		}
		if (idx==flexdms.EMBEDED_TYPE_IDX) {
			if (this.collectionType=='embedded') {
				this.proptype=flexdms.createEmbeddedPropType(flexdms.findType(this.prop['@attribute-type']));
			} else {
				this.proptype=flexdms.createEmbeddedPropType(flexdms.findType(this.prop['@target-class']));
			}
		} else if (idx==flexdms.RELATION_TYPE_IDX){
			this.proptype=flexdms.createRelationPropType(flexdms.findType(this.prop['@target-entity']));
		} else {
			this.proptype = flexdms.basic_types[idx];
		}
		
	}
	return this.proptype;
};
flexdms.Property.prototype.getRelationType=function(){
	return flexdms.findType(this.getTypeObject().value);
};
flexdms.Property.prototype.flushType = function() {
	if (this.isRelation()){
		//for relation.
		this.prop['@target-entity']=this.proptype.value;
		this.prop['@target-class']=null;
		if (!this.isCollection()) {
			this.prop['@attribute-type']=null;
		}
	
	} else if (this.isCollection()){
		//for element-collection
		this.prop['@target-class']=this.proptype.value;
		this.prop['@target-entity']=null;
	} else {
		//for single primitive or embedded
		this.prop['@attribute-type'] = this.proptype.value;
		this.prop['@target-class']=null;
		this.prop['@target-entity']=null;
	}
	
	if (angular.isDefined(this.proptype.property)) {
		this.prop.property = this.proptype.property;
	} else {
		this.prop.property = [];
	}
	this._internalSetExtraProp('typeidx', this.proptype.idx);

	if (this.proptype.isDateTime()) {
		this.prop.temporal = this.proptype.temporal;
	} else {
		delete (this.prop.temporal);
	}

	//lob
	if (angular.isDefined(this.proptype.lob) && this.proptype.lob) {
		this.prop.lob = {};
	} else {
		delete (this.prop.lob);
	}

	//column 
	if (angular.isDefined(this.proptype.column)) {
		for ( var propname in this.proptype.column) {
			this.prop.column[propname] = this.proptype.column[propname];
		}
	} else {
		this.prop.column = flexdms.createEmptyBasicProperty().column;
	}

	/*if (angular.isDefined(this.proptype.convert)) {
		this.prop.convert = this.proptype.convert;
		this.prop.converter = this.proptype.converter;
	} else {
		this.prop.convert = [];
		this.prop.converter = [];
	}*/

	if (this.proptype.isCurrency()) {
		delete (this.prop.column['@length']); //bigdecimal is controlled by scale and precision
	}
	
	if (this.proptype.idx==8) {
		delete (this.prop.column['@length']); //blob can not have length, postgresql does not support it
	}
	
};

/**
 * Assume current relationship is the owner.
 * 
 */
flexdms.Property.prototype.findInverseProp = function() {
	if (!this.isRelation()){
		return null;
	}
	if (this.prop['@mapped-by']){
		return null;
	}
	this.getTypeObject();
	var relatedType=flexdms.findType(this.proptype.value);
	var inverseprop=null;
	var fromprop=this;
	angular.forEach(relatedType.getProps(), function(propObj){
		if (!propObj.isRelation()){
			return;
		}
		if (!propObj.prop['@mapped-by']){
			return;
		}
		if (propObj.prop['@mapped-by']==fromprop.getName()){
			//Do we check relation cardinality?
			inverseprop=propObj;
		}
	});
	return inverseprop;
};
flexdms.Property.prototype.findOwnerProp = function() {
	if (!this.isRelation()){
		return null;
	}
	if (!this.prop['@mapped-by']){
		return null;
	}
	this.getTypeObject();
	return flexdms.findType(this.proptype.value).getProp(this.prop['@mapped-by']);
};

flexdms.Property.prototype.findRelatedProp = function() {
	if (!this.isRelation()){
		return null;
	}
	if (this.prop['@mapped-by']){
		return this.findOwnerProp();
	} 
	return this.findInverseProp();
	
};
