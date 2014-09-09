module.exports = function(grunt) {
	
	// Project configuration.
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
		ngtemplates:  {
			reporthtml:        {
				
				cwd: 'src/main/webapp/fragments/inst',
				src:      '**/*.html',
				dest:     'target/report1.fxext.js', 
				options: {
					prefix: 'template',
					module: 'flexdms.report',
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
			dev:    {
				src:  [ 'src/main/js/fragments/1report1_class.js', 
				        'src/main/js/fragments/query/*.js', 
				        'src/main/js/fragments/report/*.js',
				        '<%= ngtemplates.reporthtml.dest %>'],
				dest: 'src/main/resources/META-INF/resources/inst/report0.js',
				nonull: true,
			}
		}
	});
	
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-angular-templates');
	
	grunt.registerTask('default', [  'ngtemplates', 'concat']);
	
};