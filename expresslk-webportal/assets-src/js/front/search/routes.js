/**
 * Created by udantha on 10/14/14.
 */
expressFrontApp.config(['$locationProvider', '$stateProvider', '$urlRouterProvider',
    function($locationProvider, $stateProvider, $urlRouterProvider) {
        //set routes
        $stateProvider
            .state('home', {
                url : '/',
                templateUrl: '/app/index/loadpartial?p=booking',
                controller: 'searchBox'
            })
            .state('search-result', {
                url : '/bus/search/:fromName/:toName/:from/:to/:date/:booking?offerCode',
                params: {
                    date: { value: null, squash: true },
                    booking: { value: null, squash: true }
                },
                templateUrl: '/app/index/loadpartial?p=booking',
                controller: 'searchList'
            })
            .state('booking-iframe', {
                url : '/bus/placebooking',
                templateUrl: '/app/index/loadpartial?p=pay-iframe',
                controller: 'placeBookingRoute'
            })
            .state("otherwise", {
                    url : '/'
                });

        //if all routes fails
        $urlRouterProvider.otherwise('/');

        // use the HTML5 History API
        $locationProvider.html5Mode(true);
    }]);