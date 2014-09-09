angular.module("typetest", ["type", "ui.typetest.tpl"]);
describe(
		"type.js",
		function() {
			var $rootScope = null;
			var $http = null;
			var $controller = null;
			var $myInjector = null;

			beforeEach(function(){
				flexdms.typemeta=meta;
			});
			beforeEach(module("typetest"));

			// Store references to $rootScope and $compile
			// so they are available to all tests in this describe block
			beforeEach(inject(function($injector) {

				$http = $injector.get("$httpBackend");
				$rootScope = $injector.get("$rootScope");
				$controller = $injector.get('$controller');
				$myInjector = $injector;
				
				 $http.whenGET(appctx.modelerprefix+"/rs/type/meta").respond(meta);
				 //we do not test html
				 $http.whenGET("type/addtype.html").respond("<html></html>");
				 $http.whenGET("type/addprop.html").respond("<html></html>");
				 $http.whenGET("type/types.html").respond("<html></html>");
			}));

			afterEach(function() {
				$http.verifyNoOutstandingRequest();
				$http.verifyNoOutstandingExpectation();
			});

			
			it("Add Type Controller", function() {
				var scope = $rootScope.$new();
				var Type= $myInjector.get("Type");
				
				$controller("addtypeCtrl", {
					$scope : scope,
					Type: Type,
					types: flexdms.types
				});
				
				//watcher is fired for initial value.
				scope.$digest(); //fire all watcher
				
				//abstract
				expect(flexdms.getExtraProp(scope.type, "abstract")).toBeFalsy();
				scope.abstracttype=true;
				scope.$digest(); //fire all watcher
				expect(flexdms.getExtraProp(scope.type, "abstract")).toBeTruthy();
				
				expect(scope.supers.length>10).toBeTruthy();
				expect(scope.type).toBeDefined();
				expect(scope.realType).toBeDefined();
				expect(scope.realType['entity-mappings'].entity[0]).toBe(scope.type);
				
				//add prop.
				expect(scope.type.property.length).toEqual(1);
				scope.addTypeProp();
				expect(scope.type.property.length).toEqual(2);
				scope.type.property[0]['@name']='prop1';
				scope.addTypeProp();
				expect(scope.type.property.length).toEqual(3);
				scope.type.property[1]['@name']='prop2';
				
				//remove proprty
				scope.removeTypeProp(0);
				expect(scope.type.property.length).toEqual(2);
				expect(scope.type.property[0]['@name']).toEqual('prop2');

				
				//watch embedded
				expect(scope.realType['entity-mappings'].entity.length).toEqual(1);
				expect(scope.realType['entity-mappings'].embeddable.length).toEqual(0);
				scope.extension=true;
				scope.embedded=true;
				scope.$digest(); //fire all watcher
				expect(scope.realType['entity-mappings'].entity.length).toEqual(0);
				expect(scope.realType['entity-mappings'].embeddable.length).toEqual(1);
				expect(scope.extension).toBeFalsy();
				scope.embedded=false;
				scope.$digest();
				expect(scope.realType['entity-mappings'].entity.length).toEqual(1);
				expect(scope.realType['entity-mappings'].embeddable.length).toEqual(0);
				
				//extnesion.
				scope.extension=true;
				scope.$digest(); //fire all watcher
				scope.realType.cleanCache();
				expect(scope.realType.getProp("fxExtraProp")).not.toBeNull();
				scope.extension=false;
				scope.$digest(); //fire all watcher
				scope.realType.cleanCache();
				expect(scope.realType.getProp("fxExtraProp")).not.toBeDefined();
				
				
			
			
				
			});
			
			
			function createTypeCtrl(type1){
				var type=type1;
				if (!angular.isDefined(type)){
					type="Test";
				}
				var typescope=$rootScope.$new();
				var Type=$myInjector.get("Type");
				
			
				$controller("typeCtrl", {
					$scope : typescope,
					Type: Type,
					$stateParams:{typename: type}
				});
				expect(typescope.typetop).toBeDefined();
				return typescope;
				
			}
			function prepareAddPropCtrl(type1){
				var type=type1;
				if (!angular.isDefined(type)){
					type="Test";
				}
				var typescope=createTypeCtrl(type);
				
				var scope = typescope.$new();
				
				$controller("addpropCtrl", {
					$scope : scope,
					Type: $myInjector.get("Type"),
					$stateParams:{typename: type}
				});
				//watcher is fired for initial value.
				scope.$digest(); //fire all watcher
				return scope;
			}
			function prepareEditPropCtrl(type1, propname){
				var type=type1;
				if (!angular.isDefined(type)){
					type="Test";
				}
				
				var typescope=createTypeCtrl(type);
				
				var scope = typescope.$new();
				
				$controller("editpropCtrl", {
					$scope : scope,
					Type: $myInjector.get("Type"),
					$stateParams:{typename: type, propname: propname}
				});
				//watcher is fired for initial value.
				scope.$digest(); //fire all watcher
				return scope;
			}
			it("Add prop Controller: initialization", function() {
				var scope= prepareAddPropCtrl();
				
				
				//check initial value;
				expect(scope.typename).toEqual('Test');
				expect(scope.type).toBeDefined();
				expect(scope.proptypes.length>(flexdms.basic_types.length+1)).toBeTruthy();
				expect(scope.prop).toBeDefined();
				expect(scope.propObj.proptype).toBeDefined();
				expect(scope.propObj.proptype.value).toEqual("java.lang.String");
			});
			
			it("Add prop Controller: multiple checkbox", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				
				//type is string.
				//watch on collection.
				scope.multiple=true;
				scope.propObj.prop.column['@unique']=true;
				scope.$digest();
				expect(scope.propObj.collectionType).toEqual('element-collection');
				expect(scope.propObj.prop.column['@unique']).toBeFalsy();//can not be unique for collection
				
				
			});
			
			it("Add prop Controller: Odrer By for Basic", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				
				//type is string.
				//watch on collection.
				scope.multiple=true;
				scope.propObj.prop.column['@unique']=true;
				//scope.propObj.prop['@attribute-type']='java.util.List';
				scope.$digest();
				expect(scope.propObj.collectionType).toEqual('element-collection');
				expect(scope.propObj.prop.column['@unique']).toBeFalsy();//can not be unique for collection
				
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).not.toBeDefined();
				
				scope.extraprops.orderby='ByValue';
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).toBeDefined();
				
				scope.extraprops.orderby='ByIndex';
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).not.toBeDefined();
				expect(flexdms.getExtraProp(scope.prop, 'orderColumn')).toEqual("true");
				
				scope.propObj.prop['@attribute-type']='java.util.Set';
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).not.toBeDefined();
				expect(flexdms.getExtraProp(scope.prop, 'orderColumn')).toBeNull();
			});
			
			
			it("Add prop Controller: Medium String type", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				//*******************check medium string
				scope.multiple=true;
				scope.propObj.prop.column['@unique']=true;
				scope.propObj.proptype=flexdms.basic_types[1]; //medium string
				scope.$digest(); //fire all watcher
				expect(scope.prop.column['@length']).toEqual(4096);
				expect(scope.prop.lob).toBeUndefined();
				expect(scope.propObj.collectionType).toEqual("basic"); //medium string can not be collection
				expect(scope.propObj.prop.column['@unique']).toBeFalsy(); //medium string can not be unique
				expect(scope.propObj.collectionType).toEqual('basic');
				expect(scope.multiple).toBeFalsy();
			});
			it("Add prop Controller: long String type", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				//*******************check medium string
				scope.multiple=true;
				scope.propObj.prop.column['@unique']=true;
				scope.propObj.proptype=flexdms.basic_types[2]; //long string
				scope.$digest(); //fire all watcher
				expect(scope.prop.column['@columnDefinition']).toEqual('text');
				expect(scope.prop.lob).toBeDefined();
				expect(scope.prop.column['@length']).toEqual(4096);
				expect(scope.propObj.collectionType).toEqual("basic"); //long string can not be collection
				expect(scope.propObj.prop.column['@unique']).toBeFalsy(); //long string can not be unique
				expect(scope.propObj.collectionType).toEqual('basic');
				expect(scope.multiple).toBeFalsy();
			});
			it("Add prop Controller: Integer type", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				//*******************integer
				scope.multiple=true;
				scope.propObj.prop.column['@unique']=true;
				scope.propObj.proptype=flexdms.basic_types[3]; //integer
				scope.$digest(); //fire all watcher
				expect(scope.prop.column['@columnDefinition']).toBeUndefined();
				expect(scope.prop.lob).toBeUndefined();
				expect(scope.propObj.collectionType).toEqual('element-collection');
				expect(scope.multiple).toBeTruthy();
				expect(scope.propObj.prop.column['@unique']).toBeFalsy(); //collection can not be unique;
				
			});
			it("Add prop Controller: Date time type", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				//*******************date and time type
				expect(scope.prop.temporal).toBeUndefined();
				scope.propObj.proptype=flexdms.basic_types[11];
				scope.$digest();
				expect(scope.prop.temporal).toEqual('DATE');
				scope.propObj.proptype=flexdms.basic_types[0];
				scope.$digest();
				expect(scope.prop.temporal).toBeUndefined();
			});
			
			it("Add prop Controller: currecny", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				
				//*******************currency
				scope.propObj.proptype=flexdms.basic_types[7];
				scope.$digest(); //fire all watcher
				expect(scope.prop['@attribute-type']).toEqual("java.math.BigDecimal");
				expect(scope.prop.column['@precision']).toEqual(20);
				expect(scope.prop.column['@length']).toBeUndefined(); //length will override scale and precision.
			});
			
			it("Add prop Controller: byte array", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				
				//*******************byte array
				scope.multiple=true;
				scope.propObj.prop.column['@unique']=true;
				scope.propObj.proptype=flexdms.basic_types[8];
				scope.$digest(); //fire all watcher
				expect(scope.prop['@attribute-type']).toEqual("com.flexdms.flexims.jpa.helper.ByteArray");
				expect(scope.prop.column['@length']).toBeUndefined(); //length will override scale and precision.
				expect(scope.propObj.collectionType).toEqual("basic"); //long string can not be collection
				expect(scope.propObj.prop.column['@unique']).toBeFalsy(); //long string can not be unique
				expect(scope.propObj.collectionType).toEqual('basic');
				expect(scope.multiple).toBeFalsy();
			});
			it("Add prop Controller:Custom object", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				//*******************custom object
				scope.multiple=true;
				scope.propObj.prop.column['@unique']=true;
				scope.propObj.proptype=flexdms.basic_types[10];
				scope.$digest(); //fire all watcher
				scope.processExtraProps(); //no watcher on name any more.
				expect(scope.prop['@attribute-type']).toEqual("java.lang.String");
				expect(scope.prop.column['@columnDefinition']).toEqual('text');
				expect(scope.prop.lob).toBeDefined();
				expect(scope.propObj.collectionType).toEqual("basic"); //long string can not be collection
				expect(scope.propObj.prop.column['@unique']).toBeFalsy(); //long string can not be unique
				expect(scope.propObj.prop.column['@length']).toEqual(4096);
				expect(scope.propObj.collectionType).toEqual("basic"); //long string can not be collection
				expect(scope.propObj.prop.column['@unique']).toBeFalsy(); //long string can not be unique
				expect(scope.propObj.collectionType).toEqual('basic');
				expect(scope.multiple).toBeFalsy();
				
				//when custom class is set, extra property is synchronized.
				scope.extraprops.objectclass="goodclass";
				scope.$digest(); 
				scope.processExtraProps(); 
				expect(flexdms.getExtraProp(scope.prop, 'rootClass')).toEqual(scope.objectclass);
				
			});
			it("Add prop Controller: required attribute", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				//*******************Check required
				expect(scope.prop.column['@nullable']).toBeTruthy();
				scope.extraprops.notnullable=true;
				scope.$digest(); 
				scope.processExtraProps(); //no watcher on name any more.
				expect(scope.prop.column['@nullable']).toBeFalsy();
			});
			it("Add prop Controller: File upload", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				//*******************Check file upload
				scope.multiple=true;
				scope.propObj.proptype=flexdms.basic_types[16];
				scope.$digest();
				scope.extraprops.fileupload=true;
				scope.processExtraProps(); //no watcher on name any more.
				expect(scope.propObj.isFileUpload()).toBeTruthy();
				
				//************check 
				scope.prop['@attribute-type']="java.util.Set";
				expect(scope.propObj.collectionType).toEqual("element-collection"); //long string can not be collection
				scope.$digest();
				expect(scope.prop['@target-class']).toEqual(flexdms.basic_types[16].value);
				expect(scope.prop['@attribute-type']).toEqual("java.util.Set");
			});
			
			it("Add prop Controller: Embeded", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				//embedded
				var embedtype=null;
				for(var i=0; i<scope.proptypes.length; i++){
					if (scope.proptypes[i].value=='Embed1'){
						embedtype=scope.proptypes[i];
						break;
					}
				}
				
				
				scope.multiple=false;
				scope.propObj.prop.column['@unique']=true;
				scope.propObj.proptype=embedtype;
				scope.$digest(); //fire all watcher
				scope.processExtraProps(); //no watcher on name any more.
				expect(scope.prop['@attribute-type']).toEqual("Embed1");
				expect(scope.propObj.collectionType).toEqual("embedded"); //long string can not be collection
				expect(scope.propObj.prop.column['@unique']).toBeFalsy();
				expect(scope.multiple).toBeFalsy();
				
				scope.multiple=true;
				scope.prop['@attribute-type']="java.util.Set";
				scope.$digest(); //fire all watcher
				scope.processExtraProps(); //no watcher on name any more.
				scope.presave();
				expect(scope.prop['@target-class']).toEqual("Embed1");
				expect(scope.prop['@attribute-type']).toEqual("java.util.Set");
				expect(scope.propObj.collectionType).toEqual("element-collection"); //long string can not be collection
				expect(scope.propObj.prop.column['@unique']).toBeFalsy();
				expect(scope.multiple).toBeTruthy();
				
				
			});
			
			it("Add prop Controller: basic Relation", function() {
				var scope= prepareAddPropCtrl("Mcourse");
				scope.prop['@name']="prop1";
				//embedded
				var reltype=null;
				for(var i=0; i<scope.proptypes.length; i++){
					if (scope.proptypes[i].value=='Mdoomroom'){
						reltype=scope.proptypes[i];
						break;
					}
				}
				

				scope.propObj.proptype=reltype;
				scope.$digest(); //fire all watcher
				scope.processExtraProps(); //no watcher on name any more.
				expect(scope.propObj.collectionType).toEqual("one-to-one"); //default relationship
				expect(scope.prop['@target-entity']).toEqual("Mdoomroom");
				expect(scope.prop['@attribute-type']).toBeNull();
			});
			it("Add prop Controller: Inverse Relation new", function() {
				var scope= prepareAddPropCtrl("Mcourse");
				
				
				scope.prop['@name']="prop1";
				//embedded
				var reltype=null;
				for(var i=0; i<scope.proptypes.length; i++){
					if (scope.proptypes[i].value=='Mstudent'){
						reltype=scope.proptypes[i];
						break;
					}
				}
				
				//We need to remove students to make the mapped by works
				var oldprops=scope.type.attributes["many-to-many"];
				scope.type.attributes["many-to-many"]=new Array();
				
				
				scope.propObj.proptype=reltype;
				scope.propaction="add";
				scope.$digest(); //fire all watcher
				scope.processExtraProps(); //no watcher on name any more.
				expect(scope.propObj.collectionType).toEqual("one-to-one"); 
				expect(scope.prop['@target-entity']).toEqual("Mstudent");
				expect(scope.potentialMappedbys.length).toBe(1); 
				
				
				
				//select one owner relation
				scope.prop['@mapped-by']=scope.potentialMappedbys[0];
				scope.$digest(); //fire all watcher
				expect(scope.propObj.collectionType).toEqual("many-to-many"); //inverse to the owner relation. 
				expect(scope.prop['@target-entity']).toEqual("Mstudent");
				expect(scope.type.attributes["many-to-many"].length).toBe(1);
				
				scope.type.attributes["many-to-many"]=oldprops;
			});
			
			it("Add prop Controller: Order By for relation", function() {
				var scope= prepareEditPropCtrl("Mstudent", "Courses");
				expect(scope.propObj.collectionType).toEqual("many-to-many"); 
				
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).not.toBeDefined();
				
				//--------------set
				scope.extraprops.orderby="ByValue";
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).toBeDefined();
				
				scope.extraprops.orderby="ByIndex";
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).not.toBeDefined();
				expect(flexdms.getExtraProp(scope.prop, 'orderColumn')).toEqual("true");
				
				scope.propObj.prop['@attribute-type']='java.util.Set';
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).not.toBeDefined();
				expect(flexdms.getExtraProp(scope.prop, 'orderColumn')).toBeNull();
				
				scope.propObj.prop['@attribute-type']='java.util.List';
				scope.extraprops.orderby="name";
				scope.orderasc=true;
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).toEqual("name ASC");
				expect(flexdms.getExtraProp(scope.prop, 'orderColumn')).toBeNull();
				
				scope.orderasc=false;
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).toEqual("name DESC");
				expect(flexdms.getExtraProp(scope.prop, 'orderColumn')).toBeNull();
				
				
				//---------init
				scope.propObj.prop['order-by']="name DESC";
				scope.extraprops.orderby=null;
				scope.orderasc=true;
				scope.initOrder();
				expect(scope.orderasc).toBeFalsy();
				expect(scope.extraprops.orderby).toEqual("name");
				
				
				scope.propObj.prop['order-by']={};
				scope.initOrder();
				expect(scope.extraprops.orderby).toEqual("ByValue");
				
				delete(scope.propObj.prop['order-by']);
				scope.propObj._internalSetExtraProp("orderColumn", "true");
				scope.initOrder();
				expect(scope.extraprops.orderby).toEqual("ByIndex");
				
				//restore value;
				delete(scope.propObj.prop['order-by']);
				scope.propObj._internalSetExtraProp("orderColumn", null);
				
			});
			
			it("Add prop Controller: Order By for relation With Mapped By", function() {
				var scope= prepareEditPropCtrl("Mcourse", "Students");
				expect(scope.propObj.collectionType).toEqual("many-to-many"); 
				expect(scope.prop['@mapped-by']).toEqual("Courses");
				
				//--------------set
				scope.extraprops.orderby="ByValue";
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).toBeDefined();
				
				//can not by Index for inverse relation.
				scope.extraprops.orderby="ByIndex";
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).not.toBeDefined();
				expect(flexdms.getExtraProp(scope.prop, 'orderColumn')).toBeNull(); 
				
				scope.propObj.prop['@attribute-type']='java.util.Set';
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).not.toBeDefined();
				expect(flexdms.getExtraProp(scope.prop, 'orderColumn')).toBeNull();
				
				scope.propObj.prop['@attribute-type']='java.util.List';
				scope.extraprops.orderby="name";
				scope.orderasc=true;
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).toEqual("name ASC");
				expect(flexdms.getExtraProp(scope.prop, 'orderColumn')).toBeNull();
				
				scope.orderasc=false;
				scope.setOrder();
				expect(scope.propObj.prop['order-by']).toEqual("name DESC");
				expect(flexdms.getExtraProp(scope.prop, 'orderColumn')).toBeNull();
				
			});
			
			
			it("Add prop Controller: Inverse Relation edit", function() {
				var scope= prepareAddPropCtrl("Mcourse");
				
				
				scope.prop['@name']="prop1";
				//embedded
				var reltype=null;
				for(var i=0; i<scope.proptypes.length; i++){
					if (scope.proptypes[i].value=='Mstudent'){
						reltype=scope.proptypes[i];
						break;
					}
				}
				
				//We need to remove students to make the mapped by works
				scope.propObj.proptype=reltype;
				scope.$digest(); //fire all watcher
				scope.processExtraProps(); //no watcher on name any more.
				expect(scope.propObj.collectionType).toEqual("one-to-one"); 
				expect(scope.prop['@target-entity']).toEqual("Mstudent");
				//why? this property is already mapped;
				expect(scope.potentialMappedbys.length).toBe(0); 
				
				
			});
			it("EDIT prop Controller: Inverse Relation edit", function() {
				var scope= prepareEditPropCtrl("Mcourse", "Students");
				
				//embedded
				var reltype=null;
				for(var i=0; i<scope.proptypes.length; i++){
					if (scope.proptypes[i].value=='Mstudent'){
						reltype=scope.proptypes[i];
						break;
					}
				}
				
				expect(scope.propObj.collectionType).toEqual("many-to-many"); 
				expect(scope.prop['@target-entity']).toEqual("Mstudent");
				//why? self 
				expect(scope.potentialMappedbys.length).toBe(1);
				
			});
			
			
			
			
			it("Add prop Controller: private relation", function() {
				var scope= prepareAddPropCtrl("Mcourse");
				scope.prop['@name']="prop1";
				//embedded
				var reltype=null;
				for(var i=0; i<scope.proptypes.length; i++){
					if (scope.proptypes[i].value=='Mstudent'){
						reltype=scope.proptypes[i];
						break;
					}
				}
				//We need to remove students to make the mapped by works
				var oldprops=scope.type.attributes["many-to-many"];
				scope.type.attributes["many-to-many"]=new Array();
				
				
				scope.propObj.proptype=reltype;
				scope.$digest(); //fire all watcher
				scope.processExtraProps(); //no watcher on name any more.
				expect(scope.propObj.collectionType).toEqual("one-to-one"); 
				expect(scope.prop['@target-entity']).toEqual("Mstudent");
				expect(scope.potentialMappedbys.length).toBe(1); //there is one potential relationship for mappby
				expect(scope.prop['private-owned']).toBeFalsy(); //default.
				
				//value is synchronized
				scope.prop['private-owned']=true;
				scope.$digest(); //fire all watcher
				expect(scope.prop['@orphan-removal']).toBeTruthy();
				//expect(scope.prop['cascade-on-delete']).toBeTruthy();
				expect(scope.prop['cascade']['cascade-remove']).toBeTruthy();
				
				//for relation that can not be private-owned,
				scope.prop['private-owned']=true;
				scope.propObj.collectionType="many-to-one";
				scope.$digest();
				expect(scope.prop['private-owned']).toBeFalsy(); //turn to falsy if the relation can not be truthy
				expect(scope.prop['cascade']).not.toBeDefined();
				
				scope.type.attributes["many-to-many"]=oldprops;
				
				
			});
			
			it("Add prop Controller: test allowed value", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/addprop.html");
				var $compile=$myInjector.get("$compile");
				var element=$compile(template)(scope);
				
				//*******************integer: single
				scope.propObj.proptype=flexdms.basic_types[3]; //integer
				scope.$digest(); 
				
				//add one allowed value
				scope.addAllowed();
				scope.addAllowed();
				scope.addAllowed();
				scope.$digest();
				
				//required should be set to invalid
				expect(scope.propform.allowedvalues.allowedvalues0.$error.required).toBeTruthy();
				expect(scope.propform.allowedvalues.allowedvalues1.$error.required).toBeTruthy();
				expect(scope.propform.allowedvalues.allowedvalues1.$error.required).toBeTruthy();
				
				scope.propform.allowedvalues.allowedvalues0.$setViewValue("x");
				scope.propform.allowedvalues.allowedvalues1.$setViewValue("5");
				scope.propform.allowedvalues.allowedvalues2.$setViewValue("8");
				scope.$digest();
				
				//integer is detected
				expect(scope.propform.allowedvalues.allowedvalues0.$error.integer).toBeTruthy();
				expect(scope.propform.allowedvalues.allowedvalues1.$valid).toBeTruthy();
				expect(scope.propform.allowedvalues.allowedvalues2.$valid).toBeTruthy();
				expect(scope.extraprops.allowedvalues[1].value).toBe(5);
				
				
				//when min value is changed, allowd value is revaluated.
				scope.propform.allowedvalues.allowedvalues0.$setViewValue("4");
				scope.$digest();
				scope.propform.minvalue.$setViewValue("5");
				scope.propform.maxvalue.$setViewValue("7");
				expect(scope.propform.allowedvalues.allowedvalues0.$error.min).toBeTruthy();
				expect(scope.propform.allowedvalues.allowedvalues1.$valid).toBeTruthy();
				expect(scope.propform.allowedvalues.allowedvalues2.$error.max).toBeTruthy();
				
				//when type is changed, allowed is cleared.
				scope.propObj.proptype=flexdms.basic_types[11]; //date
				scope.$digest(); 
				//allowed is cleared
				expect(scope.propform.allowedvalues.allowedvalues0).not.toBeDefined();
				
				//date is checked
				scope.addAllowed();
				scope.$digest();
				scope.propform.allowedvalues.allowedvalues0.$setViewValue("2007/0523");
				scope.$digest();
				expect(scope.propform.allowedvalues.allowedvalues0.$error.date).toBeTruthy();
				
				
				
				
			});
			
			it("Add prop Controller: test Default value", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/addprop.html");
				var $compile=$myInjector.get("$compile");
				$compile(template)(scope);
				
				//*******************integer: single
				scope.propObj.proptype=flexdms.basic_types[3]; //integer
				scope.$digest(); 
				
				
				
				
				scope.propform.defaultvalue.$setViewValue("x");
				scope.$digest();
				//integer is detected
				expect(scope.propform.defaultvalue.$error.integer).toBeTruthy();
				
				
				scope.propform.defaultvalue.$setViewValue("6");
				scope.$digest();
				expect(scope.propform.defaultvalue.$valid).toBeTruthy();
				
				//when min change, the default is checked.
				scope.propform.minvalue.$setViewValue("10");
				scope.$digest();
				expect(scope.propform.defaultvalue.$error.min).toBeTruthy();
				
				//checl allowed
				scope.addAllowed();
				scope.addAllowed();
				scope.addAllowed();
				scope.$digest();
				
				scope.propform.allowedvalues.allowedvalues0.$setViewValue("11");
				scope.propform.allowedvalues.allowedvalues1.$setViewValue("12");
				scope.propform.allowedvalues.allowedvalues2.$setViewValue("13");
				scope.$digest();
				var ret=scope.validateDefaultAndAllowed();
				expect(ret).toBeFalsy();
				scope.propform.defaultvalue.$setViewValue("14");
				expect(scope.propform.defaultvalue.$error.allowed).toBeTruthy();
				
			});
			
			
			it("Add prop Controller: test Default value, multiple values", function() {
				var scope= prepareAddPropCtrl();
				scope.prop['@name']="prop1";
				var templateCache = $myInjector.get("$templateCache");
				var template=templateCache.get("template/addprop.html");
				var $compile=$myInjector.get("$compile");
				$compile(template)(scope);
				
				//*******************integer: single
				scope.multiple=true;
				scope.propObj.proptype=flexdms.basic_types[3]; //integer
				scope.$digest(); 
				
				
				scope.propform.defaultvalue.$setViewValue("x");
				scope.$digest();
				//integer is detected
				expect(scope.propform.defaultvalue.$error.integer).toBeTruthy();
				
				scope.propform.defaultvalue.$setViewValue("6");
				scope.$digest();
				//integer is detected
				expect(scope.propform.defaultvalue.$valid).toBeTruthy();
				
				
				scope.propform.defaultvalue.$setViewValue("6,x");
				scope.$digest();
				//integer is detected
				expect(scope.propform.defaultvalue.$error.integer).toBeTruthy();
			
				scope.propform.defaultvalue.$setViewValue("6,7");
				scope.$digest();
				//integer is detected
				expect(scope.propform.defaultvalue.$valid).toBeTruthy();
				expect(scope.extraprops.defaultvalue).toEqual("6,7");
			});
		});