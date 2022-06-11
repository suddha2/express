/**
 * Created by udantha on 10/25/16.
 */
var Tmpl = {
    /**
     * Bake variables into html string
     * @param templateString
     * @param params
     * @returns {*}
     */
    compile: function (templateString, params) {
        //go thrug each params and use key as replacer
        for(var key in params){
            var sKey = '{{'+ key + '}}',
                mVal = params[key];
            templateString = templateString.replace(new RegExp(sKey, 'g'), mVal);
        }
        return templateString;
    },

    compileDom: function (templateObject, target) {

    }
};