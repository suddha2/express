/**
 * Created by Udantha on 3/24/14.
 */
module.exports = {
    options: {
        separator: ';',
        //Mangle source code - changing function and variable names on minify
        mangle: true,
        //sourceMap: true,
        except: ['jQuery', 'Backbone', 'Od', '$']
    },
    build: {
        files: {
            //core files
            'public/min/core.min.js': [
                'assets-src/js/app.js',
                'assets-src/js/core/acl.js',
                'assets-src/js/core/directive/**/*.js',
                'assets-src/js/core/util/**/*.js',
                'assets-src/js/core/filter/**/*.js',
                'assets-src/js/core/factory/**/*.js'
            ],
            //for ie
            'public/min/ltie9.min.js': ['assets-src/js/core/html5shiv.js', 'assets-src/js/core/respond.min.js'],
            /**
             * Front section
             */
            //front section common includes
            'public/min/front.min.js': [
                'assets-src/js/front/app.js',
                'assets-src/js/front/common/controller/controller-common.js'
            ],
            //search and home
            'public/min/search.min.js': ['assets-src/js/front/search/**/*.js'],

            /**
             * Admin section
             */
            'public/min/admin.min.js': [
                'assets-src/js/admin/*.js',
                'assets-src/js/admin/common/**/*.js',
                'assets-src/js/admin/dashboard/**/*.js',
                'assets-src/js/admin/me/**/*.js',
                'assets-src/js/core/module/confirm.js' //confirm module
            ],
            'public/min/ticketbox.min.js': [
                'assets-src/js/admin/ticketbox/**/*.js'
            ],
            'public/min/config.min.js': [
                'assets-src/js/admin/config/**/*.js'
            ],
            'public/min/crud.min.js': [
                'assets-src/js/admin/crud/**/*.js'
            ],
            'public/min/report.min.js': [
                 'assets-src/js/admin/report/**/*.js'
             ],
            'public/min/bus-location.min.js': [
                'assets-src/js/admin/location/**/*.js'
            ],
            //legacy versions in admin
            'public/min/legacy-full.min.js': [
                "bower_components/jquery-legacy/dist/jquery.js",
                "bower_components/jqueryui-legacy/jquery-ui.js",
                'assets-src/js/legacy/core/**/*.js',
                'assets-src/js/legacy/common/**/*.js',
                'assets-src/js/legacy/ticketbox/**/*.js'
            ]
        }
    }
};
