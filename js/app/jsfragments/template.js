
/**
 * @ngdoc function
 * @name flexdms.getPropTemplate
 * @module Global
 *
 * @description
 *  decide the template for property
 *  The search sequences are
 *  <ol>
 *  	<li>PREFIX/TYPENAME/PROPNAME/htmlfile: property-specific template</li>
 *  	<li>PREFIX/TYPENAME/htmlfile: type-spefic template for property of this same type </li>
 *  	<li>default</li>
 *  </ol>
 *  
 *  TYPENAME: lower case of the type name of passed property object 
 *  
 *  PROPNAME: lower case of the property name of passed property object
 *  
 *  This function is used internally by  {@link instDirective.fxTemplates} and {@link instDirective.fxViewTemplates}. 
 *  
 *  Example
 *  Student has a property FirstName which is a short string.
 *  
 *  You can customize the edit and view property by adding a file to $templateCache with the key
 *  template/props/edit/student/firstname/shortstring.html and template/props/view/student/firstname/shortstring.html.
 *  
 *  If you want to customize the display of all shortstring for Student, add a file to $templateCache with the key 
 *  template/props/view/student/shortstring.html
 *  
 *
 * @param {Object} propobj A property object
 * @param {String} prefix prefix start to search from.  For {@link instDirective.fxTemplates} the prefix is "template/props/edit".
 *  For {@link instDirective.fxViewTemplates} the prefix is "template/props/view"
 * @param {String} htmlfile a file to search. Could be shortstring.html for value display, simple_layout.html for layout, etc. This file is used by 
 * instViewer or instEditor to create the part of viewer or editor UI. 
 * @param {Object} $templateCache templateCache from caller.
 * @returns  {String} found template from templateCache
 */
flexdms.getPropTemplate=function(propobj, prefix, htmlfile, $templateCache){
	var typename=propobj.getBelongingType().getName().toLowerCase();
	var propname=propobj.getName().toLowerCase();
	var proptype=propobj.getTypeObject();
	
	//property-specific stuff: typename/propname/file
	var template=$templateCache.get(prefix+typename+"/"+propname+"/"+htmlfile);
	if (template!=null){
		return template;
	}
	
	//check this
	//special hanlding for custom.
	if (proptype.isCustomObject()){
		template=$templateCache.get(prefix+propobj.getShortRootClass().toLowerCase()+'.html');
	}
	if (template!=null){
		return template;
	}
	
	//generic file under typename.
	template=$templateCache.get(prefix+typename+"/"+htmlfile);
	if (template!=null){
		return template;
	}
	
	
	//default name.
	template=$templateCache.get(prefix+"default/"+htmlfile);
	return template;
};

/**
 * @ngdoc service
 * @name instDirective.fxTemplates
 * @kind service
 * @module instDirective
 * @description
 * 
 * Calculate the template used for instEditor and its nested properties. 
 * 
 */
