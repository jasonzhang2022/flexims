
	
flexdms.query={};
flexdms.query.Operator=function(op){
	this.op=op;
	this.howManyOperands=function(){
		switch(this.op)
		{
		case "lt":
		case "le":
		case "eq":
		case "gt":
		case "ge":
		case "ne":
		case "like":
		case "notlike":
		case "tracedown":
		case "size":
		case "sizeGt":
		case "sizeLt":
			return 1;
		case "checked":
		case "unchecked":
		case "notnull":
		case "isnull":
		case "empty":
		case "notempty":
			return 0;
		case "between":
		case "notbetween":
			return 2;
		default:
			//oneof, notoneof
			return 3;
		}
	};
	this.isSize=function(){
		switch(this.op)
		{
		case "size":
		case "sizeGt":
		case "sizeLt":
		case "empty":
		case "notempty":
			return true;
		}
		return false;
	};
	this.isComparison=function(){
		switch(this.op)
		{
		case "lt":
		case "le":
		case "eq":
		case "gt":
		case "ge":
		case "ne":
		case "between":
		case "notbetween":
		case "like":
		case "notlike":
		case "tracedown":
			return true;
		default:
			return false;
		}
	};
};


//----------------------typedquery class
flexdms.query.typedquery=function(inst){
	this.inst=inst;
};

flexdms.query.typedquery.prototype.getTargetedType=function(){
	return flexdms.findType(this.inst.TargetedType);
};
flexdms.query.typedquery.prototype.getDisplayableProps=function(){
	if (!this.inst.TargetedType){
		return new Array();
	}
	if (angular.isDefined(this.displayableProps)){
		return  this.displayableProps;
	}
	var displayableProps=new Array();
	angular.forEach(this.getTargetedType().getProps(), function(propobj){
		if (propobj.isIdOrVersion()){
			return;
		}
		displayableProps.push({
			'display':propobj.getDisplayText(),
			'value':propobj.getName(),
			'group':''
		});
		if (propobj.isRelation() || propobj.isEmbedded()){
			var relatedtype=flexdms.findType(propobj.getTypeObject().value);
			angular.forEach(relatedtype.getProps(), function(subprop){
				if (!subprop.isIdOrVersion()){
					displayableProps.push({
						'display':subprop.getDisplayText(),
						'value': propobj.getName()+"."+subprop.getName(),
						'group':propobj.getDisplayText()
					});
				}
			});
		}
	});
	this.displayableProps=displayableProps;
	return this.displayableProps;
};
flexdms.query.typedquery.prototype.getQueriableProps=function(){
	var queriableProps=new Array();
	if (!this.inst.TargetedType){
		return queriableProps;
	}
	
	var type=this.getTargetedType();
	angular.forEach(type.getProps(), function(propobj){
		if (propobj.isIdOrVersion()){
			return;
		}
		var proptype=propobj.getTypeObject();
		if (!(proptype.isCustomObject()  ||proptype.lob || proptype.idx==1 || proptype.idx==2)){
			queriableProps.push(propobj);
		}
	});
	return queriableProps;
};

flexdms.query.typedquery.prototype.getOrderProps=function(){
	
	if (!this.inst.TargetedType){
		return new Array();
	}
	if (angular.isDefined(this.orderProps)){
		return this.orderProps;
	}
	var orderProps=new Array();
	angular.forEach(this.getTargetedType().getProps(), function(propobj){
		if (propobj.isIdOrVersion()){
			return;
		}
		if (propobj.isCollection()){
			return;
		}
		var tp=propobj.getTypeObject();
		if (tp.isCustomObject() || tp.isTrueFalse() || tp.isBinary()){
			return;
		}
		if (tp.isEmbedded()){
			var relatedType=flexdms.findType(tp.value);
			angular.forEach(relatedType.getProps(), function(subprop){
				if (subprop.isCollection()){
					return;
				}
				orderProps.push({
					'value':propobj.getName()+"."+subprop.getName(),
					'display':subprop.getDisplayText(),
					'group':propobj.getName()
				});
				
			});
		}
		if (!propobj.getTypeObject().isPrimitive()){
			return;
		}
		
		orderProps.push({
			'value':propobj.getName(),
			'group':'',
			'display':propobj.getDisplayText()
		});
	});
	this.orderProps=orderProps;
	return this.orderProps;
};

//-----------------------defaulttypedquery class
flexdms.query.defaulttypedquery=function(inst){
	this.inst=inst;
	
};


//establish the inheritance from defaultypedquery to typedquery
flexdms.query.defaulttypedquery.prototype=new flexdms.query.typedquery();
flexdms.query.defaulttypedquery.prototype.constructor=flexdms.query.defaulttypedquery;


