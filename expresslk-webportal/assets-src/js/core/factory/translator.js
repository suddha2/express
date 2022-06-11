/**
 * Created by udantha on 11/29/15.
 */
angular.module('expressCore')
    .factory('translate', ['$injector', function($injector) {
        var translations = {};
        //get translations value
        try{
            translations = $injector.get('TRANSLATIONS');
        }catch (e){

        }
        /**
         * Get translated word. If translation doesnt exists returns the key
         */
        return {
            get : function(key) {
                return (translations[key]? translations[key] : key);
            }
        }
    }]);