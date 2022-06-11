/**
 * Created by udantha on 10/14/14.
 */

expressAdminApp.config(['$stateProvider', function($stateProvider) {
    $stateProvider
        .state('me-index', {
            parent: 'main',
            url: "/me",
            templateUrl: "/admin-panel/me/"
        })
        .state('me-collection', {
            parent: 'main',
            url: "/me/collection",
            templateUrl: "/admin-panel/me/collection"
        });
}]);