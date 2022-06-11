/**
 * Created by udantha on 10/14/14.
 */

expressAdminApp.config(['$stateProvider', function($stateProvider) {
    $stateProvider
        .state('schedule', {
            parent: 'main',
            url: "/schedule",
            templateUrl: "ticketbox_template"
        })
        .state('choose', {
            parent: 'main',
            url: "/schedule/:from/:to/:date/:page/",
            templateUrl: "ticketbox_template"
        })
        .state('cancellation', {
            parent: 'main',
            url: "/ticket-cancellation",
            templateUrl: "/admin-panel/ticketbox/cancellation"
        });
}]);