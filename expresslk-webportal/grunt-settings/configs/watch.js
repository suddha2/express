/**
 * Created by udantha on 4/14/14.
 * Watches files ad rebuilds minify
 */
module.exports = {
    buildJs: {
        files: ['grunt-settings/configs/**/*.js', 'assets-src/**/*.js'],
        tasks: ['uglify:build', 'concat:commonLibsJs']
    },
    buildCss: {
        files: ['assets-src/**/*.scss'],
        tasks: ['sass']
    }
};