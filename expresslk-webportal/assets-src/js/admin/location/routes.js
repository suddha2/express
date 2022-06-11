/**
 * Created by udantha on 7/2/16.
 */
expressAdminApp.config(['$stateProvider', function($stateProvider) {
    $stateProvider
        .state('bus-location', {
            parent: 'main',
            url: "/bus-location",
            templateUrl: "/admin-panel/bus/locationview",
            controller: 'loadBusLocation'
        });
}]);