// modules used by instApp, can be expanded by third party modules
flexdms.instAppModules= ['ngCookies', 'ngGrid', 'instDirective', 'angularFileUpload', "ui.tree"];
angular.forEach(flexdms.typemodules, function(m){
	flexdms.instAppModules.push(m);
});


flexdms.basic_templates=['shortstring.html', 'mediumstring.html', 'largetext.html', 'int.html', 'long.html', 'float.html', 
                     'double.html', 'currency.html', 'noop.html', 'boolean.html', 'noop.html', 'date.html', 'timestamp.html',
                 'time.html', 'email.html', 'url.html', 'directory.html', 'serverfile.html'
                 ];

flexdms.basic_error_templates=['shortstring_error.html', 'mediumstring_error.html', 'largetext_error.html', 'int_error.html', 'long_error.html', 'float_error.html', 
                     'double_error.html', 'currency_error.html', 'noop_error.html', 'boolean_error.html', 'noop_error.html', 'date_error.html', 'timestamp_error.html',
                 'time_error.html', 'email_error.html', 'url_error.html', 'directory_error.html', 'serverfile_error.html'
                 ];

flexdms.instViewLink="#viewinst"; //#viewinst/type/id;
flexdms.instEditLink="#editinst"; //#viewinst/type/id;
flexdms.instDeleteLink="#deleteinst"; //#viewinst/type/id;
flexdms.instAddLink="#addinst"; //#viewinst/type/id;

flexdms.parentinst="$_fxparentinst";
/**
 * flexdms.typeEditorConfig.Type.ctrl=type specified controller;
 * flexdms.typeEditorConfig.Type.cssClass=type specified CSS class
 */
flexdms.config.defaultFileSchema="DBFILE";
flexdms.config.fileSchema=flexdms.config.defaultFileSchema;
flexdms.config.indexurl=appctx.formprefix+"/index.html";
flexdms.config.logout=appctx.modelerprefix+"/rs/res/web/customtemplates/logout.html";
flexdms.config.logourl="http://www.google.com";
flexdms.config.brandtxt="Flexdms";
flexdms.config.viewer={
		'typename': {
			'ctrl': 'controllerName',
			'cssClass': 'extra cssClass',
			'props': {
				'prop1': {
					'ctrl':'controllerName',
				}
			}
		},
		'default' : {
			actions:[ "<a type='button' class='btn btn-primary' title='edit' href='#/editinst/{{typename}}/{{instid}}' ng-show='actions.Edit'> " +
					"<span class='glyphicon glyphicon-edit'></span></a>",
					
			          "<button type='button' class='btn btn-default' title='refresh' data-ng-click='refresh();' ng-show='actions.Read'>" +
			          "<span class='glyphicon glyphicon-refresh'></span></button>",
		
			          "<a type='button' class='btn btn-default' title='delete' href='#/deleteinst/{{typename}}/{{instid}}' ng-show='actions.Grant'> " +
			          "<span class='glyphicon glyphicon-trash'></span></a>",
			          
			          "<a type='button' class='btn btn-default' title='download files uploaded' ng-show='type.hasClientFile() && actions.Read' " +
			          "href='"+appctx.modelerprefix+"/rs/file/clientfiles/{{typename}}/{{instid}}'> <span class='glyphicon glyphicon-arrow-down'></span></a>",
			          
			          "<a type='button' class='btn btn-default' title='download files' ng-show='type.hasServerFile() && actions.Read' " +
			          "href='"+appctx.modelerprefix+"/rs/file/serverfiles/{{typename}}/{{instid}}'> <span class='glyphicon glyphicon-arrow-down'></span></a>"
			          ],
		}
		
};
flexdms.config.editor= {
		'typename': {
			'ctrl': 'controllerName',
			'cssClass': 'extra cssClass',
			'props': {
				'prop1': {
					'ctrl':'controllerName',
				}
			}
		},
		'default' : {
			actions:[ "<button class='btn btn-primary ' data-ng-click='saveInst();' data-ng-disabled='instform.$invalid '>	<span class='glyphicon glyphicon-floppy-disk'></span></button>",
			          "<a type='button' data-ng-if='edit'  class='btn btn-default' title='Cancel' href='#/viewinst/{{typename}}/{{inst.id}}'>Cancel</a>",
			          
			          ],
		}
};

//global datepicker configuration
datepickerPopupConfig={
		"datepicker-popup": flexdms.dateFormat	
};
angular.module("instDirective", ["flexdms.TypeResource", "flexdms.InstResource", 'ui.router']).run(['$rootScope', function($rootScope){
	//make config and flexdms available to ang scope.
	$rootScope.config=flexdms.config;
	$rootScope.flexdms=flexdms;
}]);
