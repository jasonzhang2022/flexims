module.exports = function(config){
  config.set({

    basePath : '../',
    files : [
      'app/lib/jquery/dist/jquery.min.js',
      'app/lib/angular/angular.js',
      '../js/app/lib/angular-dateparser/dateparser.js',
      'app/lib/angular-ui-router/release/angular-ui-router.min.js',
      'app/lib/angular-resource/angular-resource.min.js',
      'app/lib/angular-cookies/angular-cookies.min.js',
      'app/lib/angular-animate/angular-animate.min.js',
      'app/lib/ng-grid/build/ng-grid.js',
      'app/lib/angular-file-upload/angular-file-upload.min.js',
      'app/lib/angular-ui-bootstrap-bower/ui-bootstrap-tpls.min.js',
      'app/lib/angular-ui-tree/dist/angular-ui-tree.min.js',
      'app/lib/angular-mocks/angular-mocks.js',
      '../js/app/lib/js-xlsx/dist/xlsx.core.min.js',
	 	'../js/app/lib/FileSaver/FileSaver.min.js',
      
      'app/js/common.js',
      'app/js/type/type_common.js',
      'app/js/type/serverconfig.js',
      'app/js/type/util_service.js',
      'app/js/type/type_service.js',
      'app/js/type/inst_service.js',
      'app/js/type/diagram.js',
      
      'app/js/type/type.js',
      'app/typejsfragments/**/*.js',
      'app/js/inst/config.js',
      'app/js/inst/inst_directive.js',
      'target/inst.tpl.js',
      '../js/app/js/inst/layout.js',
		 '../js/app/js/inst/excel.js',
		 '../js/app/js/inst/instdemo.js',
      'app/js/inst/inst.js',
      
      'test/unit/type/op.js',
      'test/unit/type/data.js',
      'test/unit/type/typetest.tpl.js',
      'test/unit/type/testutil.js',
      'test/unit/type/testcommon.js',
      'test/unit/type/test_type_common.js',
      'test/unit/type/test_type_service.js',
      'test/unit/type/testtype.js',
      
      'test/unit/inst/data_inst.js',
      'test/unit/inst/insttest.tpl.js',
      'test/unit/inst/test_directive.js',
    ],

    autoWatch : true,

    frameworks: ['jasmine'],

   // browsers : ['Chrome'],
    
    browsers : ['Firefox', 'FirefoxDeveloper', 'FirefoxAurora', 'FirefoxNightly'],
    
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
//    preprocessors: {
//    	//code covered by coverage
//    	'app/js/**/*.js': ['coverage']
//      },
    reporters: ['progress', 'html', 'junit'],
    

  });
};