flexdms.query.defaulttypedquery.prototype.getParams=function(instCache){
	if (this.params!=null) {
		return this.params;
	}
	var params=[];
	angular.forEach(this.getConditions(), function(condition){
		condition.processParams(instCache, params);
	});
	
	this.params=params;
	return params;
};
flexdms.query.defaulttypedquery.prototype.getConditions=function(){
	if (this.condtions){
		return this.conditions;
	}
	var conditions=[];
	var query=this;
	angular.forEach(this.inst.Conditions, function(cond){
		conditions.push(new flexdms.query.condition(query, cond));
	});
	this.conditions=conditions;
	return this.conditions;
};

//-----------------------conditions
flexdms.query.condition=function(query, inst){
	this.query=query;
	this.inst=inst; //embedded instance
	
};
flexdms.query.condition.prototype.getProp=function(){
	return this.query.getTargetedType().getProp(this.inst.Property);
};

flexdms.query.condition.prototype.processParams=function(instCache, params){
	switch(this.inst.Operator)
	{
	case "checked":
	case "unchecked":
	case "notnull":
	case "isnull":
	case "empty":
	case "notempty":
		params.push( new flexdms.query.param(this, -1));
		break;
	case "lt":
	case "le":
	case "eq":
	case "gt":
	case "ge":
	case "ne":
	case "like":
	case "notlike":
		params.push( new flexdms.query.param(this, 0));
		break;
	case "size":
	case "sizeGt":
	case "sizeLt":
		params.push( new flexdms.query.param(this, 0));
		break;
	case "between":
	case "notbetween":
		params.push( new flexdms.query.param(this, 0));
		params.push( new flexdms.query.param(this, 1));
		break;
	case "tracedown":
		var subquery=instCache.getInst('DefaultTypedQuery', this.inst.TraceDown);
		var queryObj=new flexdms.query.defaulttypedquery(subquery);
		angular.forEach(queryObj.getConditions(), function(condition){
			condition.processParams(instCache, params);
		});
		break;
	case "oneof":
	case "notoneof":
		params.push( new flexdms.query.param(this, 2));
	}
};