angular.module("instDirective").factory("fxTemplates", ["$templateCache", function($templateCache){
	var prefix="template/props/edit/";
	return {
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#getAddFormUrl
	     * 
	     * @methodOf instDirective.fxTemplates
	     *
	     * @description
	     * check $templateCache to find template for specified type. It first checks <b>template/props/edit/TYPENAME.toLowerCase()/addinst.html</b>, then default.
	     * If you  want override the default, stuck your template in $templateCache under the key <b>template/props/edit/TYPENAME.toLowerCase()/addinst.html</b>. 
	     *
	     * @param {string} typename view template to be retrieved for.
	     * @returns {url} <b>template url</b> used by fxIbstEditorForm 
	     */
		getAddFormUrl:function(typename){
			if (typename){
				var template=$templateCache.get(prefix+typename.toLowerCase()+"/addinst.html");
				if (template!=null){
					return prefix+typename.toLowerCase()+"/addinst.html";
				}
			}
			return prefix+"addinst.html";
		},
		
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#getEditFormUrl
	     * 
	     * @methodOf instDirective.fxTemplates
	     *
	     * @description
	     * check $templateCache to find template for specified type. Current implementation is the same as getAddFormUrl.
	     * 
	     *
	     * @param {string} typename view template to be retrieved for.
	     * @returns {url} <b>template url</b> used by fxIbstEditorForm 
	     */
		getEditFormUrl:function(typename){
			return this.getAddFormUrl(typename);
		},
		
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#getInstUrl
	     * @methodOf instDirective.fxTemplates
	     *
	     * @description
	     * check $templateCache to find template for specified type. It first checks <b>template/props/edit/TYPENAME.toLowerCase()/inst.html</b>, then default.
	     * If you  want override the default, stuck your template in $templateCache under the key <b>template/props/view/TYPENAME.toLowerCase()/inst.html</b>. 
	     *
	     * @param {string} typename view template to be retrieved for.
	     * @returns {url} <b>template url</b> used by fxInstEditor
	     */
		getInstUrl:function(typename){
			if (typename){
				var template=$templateCache.get(prefix+typename.toLowerCase()+"/inst.html");
				if (template!=null){
					return prefix+typename.toLowerCase()+"/inst.html";
				}
			}
			return prefix+"inst.html";
		},
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#getSimpleLayout
	     * @methodOf instDirective.fxTemplates
	     * @description
	     *  layout for property with a single form input field.
	     * 
	     * @param {Object} propobj layout template to be retrieved for.
	     * @returns {string} template used by fxPropEditor to wrap a collection of value.
	     */
		getSimpleLayout:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "simple_layout.html",$templateCache);
			return template;
		
		},
		
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#getSingleEmbedded
	     * @methodOf instDirective.fxTemplates
	     * @description
	     *  return template for property with a single embedded instance
	     * 
	     * @param {Object} propobj layout template to be retrieved for.
	     * @returns {string} template used by fxPropEditor to wrap a collection of value.
	     */
		getSingleEmbedded:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "single_embedded.html",$templateCache);
			return template;
		
		},
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#getMultipleEmbedded
	     * @methodOf instDirective.fxTemplates
	     * @description
	     *  return template for property with a multiple embedded instances
	     * 
	     * @param {Object} propobj layout template to be retrieved for.
	     * @returns {string} template used by fxPropEditor to wrap a collection of value.
	     */
		getMultipleEmbedded:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "multiple_embedded.html",$templateCache);
			return template;
		
		},
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#getMultipleLayout
	     * @methodOf instDirective.fxTemplates
	     * @description
	     *  return a layout file for a property with multiple input fields
	     * 
	     * @param {Object} propobj layout template to be retrieved for.
	     * @returns {string} template used by fxPropEditor to wrap a collection of value.
	     */
		getMultipleLayout:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "multiple_layout.html",$templateCache);
			return template;
		},
		
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#getSimpleSelect
	     * @methodOf instDirective.fxTemplates
	     * @description
	     *  a html select template for property
	     * 
	     * @param {Object} propobj layout template to be retrieved for.
	     * @returns {string} template used by fxPropEditor.
	     */
		getSimpleSelect:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "simple_select.html",$templateCache);
			return template;
		},
		
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#getRequiredError
	     * @methodOf instDirective.fxTemplates
	     * @description
	     *  error template a for required property
	     * 
	     * @param {Object} propobj layout template to be retrieved for.
	     * @returns {string} template used by fxPropEditor.
	     */
		getRequiredError:function(){
			//no need for customization
			return $templateCache.get(prefix+"default/required_error.html");
		},
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#getSimpleInputTemplate
	     * @methodOf instDirective.fxTemplates
	     * @description
	     *  template a property assuming that there is a single value.
	     * 
	     * @param {Object} propobj layout template to be retrieved for.
	     * @returns {string} template used by fxPropEditor.
	     */
		getSimpleInputTemplate:function(propobj) {
			if (propobj.isRelation()){
				var template = flexdms.getPropTemplate(propobj, prefix, "simplerelation.html", $templateCache);
				return template;
			}
			if (propobj.isFileUpload()){
				var template = flexdms.getPropTemplate(propobj, prefix, "file.html", $templateCache);
				return template;
			}
			var template = flexdms.getPropTemplate(propobj, prefix, flexdms.basic_templates[propobj.getTypeObject().idx], $templateCache);
			return template;
	       
		},
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#getErrorTemplate
	     * @methodOf instDirective.fxTemplates
	     * @description
	     *  error template for a property
	     * 
	     * @param {Object} propobj layout template to be retrieved for.
	     * @returns {string} template used by fxPropEditor.
	     */
		getErrorTemplate:function(propobj) {
			if (propobj.isRelation()){
				var template = flexdms.getPropTemplate(propobj, prefix, "simplerelation_error.html", $templateCache);
				return template;
			}
			if (propobj.isFileUpload()){
				var template = flexdms.getPropTemplate(propobj, prefix, "file_error.html", $templateCache);
				return template;
			}
			var template = flexdms.getPropTemplate(propobj, prefix, flexdms.basic_error_templates[propobj.getTypeObject().idx], $templateCache);
			return template;
		},
		
		/**
	     * @ngdoc function
	     * @name instDirective.fxTemplates#useSingleFieldForMultiple
	     * @methodOf instDirective.fxTemplates
	     * @description
	     *  when a property has a collection of value, should we use a template for the simple input to handle the collection of values or should 
	     *  we wrap the simple template within a multiple layout? The multiple_layout will repeat the simple input many times, one for each value.
	     * 
	     * @param {Object} propobj layout template to be retrieved for.
	     * @returns {boolean} should we use simple input to handle colleciton of value.
	     */
		useSingleFieldForMultiple:function(prop) {
			
			if (prop.isFileUpload()){
				return true;
			}
			var type=prop.getTypeObject();
			if (type.isDateTime()){
				return false;
			}
			if (!type.isStringType()){
				return true;
			}
			if (type.isEmail() || type.isURL()){
				return true;
			}
			if (type.isRelation()){
				return true;
			}
			return false;
		}
	
	};
}]);

