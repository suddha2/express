/**
 * Created by udantha on 10/22/14.
 */
expressAdminApp.config(['$stateProvider',
    function($stateProvider) {
        //use stateprovider to inject routes into Main CRUD application
        $stateProvider
            .state('schedules', {
                parent: 'main',
                url: "/view_schedule",
                controller: "viewSchedule",
                templateUrl: "/admin-panel/bus-schedule/view"
            })
            .state('addschedules', {
                parent: 'main',
                url: "/add_schedule",
                controller: "addSchedule",
                templateUrl: "/admin-panel/bus-schedule/add"
            });
    }]);