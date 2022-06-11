/*global module:false*/
module.exports = function (grunt) {

    //load configurations based on passed directory
    function loadConfig(path) {
        var glob = require('glob');
        var object = {};
        var key;

        glob.sync('*', {cwd: path}).forEach(function (option) {
            key = option.replace(/\.js$/, '');
            object[key] = require(path + option);
        });

        return object;
    }

    // Project configuration.
    var config = {
        // Metadata.
        pkg: grunt.file.readJSON('package.json'),
        banner: '/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - ' +
            '<%= grunt.template.today("yyyy-mm-dd") %>\n' +
            '<%= pkg.homepage ? "* " + pkg.homepage + "\\n" : "" %>' +
            '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author.name %>;' +
            ' Licensed <%= _.pluck(pkg.licenses, "type").join(", ") %> */\n',
        env: process.env
    };

    //Extend configurations from each sub config
    grunt.util._.extend(config, loadConfig('./grunt-settings/configs/'));

    grunt.initConfig(config);

    //Load grunt task definitions from package.jason
    require('load-grunt-tasks')(grunt);

    //compile all styles
    grunt.registerTask('styles', [
        'sass',
        'concat:ngadminCss'
    ]);

    //build js
    grunt.registerTask('js', [
        'uglify:build',
        'concat:commonLibsJs',
        'concat:adminJsLibs',
        'concat:ngadminJs'
    ]);

    // Default task.
    grunt.registerTask('default', [
        'clean:cleanPublic',
        'js',
        'copy:fonts',
        'copy:legacy',
        'styles'
    ]);

    //production build task
    grunt.registerTask('production', [
        'js',
        'copy:fonts',
        'copy:legacy',
        'styles'
    ]);

};