/**
 * @ngdoc service
 * @name instDirective.fxViewTemplates
 * @kind service
 * @module instDirective
 * @description
 * 
 * Calculate the template used for instViewer and its nested properties
 * 
 */
angular.module("instDirective").factory("fxViewTemplates", ["$templateCache", function($templateCache){
	var prefix="template/props/view/";
	
	return {
		/**
	     * @ngdoc function
	     * @name instDirective.fxViewTemplates#getViewTopUrl
	     * 
	     * @methodOf instDirective.fxViewTemplates
	     *
	     * @description
	     * check TemplateCache to find template for specified type. It first checks <b>template/props/view/TYPENAME.toLowerCase()/viewinst.html</b>, then default.
	     * If you  want override the default, stuck your template in templateCache under the key <b>template/props/view/TYPENAME.toLowerCase()/viewinst.html</b>. 
	     *
	     * @param {string} typename view template to be retrieved for.
	     * @returns {url} <b>template url</b> used by fxInstViewerForm 
	     */
		getViewTopUrl:function(typename){
			if (typename){
				var template=$templateCache.get(prefix+typename.toLowerCase()+"/viewinst.html");
				if (template!=null){
					return prefix+typename.toLowerCase()+"/viewinst.html";
				}
			}
			return prefix+"viewinst.html";
		},
		/**
	     * @ngdoc function
	     * @name instDirective.fxViewTemplates#getInstUrl
	     * @methodOf instDirective.fxViewTemplates
	     *
	     * @description
	     * check TemplateCache to find template for specified type. It first checks <b>template/props/view/TYPENAME.toLowerCase()/inst.html</b>, then default.
	     * If you  want override the default, stuck your template in templateCache under the key <b>template/props/view/TYPENAME.toLowerCase()/inst.html</b>. 
	     *
	     * @param {string} typename view template to be retrieved for.
	     * @returns {url} <b>template url</b> used by fxInstViewer
	     */
		getInstUrl:function(typename){
			if (typename){
				var template=$templateCache.get(prefix+typename.toLowerCase()+"/inst.html");
				if (template!=null){
					return prefix+typename.toLowerCase()+"/inst.html";
				}
			}
			return prefix+"inst.html";
		},
		
		/**
	     * @ngdoc function
	     * @name instDirective.fxViewTemplates#getMultipleLayout
	     * @methodOf instDirective.fxViewTemplates
	     * @description
	     *  A layout template for property whose value is a collection. The multiple_layout will repeat the simple view many times, one for each value.
	     * 
	     * @param {Object} propobj view template to be retrieved for.
	     * @returns {string} template used by fxPropViewer
	     */
		getMultipleLayout:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "multiple_layout.html", $templateCache);
			return template;
		},
		/**
	     * @ngdoc function
	     * @name instDirective.fxViewTemplates#getSimpleViewTemplate
	     * @methodOf instDirective.fxViewTemplates
	     * @description
	     *  A <b>value template</b> for a single value
	     * 
	     * @param {Object} propobj view template to be retrieved for.
	     * @returns {string} <b>template</b> used by fxPropViewer
	     */
		getSimpleViewTemplate:function(propobj) {
			if (propobj.isRelation()){
				var template = flexdms.getPropTemplate(propobj, prefix, "simplerelation.html", $templateCache);
				return template;
			}
			if (propobj.isFileUpload()){
				var template = flexdms.getPropTemplate(propobj, prefix, "file.html", $templateCache);
				return template;
			}
			var template = flexdms.getPropTemplate(propobj, prefix, flexdms.basic_templates[propobj.getTypeObject().idx], $templateCache);
			return template;
		},
		/**
	     * @ngdoc function
	     * @name instDirective.fxViewTemplates#getSingleEmbedded
	     * @methodOf instDirective.fxViewTemplates
	     * @description
	     *  A template for property with single embedded value
	     * 
	     * @param {Object} propobj view template to be retrieved for.
	     * @returns {string} <b>template</b> used by fxPropViewer
	     */
		getSingleEmbedded:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "single_embedded.html",$templateCache);
			return template;
		
		},
		/**
	     * @ngdoc function
	     * @name instDirective.fxViewTemplates#getMultipleEmbedded
	     * @methodOf instDirective.fxViewTemplates
	     * @description
	     *  A template for property with multiple embedded values
	     * 
	     * @param {Object} propobj view template to be retrieved for.
	     * @returns {string} <b>template</b> used by fxPropViewer
	     */
		getMultipleEmbedded:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "multiple_embedded.html",$templateCache);
			return template;
		
		},
		/**
	     * @ngdoc function
	     * @name instDirective.fxViewTemplates#shouldWrapSimpleValueWithMultipleTemplate
	     * @methodOf instDirective.fxViewTemplates
	     * @description
	     *  when a property has a collection of value, should we use a single value template to handle the collection of values or should 
	     *  we wrap the simple value template within a multiple layout? The multiple_layout will repeat the simple value template many times, one for each value.
	     * 
	     * @param {Object} propobj view template to be retrieved for.
	     * @returns {boolean} whether to use multiple template for this property
	     */
		shouldWrapSimpleValueWithMultipleTemplate:function(prop) {
			
			var type=prop.getTypeObject();
			if (type.isFileType()){
				return false;
			}
			return true;
		},
		
		/**
	     * @ngdoc function
	     * @name instDirective.fxViewTemplates#shouldShowMultipleValueAsSimple
	     * @methodOf instDirective.fxViewTemplates
	     * @description
	     *  If the collection of value is wrapped within multiple_layout, should we display them as a single value or a bulletin list?
	     * 
	     * @param {Object} propobj view template to be retrieved for.
	     * @returns {boolean} should we display them as a single value or a bulletin list
	     */
		shouldShowMultipleValueAsSimple:function(prop){

			var type=prop.getTypeObject();
			if (type.isFileType()){
				return true;
			}
			if (type.isDateTime()){
				return true;
			}
			if (!type.isStringType()){
				return true;
			}
			if (type.isEmail() || type.isURL()){
				return true;
			}
			if (type.isRelation()){
				return true;
			}
			return false;
		}
	
	};
}]);
