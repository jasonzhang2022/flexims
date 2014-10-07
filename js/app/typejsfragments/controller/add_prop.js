

typeApp.controller("addpropCtrl", 
                                   function($scope, Type, $stateParams, $modal, $state){
	
	
	function createExtraProps(){
		return {
			notnullable:false,
			fileupload:false,
			objectclass:null,
			viewable:true,
			editable:true,
			minlen:null,
			maxlen:null,
			minvalue:null,
			maxvalue:null,
			autogenerate:true,
			summaryprop:false,
			defaultvalue:null,
			pattern:null,
			ignorepatterncase:true,
			allowedvalues:[],
			tooltip:null,
			display:null,
			orderby:null,
			
		};
	}
	$scope.propaction="add";
	addOrEditProp($scope, Type, $stateParams, $modal, $state);
	$scope.init=function(){
		
		//control which sections in add props to display initially
		$scope.advanceattrs=true;
		$scope.relationattrs=false;
		$scope.basicattrs=false;
		
		//type constants
		$scope.proptypes=$scope.establishTypes();
		
		//object is ready for edit
		$scope.propObj=new flexdms.Property(flexdms.createEmptyBasicProperty());
		$scope.propObj.type=$scope.typetop;
		$scope.propObj.setTypeObject($scope.proptypes[0]);
		$scope.prop=$scope.propObj.prop;
	};
	
	
	
	
	$scope.initDefaultStateForPropType=function(){
		var proptype=$scope.propObj.getTypeObject();
		if (proptype.isPrimitive() ) {
			if (!proptype.canHaveMultiple() && $scope.multiple) {
				$scope.multiple=false;
			}
			if (!proptype.canUnique() && $scope.propObj.prop.column['@unique']){
				$scope.propObj.prop.column['@unique']=false;	
			}
		} 
		
		//default collectionType for 
		$scope.decideCollection();
		
		//initialized as false.
		//DO not need this: Collection change will change private by cascade
		//$scope.prop['private-owned']=false;
		$scope.propObj.flushType();
		$scope.potentialMappedbys=$scope.findMappedBys();
		$scope.prop['@mapped-by']=null;
		$scope.extraprops=createExtraProps();
		$scope.processExtraProps();
		$scope.resetDefaultAndAllowedCtrls();
	};
	
	
	
	$scope.init();
	//*****************Communicate with server to check name instantly.
	
	$scope.$watch("propObj.proptype", function(newValue, oldValue, scope){
		$scope.initDefaultStateForPropType();
	});
	
	$scope.$watch("multiple", function(value){
		if ($scope.propObj.getTypeObject().isRelation()){
			return;
		}
		$scope.decideCollection();
		if ($scope.propObj.collectionType=='element-collection'){
			$scope.propObj.prop.column['@unique']=false;
		} 
	});
	
	/*
	 * Trigger from default assign based on property type or use interaciton.
	 */
	$scope.$watch("propObj.collectionType", function(newValue, oldValue){
		if ($scope.propObj.isCollection()){
			//decide the default collection type for items: List, Set, Map, Collection
			$scope.assignDefaultContainerType();
		} 
		/*
		 * Why this?
		 * If no type is changed, but the collection<->Single property is changed. We need to switch between target-class/target-entity<->attribute->type
		 */
		$scope.propObj.flushType();
		
		//special handle for private
		if (!$scope.canPrivate() && $scope.prop['private-owned']){
			//not applicable, we could delete this
			$scope.prop['private-owned']=false;
		}
		//move property from the type->collection to new one.
		var propname=$scope.propObj.getName();
		$scope.removeProp($scope.prop, oldValue);
		$scope.type.attributes[$scope.propObj.collectionType].push($scope.prop);
		$scope.potentialMappedbys=$scope.findMappedBys();
		
		
	});
	
	
	//synchronize multiple variable corresponding to private
	$scope.$watch("prop['private-owned']", function(value){
		if (!$scope.propObj.getTypeObject().isRelation()){
			return;
		}
		//synchronize value for private management.
		if (angular.isDefined(value)){
			//do not use this, foreign key is not established correctly in postgrseql.
			//We rely on cascade at persistence lay instead of database
			//$scope.prop['cascade-on-delete']=value;
			$scope.prop['@orphan-removal']=value;
		}
		if (value){
			$scope.prop["cascade"]={
					'cascade-remove':true
			};
		} else {
			delete($scope.prop["cascade"]);
		}
	});
	
	//cardinality has to be inverse of of the owner.
	$scope.$watch("prop['@mapped-by']", function(value){
		if (!$scope.propObj.getTypeObject().isRelation()){
			return;
		}
		//synchronize value for private management.
		if (value){
			var ownerprop=flexdms.findType($scope.propObj.getTypeObject().value).getProp(value);
			if (ownerprop.isOneToOne()){
				$scope.propObj.collectionType="one-to-one";
			} else  if (ownerprop.isManyToMany()){
				$scope.propObj.collectionType="many-to-many";
			} else  if (ownerprop.isManyToOne()){
				$scope.propObj.collectionType="one-to-many";
			} 
		}
	});
	
	
	//have this a separate function so it can be tested.
	$scope.presave=function(){
		$scope.processExtraProps();
		
		$scope.typetop.cleanCache();
		
	};
	$scope.saveProp=function(){
		
		
		$scope.presave();
		if (!$scope.validateDefaultAndAllowed()){
			return;
		}
		$scope.typetop.$saveprop(null, function(value){
			flexdms.reloadTypes(Type);
			$state.go("viewtype", {typename:value.getName()});
		});
	};
	
	$scope.cancelProp=function(){
		
		for (var i=0; i<$scope.type.attributes[$scope.propObj.collectionType].length; i++){
			if ($scope.type.attributes[$scope.propObj.collectionType][i]===$scope.prop){
				$scope.type.attributes[$scope.propObj.collectionType].splice(i, 1);
				break;
			}
		}
		$state.go("viewtype", {typename:$scope.typename});
	};
	
	
	
});


	