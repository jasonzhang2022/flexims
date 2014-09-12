
/**
 * @ngdoc function
 * @name flexdms.getPropTemplate
 * @module Global
 *
 * @description
 *  decide the template for property with a single value
 *  The search sequences
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
 *  E.g: 
 *  Student has a property FirstName.
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
 * instViewer or instEditor to create the viewer or editor UI. 
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
 * It Has the same structure as {@link instDirective.fxViewTemplates}.
 * Please refer to {@link instDirective.fxViewTemplates} for more information
 * 
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
	     * check TemplateCache to find template for specified type. It first checks <b>template/props/edit/TYPENAME.toLowerCase()/addinst.html</b>, then default.
	     * If you  want override the default, stuck your template in templateCache under the key <b>template/props/edit/TYPENAME.toLowerCase()/addinst.html</b>. 
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
	     * check TemplateCache to find template for specified type. It first checks <b>template/props/edit/TYPENAME.toLowerCase()/addinst.html</b>, then default.
	     * If you  want override the default, stuck your template in templateCache under the key <b>template/props/edit/TYPENAME.toLowerCase()/addinst.html</b>. 
	     *
	     * @param {string} typename view template to be retrieved for.
	     * @returns {url} <b>template url</b> used by fxIbstEditorForm 
	     */
		getEditFormUrl:function(typename){
			return this.getAddFormUrl(typename);
		},
		getInstUrl:function(typename){
			if (typename){
				var template=$templateCache.get(prefix+typename.toLowerCase()+"/inst.html");
				if (template!=null){
					return prefix+typename.toLowerCase()+"/inst.html";
				}
			}
			return prefix+"inst.html";
		},
		getSimpleLayout:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "simple_layout.html",$templateCache);
			return template;
		
		},
		getSingleEmbedded:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "single_embedded.html",$templateCache);
			return template;
		
		},
		getMultipleEmbedded:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "multiple_embedded.html",$templateCache);
			return template;
		
		},
		getMultipleLayout:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "multiple_layout.html",$templateCache);
			return template;
		},
		getSimpleSelect:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "simple_select.html",$templateCache);
			return template;
		},
		getRequiredError:function(){
			//no need for customization
			return $templateCache.get(prefix+"default/required_error.html");
		},
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
	     *  A template for property whose value is a collection.
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
	     *  A template for property with a single value
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
	     * @name instDirective.fxViewTemplates#useSingleFieldForMultiple
	     * @methodOf instDirective.fxViewTemplates
	     * @description
	     *  whether to use multiple template for this property
	     * 
	     * @param {Object} propobj view template to be retrieved for.
	     * @returns {boolean} whether to use multiple template for this property
	     */
		useSingleFieldForMultiple:function(prop) {
			
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
