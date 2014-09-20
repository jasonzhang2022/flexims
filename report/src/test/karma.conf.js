module.exports = function(config){
	config.set({
		
		basePath : '../../',
		files : [
		         '../js/app/lib/jquery/dist/jquery.min.js',
		         '../js/app/lib/angular/angular.js',
		         '../js/app/lib/angular-ui-router/release/angular-ui-router.min.js',
		         '../js/app/lib/angular-resource/angular-resource.min.js',
		         '../js/app/lib/angular-cookies/angular-cookies.min.js',
		         '../js/app/lib/angular-animate/angular-animate.min.js',
		         '../js/app/lib/ng-grid/build/ng-grid.js',
		         '../js/app/lib/ng-grid/plugins/ng-grid-csv-export.js',
		         '../js/app/lib/angular-file-upload/angular-file-upload.min.js',
		         '../js/app/lib/angular-ui-bootstrap-bower/ui-bootstrap-tpls.min.js',
		         '../js/app/lib/angular-ui-tree/dist/angular-ui-tree.min.js',
		         '../js/app/lib/angular-mocks/angular-mocks.js',
		         '../js/app/js/common.js',
		         '../js/app/js/type/type_common.js',
		         '../js/app/js/type/serverconfig.js',
		         'src/test/typemodules.js',
		         
		         'src/main/resources/META-INF/resources/thirdparty/picklist.js',
		         'src/main/resources/META-INF/resources/thirdparty/report_picklist.js',
		         
		         '../js/app/js/type/util_service.js',
		         '../js/app/js/type/type_service.js',
		         '../js/app/js/type/inst_service.js',
		         '../js/app/js/type/diagram.js',
		         '../js/app/js/type/type.js',
		         '../js/app/typejsfragments/**/*.js',
		         '../js/app/js/inst/config.js',
		         '../js/app/js/inst/inst_directive.js',
		         '../js/target/inst.tpl.js',
		         '../js/app/js/inst/layout.js',
		         
		         'src/main/js/fragments/1report1_class.js',
		         'src/main/js/fragments/query/defaulttypedquery.js',
		         'src/main/js/fragments/report/report2_service.js',
		         'src/main/js/fragments/report/report3_template.js',
		         'src/main/js/fragments/report/report4_report_grid.js',
		         'src/main/js/fragments/report/report7_queryparam.js',
		         'src/main/js/fragments/report/report8_fxreportinst.js',
		         'src/main/js/fragments/report/report9_relationeditor.js',
		         'src/main/js/fragments/report/report9_relationui.js',
		         
		         'target/report1.fxext.js',
		         
		         '../js/app/js/inst/inst.js',
		         '../js/test/unit/type/op.js',
		         '../js/test/unit/type/data.js',
		         
		         'src/test/javascript/*.js'
		         
		         ],
		         
		         autoWatch : true,
		         
		         frameworks: ['jasmine'],
		         
		         browsers : ['Chrome'],
		         
		         plugins : [
		                    'karma-chrome-launcher',
		                    'karma-firefox-launcher',
		                    'karma-jasmine',
		                    'karma-junit-reporter',
		                    'karma-html-reporter',
		                    'karma-coverage'
		                    ],
		                    
		                    junitReporter : {
		                    	outputFile: 'target/surefire-reports/unit.xml',
		                    	suite: 'unit'
		                    },
		                    
		                    // the default configuration
		                    htmlReporter: {
		                    	outputDir: 'target/karma_html',
		                    	templatePath: 'node_modules/karma-html-reporter/jasmine_template.html'
		                    },
		                    // optionally, configure the reporter
		                    coverageReporter: {
		                    	type : 'html',
		                    	dir : 'target/coverage/'
		                    },
//		                    preprocessors: {
//		                    //code covered by coverage
//		                    '../js/app/js/**/*.js': ['coverage']
//		                    },
		                    reporters: ['progress', 'html', 'junit'],
		                    
		                    
	});
};
