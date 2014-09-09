

//--------------view template for property in report
flexdms.query.fxReportTemplates=function($templateCache, fxViewTemplates){
	var prefix="template/props/report/";
	return {
		getInstUrl:function(typename){
			if (typename){
				var template=$templateCache.get(prefix+typename.toLowerCase()+"/inst.html");
				if (template!=null){
					return prefix+typename.toLowerCase()+"/inst.html";
				}
			}
			template=prefix+"inst.html";
			if ($templateCache.get(template)!=null){
				return template;
			}
			return fxViewTemplatesgetInstUrl(typename);
		},
		getMultipleLayout:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "multiple_layout.html", $templateCache);
			if ($templateCache.get(template)!=null){
				return template;
			}
			return fxViewTemplates.getMultipleLayout(propobj);
		},
		getSimpleViewTemplate:function(propobj) {
			if (propobj.isRelation()){
				var template = flexdms.getPropTemplate(propobj, prefix, "simplerelation.html", $templateCache);
				if ($templateCache.get(template)!=null){
					return template;
				}
				return fxViewTemplates.getSimpleViewTemplate(propobj);
			}
			var template = flexdms.getPropTemplate(propobj, prefix, flexdms.basic_templates[propobj.getTypeObject().idx], $templateCache);
			if ($templateCache.get(template)!=null){
				return template;
			}
			return fxViewTemplates.getSimpleViewTemplate(propobj);
		},
		getSingleEmbedded:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "single_embedded.html",$templateCache);
			if ($templateCache.get(template)!=null){
				return template;
			}
			return fxViewTemplates.singleEmbedded(propobj);
		
		},
		getMultipleEmbedded:function(propobj){
			var template = flexdms.getPropTemplate(propobj, prefix, "multiple_embedded.html",$templateCache);
			if ($templateCache.get(template)!=null){
				return template;
			}
			return fxViewTemplates.getMultipleEmbedded(propobj);		
		},
		useSingleFieldForMultiple:function(prop) {
			return fxViewTemplates.useSingleFieldForMultiple(prop);
		}
	};
};
angular.module("flexdms.report").factory("fxReportTemplates", flexdms.query.fxReportTemplates);