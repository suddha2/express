/**
 * Created by udantha on 12/17/16.
 */
expressAdminApp
    .directive("expDatepicker", ['$window', function ($window) {
        return {
            restrict: 'E',
            scope: false,
            template: '',
            controller: ['$scope', '$element', 'bookingService', '$rootScope',
                function($scope, $element, bookingService, $rootScope){

                    $scope.resendEmail = function(){

                    }

                }],
            link: function ($scope, $element, $attrs) {
                $scope.reference = $scope.$eval($attrs.bookingReference);
            }
        };
    }]);