/**
 * Created by udantha on 10/14/14.
 */

expressAdminApp.config(['$stateProvider', function($stateProvider) {
    $stateProvider
    //If URL is legacy ticket box, redirect user
        .state('legacyTB', {
            parent: 'main',
            url: "/legacy/ticketbox",
            controller: ['$window', function ($window) {
                $window.location.href = '/legacy/ticketbox';
            }]
        });
}]);