//----------------------------represent a query param at client side. The purpose to collect 
// value for query parameter at client side
flexdms.query.param=function(condition, index){
	
	this.condition=condition;
	this.index=0;
	this.relativeunit='MONTH';
	this.relativevalue=null;
	this.wholetime=true;
	
	if (angular.isDefined(index)){
		this.index=index;
	}
	
	this.value=null;
	this.getOp=function(){
		return new flexdms.query.Operator(this.condition.inst.Operator);
	};
	
	
	this.computeValue=function(){
		if (this.index==-1){
			//no value is needed;
			return;
		}
		
		if (!angular.isDefined(this.value) || this.value==null){
			return;
		}
		var op=this.getOp();
		if (op.isSize()){
			this.value=this.value-0;
			return;
		}
		var proptype=this.condition.getProp().getTypeObject();
		//only for single date and time
		if (this.index<2 && (proptype.isDateOnly() || proptype.isTimestamp() )){
			if (isNaN(Date.parse(this.value))){ //relative time
				this.relativevalue=parseFloat(this.value.split(":")[0]);
				this.relativeunit=this.value.split(":")[1];
				this.wholetime=(this.value.split(":")[2]=='true'||this.value.split(":")[2]=='TRUE')
				this.value=null;
				return;
			}
		}
		if (this.index==0 || this.index==1){		
			this.value=proptype.parse(this.value)
			return;
		}
		if (this.index==2){
			var vs=[];
			angular.forEach(this.value, function(v){
				 vs.push(proptype.parse(v));
			})
			this.value=vs;
		}
	};
	
	this.setValueFromQuery=function(){
		if (this.index==-1){
			//no value is needed;
			return;
		}
		
		
		var op=this.getOp();
		if (op.isSize()){
			this.value=this.condition.inst.FirstValue
			if (this.value){
				this.value=this.value-0;
			}
			return;
		}
		if (this.index==0){
			this.value=this.condition.inst.FirstValue;
			this.relativeunit=this.condition.inst.RelativeStartUnit;
			this.relativevalue=this.condition.inst.RelativeStartDate;
			this.wholetime=this.condition.inst.WholeTime;
			if (!this.relativeunit){
				this.relativeunit="MONTH";
			}
			return;
		}
		if (this.index==1){
			this.value=this.condition.inst.SecondValue;
			this.relativeunit=this.condition.inst.RelativeEndUnit;
			this.relativevalue=this.condition.inst.RelativeEndDate;
			this.wholetime=this.condition.inst.WholeTime;
			if (!this.relativeunit){
				this.relativeunit="MONTH";
			}
		}
		if (this.index==2){
			if (angular.isDefined(this.condition.inst.FirstValue) && this.condition.inst.FirstValue!==null){
				this.value=this.condition.inst.FirstValue.split(",");
			} else {
				this.value=[];
			}
			return;
		}
	}
	this.setValueFromQuery();
	this.computeValue();
	
	this.isReady=function(){
		if (this.index==-1){
			return true;
		}
		var op=this.getOp();
		if (angular.isDefined(this.relativevalue) && this.relativevalue!==null){
			return true;
		}
		if (!angular.isDefined(this.value) || this.value===null){
			return false;
		}
		if (op.isSize() ||this.index==0 ||this.index==1){
			return true;
		}
		if (angular.isArray(this.value)){
			return this.value.length>0?true:false;
		}
		return true;
	};

	
	
	
	
	this.getLabelText=function(){
		if (this.condition.Description){
			return this.condition.Description;
		}
		
		var text=this.condition.getProp().getDisplayText()+" ";
		var op=this.getOp();
		
		switch(op.op)
		{
		case "lt":
			text+="less than";
			break;
		case "le":
			text+="less than or equal";
			break;
		case "eq":
			text+="equal";
			break;
		case "gt":
			text+="greater than";
			break;
		case "ge":
			text+="greater than or equal";
			break;
		case "ne":
			text+="not equal";
			break;
		case "like":
			text+="like (% for any character, _ for single character)";
			break;
		case "notlike":
			text+="not like(% for any character, _ for single character)";
			break;
		case "tracedown":
			text+="sub query";
			break;
		case "size":
			text+="has number of elements";
			break;
		case "sizeGt":
			text+="has more than";
			break;
		case "sizeLt":
			text+="has less than";
			break;
		case "checked":
			text+="checked";
			break;
		case "unchecked":
			text+="unchecked";
			break;
		case "notnull":
			text+="has value";
			break;
		case "isnull":
			text+="has no value";
			break;
		case "empty":
			text+="has no members";
			break;
		case "notempty":
			text+="has members";
			break;
		case "between":
			if (this.index==0){
				text+="greater than or equal";
			} else {
				text+="less than or equal";
			}
			break;
		case "notbetween":
			if (this.index==0){
				text+="less than";
			} else {
				text+="gerater than";
			}
			break;
		case "oneof":
			text+="contains";
			break;
		case "notoneof":
			text+="not contains";
			break;
		default:
			
		}
		return text;
		
	};
	this.getLabelTextViewer=function(){
		if (this.condition.Description){
			return this.condition.Description;
		}
		
		var text=this.condition.getProp().getDisplayText()+" ";
		var op=this.getOp();
		
		switch(op.op)
		{
		case "lt":
			text+="less than";
			break;
		case "le":
			text+="less than or equal";
			break;
		case "eq":
			text+="equal";
			break;
		case "gt":
			text+="greater than";
			break;
		case "ge":
			text+="greater than or equal";
			break;
		case "ne":
			text+="not equal";
			break;
		case "like":
			text+="like";
			break;
		case "notlike":
			text+="not like";
			break;
		case "tracedown":
			text+="sub query";
			break;
		case "size":
			text+="has number of elements";
			break;
		case "sizeGt":
			text+="has more than";
			break;
		case "sizeLt":
			text+="has less than";
			break;
		case "checked":
			text+="checked";
			break;
		case "unchecked":
			text+="unchecked";
			break;
		case "notnull":
			text+="has value";
			break;
		case "isnull":
			text+="has no value";
			break;
		case "empty":
			text+="has no members";
			break;
		case "notempty":
			text+="has members";
			break;
		case "between":
			if (this.index==0){
				text+="greater than or equal";
			} else {
				text+="less than or equal";
			}
			break;
		case "notbetween":
			if (this.index==0){
				text+="less than";
			} else {
				text+="gerater than";
			}
			break;
		case "oneof":
			text+="contains";
			break;
		case "notoneof":
			text+="not contains";
			break;
		default:
			
		}
		return text;
		
	};
	
	this.needInput=function(){
		var op=new flexdms.query.Operator(this.condition.inst.Operator);
		var numOfOperands=op.howManyOperands();
		return numOfOperands>0;
	};
	
	//return a name value pair
	this.getValueObject=function(i){
		function toString(v){
			if (angular.isDate(v)){
				return v.toISOString();
			}
			return v+"";
		}
		var pobj={'index': i, 'values':[]};
		if (angular.isDefined(this.relativevalue) && this.relativevalue!==null){
			pobj.values.push(this.relativevalue+":"+this.relativeunit+":"+this.wholetime);
			return pobj;
		} 
		if (!angular.isDefined(this.value)|| this.value===null){
			return pobj;
		}
		
		
		if (angular.isArray(this.value)){
			angular.forEach(this.value, function(v){
				if (angular.isDefined(v)&&v!==null){
					pobj.values.push(toString(v));
				}
			});
		} else{
			//we have single value: most string or object
			var op=this.getOp();
			if (op.howManyOperands()==1|| op.howManyOperands()==2){
				pobj.values.push(toString(this.value));
			} else if (op.howManyOperands()==3){
				angular.forEach(this.value.split(","), function(v){
					if (angular.isDefined(v)&&v!==null && v.length>0){
						pobj.values.push(v);
					}
				});
			}
		}
		return pobj;
	};
	
};

angular.module("flexdms.report", ["flexdms.TypeResource", "flexdms.TypeResource", "instDirective"]);
flexdms.instAppModules.push("flexdms.report");

