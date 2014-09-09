

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
 * logic to find input for each property.
 * TODO provide property specific override mechanism.
 */
angular.module("instDirective").factory("fxTemplates", ["$templateCache", function($templateCache){
	var prefix="template/props/edit/";
	return {
		getAddFormUrl:function(typename){
			if (typename){
				var template=$templateCache.get(prefix+typename.toLowerCase()+"/addinst.html");
				if (template!=null){
					return prefix+typename.toLowerCase()+"/addinst.html";
				}
			}
			return prefix+"addinst.html";
		},
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
 * logic to find view for each property.
 * TODO provide property specific override mechanism.
 */
angular.module("instDirective").factory("fxViewTemplates", ["$templateCache", function($templateCache){
	var prefix="template/props/view/";
	
	return {
		getViewTopUrl:function(typename){
			if (typename){
				var template=$templateCache.get(prefix+typename.toLowerCase()+"/viewinst.html");
				if (template!=null){
					return prefix+typename.toLowerCase()+"/viewinst.html";
				}
			}
			return prefix+"viewinst.html";
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
		getMultipleLayout:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "multiple_layout.html", $templateCache);
			return template;
		},
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
		getSingleEmbedded:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "single_embedded.html",$templateCache);
			return template;
		
		},
		getMultipleEmbedded:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "multiple_embedded.html",$templateCache);
			return template;
		
		},
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
