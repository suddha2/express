/**
 * Created by udantha on 6/10/14.
 */
expressFrontApp.controller('booking-success', ['$scope', '$http',
    function ($scope, $http) {
        //set current loading screen
        $rootScope.currentScreen = 'bookingDone';
    }
]);