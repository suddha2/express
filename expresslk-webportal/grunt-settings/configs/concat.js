/**
 * Created by udantha on 3/16/15.
 */
module.exports = {

    //common Javascript libraries
    commonLibsJs: {
        nonull: true,
        src: [
            "bower_components/jquery/dist/jquery.min.js",
            "bower_components/angular/angular.min.js",
            //"bower_components/bootstrap/js/tooltip.js",
            "bower_components/angular-bootstrap/ui-bootstrap.min.js",
            "bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js",
            //"bower_components/angular-route/angular-route.min.js",
            "bower_components/angular-ui-router/release/angular-ui-router.min.js",
            "bower_components/angular-animate/angular-animate.min.js",
            //"bower_components/ng-table/dist/ng-table.min.js",
            "bower_components/angular-sanitize/angular-sanitize.min.js",
            "bower_components/moment/min/moment.min.js",
            "bower_components/moment-timezone/builds/moment-timezone.min.js"
            //"bower_components/angular-ui-select/dist/select.min.js"
        ],
        dest: 'public/min/libs.min.js'
    },

    //Admin's js libraries
    adminJsLibs: {
        nonull: true,
        src: [
            //required for ngadmin
            "bower_components/jquery/dist/jquery.min.js",
            "bower_components/angular/angular.min.js",
            "bower_components/angular-bootstrap/ui-bootstrap.min.js",
            "bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js",
            "bower_components/angular-resource/angular-resource.min.js",
            "bower_components/textAngular/dist/textAngular-sanitize.min.js", // old "bower_components/angular-sanitize/angular-sanitize.min.js",
            "bower_components/angular-ui-codemirror/ui-codemirror.min.js",
            "bower_components/angular-ui-router/release/angular-ui-router.min.js",
            "bower_components/angular-numeraljs/dist/angular-numeraljs.min.js",
            "bower_components/humane/humane.js",
            "bower_components/inflection/inflection.min.js",
            "bower_components/lodash/lodash.min.js",
            "bower_components/ng-file-upload/ng-file-upload-all.min.js",
            "bower_components/ngInflection/dist/ngInflection.js",
            "bower_components/nprogress/nprogress.js",
            "bower_components/restangular/dist/restangular.min.js",
            "bower_components/textAngular/dist/textAngular-rangy.min.js",
            "bower_components/textAngular/dist/textAngular.min.js",
            "bower_components/papaparse/papaparse.min.js",
            "bower_components/numeral/min/numeral.min.js",
            "bower_components/codemirror/lib/codemirror.js",
            "bower_components/codemirror/addon/edit/closebrackets.js",
            "bower_components/codemirror/addon/lint/lint.js",
            "bower_components/jsonlint/lib/jsonlint.js",
            "bower_components/codemirror/addon/lint/json-lint.js",
            "bower_components/codemirror/addon/selection/active-line.js",
            "bower_components/codemirror/mode/javascript/javascript.js",
            //rest of the libs
            "bower_components/angular-route/angular-route.min.js",
            "bower_components/angular-animate/angular-animate.min.js",
            "bower_components/ng-table/dist/ng-table.min.js",
            "bower_components/moment/min/moment.min.js",
            "bower_components/moment-timezone/builds/moment-timezone.min.js",
            "bower_components/angular-ui-select/dist/select.min.js",
            "bower_components/angular-translate/angular-translate.min.js",
            "bower_components/angular-simple-logger/dist/angular-simple-logger.min.js", //ui google map
            "bower_components/angular-google-maps/dist/angular-google-maps.min.js" //ui google map
        ],
        dest: 'public/min/admin-libs.min.js'
    },
    ngadminJs: {
        nonull: true,
        src: [
            "bower_components/requirejs/require.js",
            //"bower_components/metisMenu/dist/metisMenu.min.js",
            //"bower_components/startbootstrap-sb-admin-2/dist/js/sb-admin-2.js",
            "bower_components/ng-admin/build/ng-admin-only.min.js"
        ],
        dest: 'public/min/ngadmin.min.js'
    },

    //Admin's css libraries
    ngadminCss: {
        nonull: true,
        src: [
            "bower_components/ng-admin/build/ng-admin.min.css"
        ],
        dest: 'public/min/ngadmin.min.css'
    }

};