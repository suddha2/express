/**
 * Created by udantha on 6/13/15.
 */
module.exports = {
    dist: {
        options: {
            require: 'sass-css-importer',
            sourcemap: 'none',
            style: 'compressed'
        },
        files: {
            'public/min/libs.min.css': [
                'assets-src/scss/front/libs.scss'
            ],
            'public/min/core.min.css': [
                'assets-src/scss/front/main.scss'
            ],
            'public/min/superline.min.css': [
                'assets-src/scss/front/superline.scss'
            ],
			'public/min/sltb.min.css': [
                'assets-src/scss/front/sltb.scss'
            ],
            'public/min/surrexi.min.css': [
                'assets-src/scss/front/surrexi.scss'
            ],
			'public/min/ntc.min.css': [
                'assets-src/scss/front/ntc.scss'
            ],

            /**
             * Admin section
             */
            'public/min/admin.min.css': [
                'assets-src/scss/admin/main.scss'
            ],
            'public/min/legacy.min.css': [
                'assets-src/scss/legacy/main.scss'
            ]
        }
    }
};