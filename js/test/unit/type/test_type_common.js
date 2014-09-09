describe(
		"type_common.js",
		function() {
			var $http = null;
			var $myInjector = null;
			
			beforeEach(function(){
				flexdms.typemeta=meta;
			});
			beforeEach(module("type"));
			beforeEach(inject(function($injector) {
				$http = $injector.get("$httpBackend");
				$rootScope = $injector.get("$rootScope");
				$controller = $injector.get('$controller');
				$myInjector = $injector;

				$http.whenGET(appctx.modelerprefix+"/rs/type/meta").respond(meta);
				//we do not test html
				$http.whenGET("type/addtype.html").respond("<html></html>");
			}));

			afterEach(function() {
				$http.verifyNoOutstandingRequest();
				$http.verifyNoOutstandingExpectation();
			});
			
			
			
			it("Property type:parse date", function(){
				var dateType=flexdms.basic_types[11];
				expect(dateType.isDateTime()).toBeTruthy();
				
				//date parse and equal
				var date1=dateType.parse("2013-12-15");
				var date2=dateType.parse("2013-12-15");
				
				expect(date1 instanceof Date).toBeTruthy();
				expect(date1.getFullYear()).toEqual(2013);
				expect(date1==date2).toBeFalsy();
				expect(dateType.isEqual(date1, date2)).toBeTruthy();
				expect(dateType.isDateTime()).toBeTruthy();
				expect(dateType.isDateOnly()).toBeTruthy();
				
				//date object can be detected
				expect(dateType.parse(new Date())).not.toBeNull();
				
				//bad date return as null
				expect(dateType.parse("xyntag")).toBeNull();
			});
			
			it("Extra properties:basic functions", function(){
				var obj={"property": []};
				flexdms.addExtraProp(obj, "prop1", "value1");
				expect(flexdms.getExtraProp(obj, "prop1")).toEqual("value1");
				flexdms.addExtraProp(obj, "prop1", "value2");
				expect(flexdms.getExtraProp(obj, "prop1")).toEqual("value2");
				flexdms.deleteExtraProp(obj, "prop1");
				
				var test={"property": []};
				flexdms.addExtraProp(test, "clientFile", "true");
				expect(test.property.length).toEqual(1);
				expect(test.property[0]['@name']).toEqual('clientFile');
				
				flexdms.addExtraProp(test, "prop2", "value2");
				expect(test.property.length).toEqual(2);
				flexdms.deleteExtraProp(test, 'prop3');
				expect(test.property.length).toEqual(2);
				
				flexdms.deleteExtraProp(test, 'prop2');
				expect(test.property.length).toEqual(1);
				expect(test.property[0]['@name']).toEqual('clientFile');
				expect(flexdms.getExtraProp(test, 'clientFile')).toEqual('true');
				
				
				var obj1={
						top: {
							prop1:{
								'name':'value1',
							},
							'array1':[1,2,3],
							'simple': 's',
						}
				};
				var results={};

				flexdms.flatObject(obj1, results);
				for(var propname in results){
					flexdms.addExtraProp(obj, propname, results[propname]);
				}
				var returned=flexdms.getExtraPropObj(obj, 'top');
				expect(angular.isArray(returned.array1));
				
			});
			
			it("Property Object: allows", function(){
				
				//allows
				var prop=new flexdms.Property(flexdms.createEmptyBasicProperty());
				prop.setTypeObject(flexdms.basic_types[11]);
				prop.setAllowedValues([{value:"2013-12-15"},{value:"2013-12-16"}, {value:"2013-12-17"} ]);
				var allows=prop.getAllowedValues();
				expect(allows.length).toBe(3);
				expect(allows[0].display).toEqual("2013-12-15");
				expect(allows[0].value instanceof Date).toBeTruthy();
				
				var ds=[new Date("2013-12-15"), new Date("2013-12-17"), new Date("2013-12-18")];
				var ds1=prop.pickValueFromAlloweds(ds);
				expect(ds1.length).toBe(2);
				expect(ds1[0]).toBe(allows[0].value);
				expect(ds1[1]).toBe(allows[2].value);
				
				var d=prop.pickValueFromAlloweds(ds[0]);
				expect(d).toBe(allows[0].value);
			});
			
			it("Property Object:Guess property", function() {
				var types=flexdms.types;
				expect(types.length>5).toBeTruthy();
				var type=flexdms.findType("Basictype");
				//------------------basic primitive
				//nuke all property
				angular.forEach(type.$json.attributes.basic, function(propjson){
					propjson.property_back=propjson.property;
					propjson.property=[];
				});
				expect(type.getProp("mediumstring").getTypeObject().idx).toBe(0);
				expect(type.getProp("propint").getTypeObject().idx).toBe(3);
				expect(type.getProp("proptime").getTypeObject().idx).toBe(13);
				angular.forEach(type.$json.attributes.basic, function(propjson){
					propjson.property=propjson.property_back;
				});
				
				//---------------embedded
				type=flexdms.findType("Embedmain");
				//nuke all property
				angular.forEach(type.$json.attributes.embedded, function(propjson){
					propjson.property_back=propjson.property;
					propjson.property=[];
				});
				angular.forEach(type.$json.attributes['element-collection'], function(propjson){
					propjson.property_back=propjson.property;
					propjson.property=[];
				});
				expect(type.getProp("singleembed").getTypeObject().idx).toBe(-1);
				expect(type.getProp("multiembed").getTypeObject().idx).toBe(-1);
				
				angular.forEach(type.$json.attributes.embedded, function(propjson){
					propjson.property=propjson.property_back;
				});
				angular.forEach(type.$json.attributes['element-collection'], function(propjson){
					propjson.property=propjson.property_back;
				});
				
				
				//relation
				type=flexdms.findType("Mstudent");
				expect(type.getProp("OneManys").getTypeObject().idx).toBe(-2);
				expect(type.getProp("doomroom").getTypeObject().idx).toBe(-2);
				expect(type.getProp("doombuild").getTypeObject().idx).toBe(-2);
				expect(type.getProp("Courses").getTypeObject().idx).toBe(-2);
				
			});
			it("Property Object:Find related prop", function() {
				var types=flexdms.types;
				expect(types.length>5).toBeTruthy();
				var room=flexdms.findType("Mdoomroom");
				var inverseprop=room.getProp("doombuild").findInverseProp();
				expect(inverseprop).not.toBeNull();
				expect(inverseprop.getName()).toEqual("rooms");
				expect(room.getProp("doombuild").findRelatedProp()).toBe(inverseprop);
				//------------------basic primitive
				var build=flexdms.findType("Mdoombuild");
				var ownerprop=build.getProp("rooms").findOwnerProp();
				expect(ownerprop).not.toBeNull();
				expect(ownerprop.getName()).toEqual("doombuild");
				expect(build.getProp("rooms").findRelatedProp()).toBe(ownerprop);
			
				
			});
			
			it("Property Object:Flush type", function() {
				var types=flexdms.types;
				expect(types.length>5).toBeTruthy();
				var propObj=new flexdms.Property(flexdms.createEmptyBasicProperty());
				propObj.setTypeObject(flexdms.basic_types[0]);
				
				//basic
				propObj.flushType();
				expect(propObj.prop['@attribute-type']).toEqual(flexdms.basic_types[0].value);
				expect(propObj.prop['@target-class']).toBeNull();
				expect(propObj.prop['@target-entity']).toBeNull();
				
				//relation: collection
				propObj.collectionType="one-to-many";
				propObj.setTypeObject(flexdms.createRelationPropType(flexdms.findType("Mdoomroom")));
				propObj.prop['@attribute-type']="java.util.List";
				propObj.flushType();
				expect(propObj.prop['@attribute-type']).toEqual("java.util.List");
				expect(propObj.prop['@target-class']).toBeNull();
				expect(propObj.prop['@target-entity']).toEqual("Mdoomroom");
				
				
				//not collection
				propObj.collectionType="many-to-one";
				propObj.setTypeObject(flexdms.createRelationPropType(flexdms.findType("Mdoomroom")));
				propObj.prop['@attribute-type']="java.util.List";
				propObj.flushType();
				expect(propObj.prop['@attribute-type']).toBeNull();
				expect(propObj.prop['@target-class']).toBeNull();
				expect(propObj.prop['@target-entity']).toEqual("Mdoomroom");
				
				//element collection
				propObj.collectionType="element-collection";
				propObj.setTypeObject(flexdms.createEmbeddedPropType(flexdms.findType("Embed1")));
				propObj.prop['@attribute-type']="java.util.List";
				propObj.flushType();
				expect(propObj.prop['@attribute-type']).toEqual("java.util.List");
				expect(propObj.prop['@target-entity']).toBeNull();
				expect(propObj.prop['@target-class']).toEqual("Embed1");
			});

		}
);