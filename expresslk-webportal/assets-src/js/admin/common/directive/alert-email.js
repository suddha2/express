/**
 * Created by udantha on 2/16/16.
 */
expressAdminApp
    .directive("alertEmail", ['$window', function ($window) {
        return {
            restrict: 'E',
            scope: false,
            template: '<button type="button" class="btn btn-warning" ng-click="resendEmail()">Re-send Email</button>',
            controller: ['$scope', '$element', 'bookingService', '$rootScope',
                function($scope, $element, bookingService, $rootScope){

                    $scope.resendEmail = function(){
                        if(confirm('Are you sure to re-send the Email?')){
                            $rootScope.Global.loading = true;
                            //send request
                            bookingService.resedEmail($scope.reference).then(function (data) {
                                if (!data.error) {
                                    //set success message
                                    $rootScope.Global.loading = false;
                                    $rootScope.Global.success.isSuccess = true;
                                    $rootScope.Global.success.message = "Booking Email sent to '"+ data.email +"' successfully.";
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