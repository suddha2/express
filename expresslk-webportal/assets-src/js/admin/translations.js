/**
 * Created by udantha on 6/16/16.
 */
angular.module('expressAdmin')
    .config(['$translateProvider', 'TRANSLATIONS', function ($translateProvider, TRANSLATIONS) {
        
        $translateProvider.translations('cl' /* Current Language */, TRANSLATIONS);
        $translateProvider.preferredLanguage('cl');
    }]);