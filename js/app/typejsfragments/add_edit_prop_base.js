
function agumentScopeForOrder($scope) {
	var primitiveOrderOptions=[
	                           {
	                        	   'value':'ByValue',
	                        	   'display':'Value',
	                        	   'group':''
	                           }, {
	                        	   'value':'ByIndex',
	                        	   'display':'Added Order',
	                        	   'group':''
	                           }
	                           ];
	
	
	function getOrderProps(targetType){
		var orderProps=new Array();
		angular.forEach(targetType.getProps(), function(propobj){
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
		orderProps.push({
			'value':'ByIndex',
			'display':'Added Order',
			'group':'Special',
		});
		orderProps.push({
			'value':'ByValue',
			'display':'Default:Database ID',
			'group':'Special'
		});
		return orderProps;
	}
	
	$scope.orderByOptions=function(){
		if (( $scope.propObj.collectionType=='one-to-many' 
			|| $scope.propObj.collectionType=='many-to-many' 
				|| $scope.propObj.collectionType=='element-collection') && $scope.prop['@attribute-type']=='java.util.List'){
			if ( $scope.propObj.collectionType=='element-collection'){
				return primitiveOrderOptions;
			}
			var targetedType=flexdms.findType($scope.propObj.getTypeObject().value);
			return getOrderProps(targetedType);
		}
		return [];
	};
	
	$scope.$watch("prop['@attribute-type']", function(newv, oldv){
		if (newv && (newv!==oldv || !angular.isDefined($scope.orders))){
			
			//set Orders
			$scope.orders=$scope.orderByOptions();
		}
	});
	
	
	
	
	$scope.setOrder=function(){
		//orderby
		if ($scope.propObj.collectionType==='element-collection'){
			if ($scope.extraprops.orderby && $scope.propObj.prop['@attribute-type']==='java.util.List'){
				if ($scope.extraprops.orderby==='ByValue'){
					$scope.propObj.prop['order-by']={};
					$scope.propObj._internalSetExtraProp("orderColumn", null);
				} else {
					//order column
					delete($scope.propObj.prop['order-by']);
					$scope.propObj._internalSetExtraProp("orderColumn", "true");
				}
			} else {
				delete($scope.propObj.prop['order-by']);
				$scope.propObj._internalSetExtraProp("orderColumn", null);
			}
		} else if ($scope.propObj.collectionType==='many-to-many' ||$scope.propObj.collectionType==='one-to-many') {
			
			
			if ($scope.extraprops.orderby && $scope.propObj.prop['@attribute-type']==='java.util.List'){
				//by database id.
				if ($scope.extraprops.orderby==='ByValue'){
					$scope.propObj.prop['order-by']={};
					$scope.propObj._internalSetExtraProp("orderColumn", null);
				} else if ($scope.extraprops.orderby==='ByIndex'){
					if (angular.isDefined($scope.prop['@mapped-by']) && $scope.prop['@mapped-by']!=null){
						//ignore, it mapped by
						delete($scope.propObj.prop['order-by']);
						$scope.propObj._internalSetExtraProp("orderColumn", null);
					} else {
						delete($scope.propObj.prop['order-by']);
						$scope.propObj._internalSetExtraProp("orderColumn", "true");
					}
				} else { 
					//by property
					$scope.propObj.prop['order-by']=$scope.extraprops.orderby+" "+($scope.orderasc?'ASC':'DESC');
					$scope.propObj._internalSetExtraProp("orderColumn", null);
				}
			} else {
				delete($scope.propObj.prop['order-by']);
				$scope.propObj._internalSetExtraProp("orderColumn", null);
			}
		} else {
			delete($scope.propObj.prop['order-by']);
			$scope.propObj._internalSetExtraProp("orderColumn", null);
		}
	};
	

	$scope.initOrder=function(){
		if (!$scope.propObj.prop['@attribute-type']==='java.util.List'){
			return null;
		}
		//orderby
		if ($scope.propObj.collectionType==='element-collection'){
			if(angular.isDefined($scope.propObj.prop['order-by'])){
				$scope.extraprops.orderby="ByValue";
			} else if ($scope.propObj.getExtraProp("orderColumn")!=null){
				$scope.extraprops.orderby="ByIndex";
			} else {
				$scope.extraprops.orderby=null;
			}
		} else if ($scope.propObj.collectionType==='many-to-many' ||$scope.propObj.collectionType==='one-to-many') {
			if ($scope.propObj.getExtraProp("orderColumn")!=null){
				
				$scope.extraprops.orderby="ByIndex";
			} else if(angular.isDefined($scope.propObj.prop['order-by'])){
				if (angular.isObject($scope.propObj.prop['order-by'])){
					$scope.extraprops.orderby="ByValue";
				} else {
					var vs=$scope.propObj.prop['order-by'].split(" ");
					$scope.extraprops.orderby=vs[0];
					if (vs.length===1|| vs[1]=='ASC'){
						$scope.orderasc=true;
					} else {
						$scope.orderasc=false;
					}
				}
			} else {
				$scope.extraprops.orderby=null;
			}
		} else {
			$scope.extraprops.orderby=null;
		}
		
	};
}

function agumentScopeForDefaultAndAllowed($scope) {
	/*
	 * Make sure allowed value and default value are correct
	 */
	$scope.validateDefaultAndAllowed=function(){
		if (!$scope.canDefault()){
			return true;
		}
		$scope.processExtraProps();
		if ($scope.extraprops.allowedvalues.length>0) {
			for (var i=0; i<$scope.extraprops.allowedvalues.length; i++){
				var ctrl=$scope.propform.allowedvalues["allowedvalues"+i];
				$scope.resetError(ctrl);
				if (ctrl.$viewValue){
					$scope.validateSingleValue($scope.propObj, ctrl.$viewValue, ctrl, false);
				}
				
			}
		}
		$scope.resetError($scope.propform.defaultvalue);
		if ($scope.propform.defaultvalue.$viewValue){
			$scope.validateSingleValue($scope.propObj, $scope.propform.defaultvalue.$viewValue, $scope.propform.defaultvalue, true);
		}
		
		return $scope.propform.defaultvalue.$valid && $scope.propform.allowedvalues.$valid;
	};
	
	$scope.resetDefaultAndAllowedCtrls=function(){
		//form is not ready yet
		if (!angular.isDefined($scope.propform)){
			return;
		}
		if ($scope.extraprops.allowedvalues.length>0) {
			for (var i=0; i<$scope.extraprops.allowedvalues.length; i++){
				var ctrl=$scope.propform.allowedvalues["allowedvalues"+i];
				$scope.resetError(ctrl);
			}
		}
		$scope.resetError($scope.propform.defaultvalue);
					
	};
	//set error for ngModel
	$scope.resetError=function(ctrl){
		for(var key in ctrl.$error){
			ctrl.$setValidity(key, true);
		}
	};
	
	//validate single value for default or allowed
	$scope.validateSingleValue=function(prop, viewValue1, ctrl, checkAllow){
		if (!viewValue1){
			return undefined;
		}
		var proptype=prop.getTypeObject();
		
		var viewValue=viewValue1;
		
		//check type
		var typepat=null;
		var errorkey=null;
		if (proptype.isDateTime()){
			viewValue=proptype.parse(viewValue);
			if (viewValue===null){
				ctrl.$setValidity("date", false);
				return undefined;
			}
		}
		if (proptype.isURL()){
			typepat=flexdms.URL_REGEXP;
			errorkey="url";
		}else if (proptype.isInteger()){
			errorkey="integer";
			typepat=flexdms.INTEGER_REGEXP;
		} else if (proptype.isNumber()){
			errorkey="number";
			typepat=flexdms.NUMBER_REGEXP;
		} else if (proptype.isEmail()){
			errorkey="email";
			typepat=flexdms.EMAIL_REGEXP;
		} else if (proptype.isRelation()){
			errorkey="integer";
			typepat=flexdms.INTEGER_REGEXP;
		}
		if (typepat!=null){
			if (!typepat.test(viewValue)) {
				ctrl.$setValidity(errorkey, false);
				return undefined;
			}
		}

		var strpat=prop.getPatExp();
		if (strpat!=null){
			if (!strpat.test(viewValue)) {
				ctrl.$setValidity("pattern", false);
				return undefined;
			}
		}
		var minlength=prop.getMinLen();
		if (minlength!=null){
			var m=minlength-0;
			if (viewValue.length<m) {
				ctrl.$setValidity("minlength", false);
				return undefined;
			}
		}
		var maxlength=prop.getMaxLen();
		if (maxlength!=null){
			var m=maxlength-0;
			if (viewValue.length>m) {
				ctrl.$setValidity("maxlength", false);
				return undefined;
			}
		}
		//type conversion
		if (proptype.isNumber()){
			viewValue=proptype.parse(viewValue);
		}
		//number is expect for relation
		if (proptype.isRelation()){
			viewValue=viewValue-0;
		}
		var min=prop.getMinValue();
		if (min!=null){
			var m=min-0;
			if (viewValue<m) {
				ctrl.$setValidity("min", false);
				return undefined;
			}
		}
		var max=prop.getMaxValue();
		if (max!=null){
			var m=max-0;
			if (viewValue>m) {
				ctrl.$setValidity("max", false);
				return undefined;
			}
		}
		if (checkAllow && prop.getAllowedValues()!=null){
			var findAllowed=false;
			angular.forEach(prop.getAllowedValues(), function(v){
				if (v.value==viewValue){
					findAllowed=true;
				}
			});
			if (!findAllowed){
				ctrl.$setValidity("allowed", false);
			}
		}
		return viewValue;
		
	};
	
	$scope.addAllowed=function(){
		if ($scope.extraprops.allowedvalues==null){
			$scope.extraprops.allowedvalues=[];
		}
		$scope.extraprops.allowedvalues.push({value:'', display:''});
	};
	$scope.getDefaultControl=function(){
		return $scope.propform.defaultvalue;
	};
	$scope.canAllowed=function(){
		return $scope.canDefault();
	};
	$scope.canDefault=function(){
		$scope.propObj.getTypeObject();
		if ($scope.propObj.proptype.isCustomObject()){
			return false;
		}
		//can be element collection
		if ($scope.propObj.proptype.isStringType()){
			return true;
		}
		if ($scope.propObj.proptype.isNumber()){
			return true;
		}
		if ($scope.propObj.proptype.isDateTime()){
			return true;
		}
		if ($scope.propObj.proptype.isRelation()){
			return true;
		}
		if ($scope.propObj.proptype.isTrueFalse()){
			return true;
		}
		return false;
	};

	$scope.validateAlloweds=function(){
		if ($scope.propObj.getAllowedValues()==null || $scope.propObj.getAllowedValues().length==0){
			return;
		}
	};
}

//------------------------------Add properties.
function addOrEditProp($scope, Type, $stateParams, $modal, $state){
	
	agumentScopeForOrder($scope);
	agumentScopeForDefaultAndAllowed($scope);
	
	/**
	 * Add entity or embedded type to the list of property type.
	 */
	$scope.establishTypes=function(){
		var alltypes=angular.copy(flexdms.basic_types);
		
		
		//add embedded
		var types=[];
		angular.forEach($scope.types, function(t){
			if (!t.isEntity()) {
				types.push(flexdms.createEmbeddedPropType(t));
			}
		});
		types.sort(function(a, b){
			return a.value.localeCompare(b.value);
		});
		angular.forEach(types, function(t){
			alltypes.push(t);
		});
			
		//add entity
		types=[];
		angular.forEach($scope.types, function(t){
			if (t.isEntity()) {
				types.push(flexdms.createRelationPropType(t));
			}
		});
		types.sort(function(a, b){
			return a.value.localeCompare(b.value);
		});
		angular.forEach(types, function(t){
			alltypes.push(t);
		});
		return alltypes;
	};
	
	//default collectionType for a property type
	$scope.decideCollection=function(){
		var proptype=$scope.propObj.getTypeObject();
		if (proptype.isPrimitive() ){
			if ($scope.multiple){
				$scope.propObj.collectionType="element-collection";
			} else {
				$scope.propObj.collectionType="basic";
			}
		} else if (proptype.isEmbedded()){
			if ($scope.multiple){
				$scope.propObj.collectionType='element-collection';
			} else {
				$scope.propObj.collectionType='embedded';
			}
		} else {
			$scope.propObj.collectionType='one-to-one';
		}
	};
	//potential owner relation.
	$scope.findMappedBys=function(){
		var relatedType=flexdms.findType($scope.propObj.getTypeObject().value);
		var relatedProps=new Array();
		if (!relatedType){
			return relatedProps;
		}
		
		/*
		 * For those props
		 * 1. is a relation
		 * 2. refers to current type
		 * 3. relation owner.
		 * 4. not mapped by other props yet
		 * 5. has reverse caredinality
		 * 
		 */
	
		angular.forEach(relatedType.getProps(), function(propObj){
			//rule 1.
			if (!propObj.isRelation()){
				return;
			}
			//rule 2
			if (propObj.getTypeObject().value!=$scope.typetop.getName()){
				return;
			}
			//rule 3 
			var inverseprop=propObj.findInverseProp();
			if (inverseprop==null){
				//and rule 4.
				//no inverse, current property can be inverse
				if (!propObj.isOneToMany()){
					//one to many can not be owner of an inverse relation
					relatedProps.push(propObj.getName());
				}
				
			} 
		});
		/**
		 * If we are edit a property, we have already have mapped by, this mapped  by is excluded by rule 4.
		 * We need to add it back
		 * 
		 */
		if ($scope.propaction=='edit' && $scope.propObj.getMappedBy()){
			relatedProps.push($scope.propObj.getMappedBy());
		}
		return relatedProps;
	};
	
	//process property that is bound to scope, but should be transferred to prop
	$scope.processExtraProps=function()
	{
		$scope.propObj.setRequired($scope.extraprops.notnullable);
		$scope.propObj.setDisplayText($scope.extraprops.display);
		$scope.propObj.setTooltip($scope.extraprops.tooltip);
		
		if ($scope.extraprops.fileupload && $scope.propObj.getTypeObject().isFileType()){
			$scope.propObj.setFileUpload($scope.extraprops.fileupload);
		} else{
			$scope.propObj.setFileUpload(null);
		}
		
		if ($scope.propObj.getTypeObject().isCustomObject() && $scope.extraprops.objectclass!=null){
			$scope.propObj.setRootClass( $scope.objectclass);
		} else{
			$scope.propObj.setRootClass(null);
		}
		if ($scope.extraprops.viewable){
			$scope.propObj.setViewable($scope.extraprops.viewable);
		} else{
			$scope.propObj.setViewable(null);
		}
			
		if ($scope.extraprops.editable){
			$scope.propObj.setEditable($scope.extraprops.editable);
		} else {
			$scope.propObj.setEditable(null);
		}
		if($scope.propObj.getTypeObject().isStringType()){
			if ($scope.extraprops.minlen!=null){
				$scope.propObj.setMinLen($scope.extraprops.minlen);
			}else{
				$scope.propObj.setMinLen(null);
			}
			if ($scope.extraprops.maxlen!=null){
				$scope.propObj.setMaxLen($scope.extraprops.maxlen);
			}else{
				$scope.propObj.setMaxLen(null);
			}
			if ($scope.extraprops.pattern!=null){
				$scope.propObj.setPattern($scope.extraprops.pattern);
				$scope.propObj.setIgnoreCaseForPattern( $scope.extraprops.ignorepatterncase?'true':'false');
			}else{
				$scope.propObj.setPattern(null);
				$scope.propObj.setIgnoreCaseForPattern( null);
				
			}
		}
		if($scope.propObj.getTypeObject().isNumber()){
			if ($scope.extraprops.minvalue!=null){
				$scope.propObj.setMinValue($scope.extraprops.minvalue);
			}else{
				$scope.propObj.setMinValue(null);
			}
			if ($scope.extraprops.maxvalue!=null){
				$scope.propObj.setMaxValue($scope.extraprops.maxvalue);
			}else{
				$scope.propObj.setMaxValue(null);
			}
		}

		if ($scope.extraprops.autogenerate){
			$scope.propObj.setAutogenerate($scope.extraprops.autogenerate);
		} else {
			$scope.propObj.setAutogenerate(null);
		}
		if ($scope.extraprops.summaryprop){
			$scope.propObj.setSummaryProp(true);
		} else {
			$scope.propObj.setSummaryProp(null);
		}
		if ($scope.extraprops.defaultvalue!=null){
			$scope.propObj.setDefaultValue( $scope.extraprops.defaultvalue);
		} else {
			$scope.propObj.setDefaultValue( null);
		}
		
		if ($scope.extraprops.allowedvalues.length>0){
			$scope.propObj.setAllowedValues($scope.extraprops.allowedvalues);
		} else{
			$scope.propObj.setAllowedValues(null);
		}
		
		$scope.setOrder();
		
		
	};
	
	

	$scope.checkName=function()
	{
		if (typeof($scope.prop['@name'])=='undefined')
			return;
		
		Type.checkName({typename:$scope.typename, propname:$scope.prop['@name']}, function(data, headers){
			if (data.appMsg.statuscode!=0)
			{
				$scope.propform.name.$valid=false;
				$scope.propform.name.$invalid=true;
				$scope.propform.name.$error.badname=data.appMsg.msg;
					
			}else {
				$scope.propform.name.$valid=true;
				$scope.propform.name.$invalid=false;
				$scope.propform.name.$error.badname=false;
			}
		});
	};
	
	$scope.checkCustomClass=function(){
		//TODO implement this
		$scope.propform.objectclass.$invalid=true;
		$scope.propform.objectclass.$valid=false;
		$scope.propform.objectclass.$error.badclass="invalid class";
	};
	
	$scope.assignDefaultContainerType=function(){
		if (!angular.isDefined($scope.prop['@attribute-type'])){
			$scope.prop['@attribute-type']="java.util.List";
			return;
		}
		//keep the old one.
		if ($scope.prop['@attribute-type']=='java.util.List' ||$scope.prop['@attribute-type']=='java.util.Set' ||$scope.prop['@attribute-type']=='java.util.Map' ||$scope.prop['@attribute-type']=='java.util.Collection'){
			return;
		}
		//use default one.
		$scope.prop['@attribute-type']="java.util.List";
	};
	
	/*$scope.getPropIdx=function(propname, collectionType){
		for (var i=0; i<$scope.type.attributes[collectionType].length; i++){
			var prop=$scope.type.attributes[collectionType][i];
			if (prop['@name']==propname){
				return i;
			}
		}
		return -1;
		
	};*/
	$scope.removeProp=function(prop, collectionType){
		for (var i=0; i<$scope.type.attributes[collectionType].length; i++){
			var prop1=$scope.type.attributes[collectionType][i];
			if (prop===prop1){
				$scope.type.attributes[collectionType].splice(i, 1);
				return;
			}
		}
	};
	
	
	$scope.openRegexVerify=function(){
		 $modal.open({
		      templateUrl: 'type/regexp.html',
		      controller: "regexpCtrl",
		      scope:$scope
		    });
	};
	
	$scope.canPrivate=function(){
		if ($scope.propObj.isOneToOne() || $scope.propObj.isOneToMany()){
			return true;
		}
		return false;
	};
	
	$scope.canBasicMultiple=function(){
		$scope.propObj.getTypeObject();
		//can be element collection
		if ((!$scope.propObj.proptype.isRelation()&& $scope.propObj.proptype.canHaveMultiple())||$scope.propObj.proptype.isEmbedded()){
			return true;
		}
		return false;
	};
	
	
	
	$scope.resetPropType=function(){
		var proptype=$scope.propObj.getTypeObject();
		if (proptype.idx>=0 && proptype.idx<flexdms.basic_types.length){
			$scope.propObj.proptype=$scope.proptypes[proptype.idx];
			return;
		}
		
		for (var i=flexdms.basic_types.length; i++; i<$scope.proptypes.length){
			if ($scope.proptypes[i].idx==proptype.idx && $scope.proptypes[i].value==proptype.value){
				$scope.propObj.proptype=$scope.proptypes[i];
				return;
			}
		}
	};
	
	
}
