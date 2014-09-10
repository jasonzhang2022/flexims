module.exports = function(grunt) {
	
	// Project configuration.
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
		ngtemplates:  {
			
			test_instpl:        {
				
				cwd: 'app/template',
				src:      '**/*.html',
				dest:     'target/inst.tpl.js', 
				options: {
					prefix: 'template',
					module: 'instDirective',
					htmlmin: {
						collapseBooleanAttributes:      false,
						collapseWhitespace:             true,
						removeAttributeQuotes:          false,
						removeComments:                 true, // Only if you don't use comment directives!
						removeEmptyAttributes:          false,
						removeRedundantAttributes:      false,
						removeScriptTypeAttributes:     false,
						removeStyleLinkTypeAttributes:  false
					}
				},
				nonull: true,
			},
			test_inst:        {
				
				cwd: 'test/unit/inst/templates',
				src:      '*.html',
				dest:     'test/unit/inst/insttest.tpl.js', 
				options: {
					prefix: 'template',
					module: 'instApp',
					htmlmin: {
						collapseBooleanAttributes:      false,
						collapseWhitespace:             true,
						removeAttributeQuotes:          false,
						removeComments:                 true, // Only if you don't use comment directives!
						removeEmptyAttributes:          false,
						removeRedundantAttributes:      false,
						removeScriptTypeAttributes:     false,
						removeStyleLinkTypeAttributes:  false
					}
				},
				nonull: true,
			},
			test_type:        {
				
				cwd: 'app/type',
				src:      ['addprop.html','addtype.html', 'regexp.html', 'types.html', 'viewtype.html'],
				dest:     'test/unit/type/typetest.tpl.js', 
				options: {
					prefix: 'template',
					module: 'ui.typetest.tpl',
					standalone: true,
					htmlmin: {
						collapseBooleanAttributes:      false,
						collapseWhitespace:             true,
						removeAttributeQuotes:          false,
						removeComments:                 true, // Only if you don't use comment directives!
						removeEmptyAttributes:          false,
						removeRedundantAttributes:      false,
						removeScriptTypeAttributes:     false,
						removeStyleLinkTypeAttributes:  false
					}
				},
				nonull: true,
			}
		},
		concat:   {
			instjs:    {
				//src:  [ 'app/jsfragments/**/*.js', '<%= ngtemplates.dev.dest %>'],
				src:  [ 'app/jsfragments/**/*.js'],
				dest: 'app/js/inst/inst_directive.js',
				nonull: true,
			}, 
			typejs:    {
				//src:  [ 'app/jsfragments/**/*.js', '<%= ngtemplates.dev.dest %>'],
				src:  [ 'app/typejsfragments/**/*.js'],
				dest: 'app/js/type/typectrl.js',
				nonull: true,
			}
		},
		less: {
			dev: {
				options: {
					paths: ["app/lib/bootstrap/less", "app/less/fragments"],
				},
				files: {
					"app/css/inst_bootstrap.css": "app/less/inst.less",
					"app/css/type_bootstrap.css": "app/less/type.less"
				}
			}
		}, 
		jsdoc : {
	        dist : {
	            src: [
	                  'app/js/common.js',
	                  'app/js/type/type_common.js',
	                  'app/js/type/type_service.js',
	                  'app/js/type/inst_service.js',
	                  'app/js/type/util_service.js',
	                  'app/js/inst/inst_directive.js'
	                  
	                  ], 
	            options: {
	                destination: 'app/doc'
	            }
	        }
	    }
		
	});
	
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-angular-templates');
	grunt.loadNpmTasks('grunt-contrib-less');
	grunt.loadNpmTasks('grunt-contrib-copy');
	grunt.loadNpmTasks('grunt-jsdoc');
	grunt.registerTask('default', [ 'less', 'ngtemplates', 'concat', 'jsdoc']);
	
};