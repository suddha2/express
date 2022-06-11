/**
 * Created by udantha on 2/8/16.
 */
expressAdminApp
    .directive("alertSms", ['$window', function ($window) {
        return {
            restrict: 'E',
            scope: false,
            template: '<button type="button" class="btn btn-warning" ng-click="resendSms()">Re-send SMS</button>',
            controller: ['$scope', '$element', 'bookingService', '$rootScope',
                function($scope, $element, bookingService, $rootScope){

                    $scope.resendSms = function(){
                        if(confirm('Are you sure to re-send the SMS?')){
                            $rootScope.Global.loading = true;
                            //send request
                            bookingService.resedSms($scope.reference).then(function (data) {
                                if (!data.error) {
                                    //set success message
                                    $rootScope.Global.loading = false;
                                    $rootScope.Global.success.isSuccess = true;
                                    $rootScope.Global.success.message = "Booking SMS sent to '"+ data.phone +"' successfully.";
                                } else {
                                    $rootScope.Global.loading = false;
                                    $rootScope.Global.error.hasError = true;
                                    $rootScope.Global.error.message = data.error;
                                }
                            }, function (error) {
                                $rootScope.Global.loading = false;
                                $rootScope.Global.error.hasError = true;
                                $rootScope.Global.error.message = error;
                            });
                        }
                    }

            }],
            link: function ($scope, $element, $attrs) {
                $scope.reference = $scope.$eval($attrs.bookingReference);
            }
        };
    }]);