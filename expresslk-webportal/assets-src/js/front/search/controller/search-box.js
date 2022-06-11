/**
 * Created by udantha on 4/25/14.
 */
/**
 * Controller for search widget.
 */
expressFrontApp.controller('searchBox', ['$scope', '$http', '$location', '$rootScope', 'translate',
    function ($scope, $http, $location, $rootScope, translate) {
    $scope.searchFormData = {
        offercode: '',
        start: '',
        end: '',
        date: null
    };

    //set current loading screen
    $rootScope.currentScreen = 'search';



}]);