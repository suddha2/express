/**
 * Created by Udantha on 3/24/14.
 */
module.exports = {

    fonts: {
        files: [
            {
                expand: true,
                dot: true,
                cwd: 'bower_components/font-awesome/fonts',
                src: ['**'],
                dest: 'public/fonts/'
            },
            {
                expand: true,
                dot: true,
                cwd: 'bower_components/bootstrap-sass/assets/fonts/bootstrap',
                src: ['**'],
                dest: 'public/fonts/'
            }
        ]
    },
    legacy: {
        files: [
            {
                expand: true,
                dot: true,
                cwd: 'bower_components/jqueryui-legacy/themes/base',
                src: ['images/*', 'jquery-ui.min.css'],
                dest: 'public/min/jquery-ui-legacy/'
            }
        ]
    }
};
