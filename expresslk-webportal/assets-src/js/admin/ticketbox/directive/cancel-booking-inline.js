/**
 * Created by udantha on 6/14/16.
 */
expressAdminApp
    .directive("cancelBooking", ['$uibModal', function ($uibModal) {
        return {
            restrict: 'A',
            scope: false,
            link: function ($scope, $element, $attrs) {
                var reference = $scope.$eval($attrs.cancelBooking);
                
                //fire up popup
                $element.on('click', function () {
                    //open modal
                    var modalInstance = $uibModal.open({
                        animation: true,
                        templateUrl: 'cancellation_model',
                        controller: 'cancellationModalInstanceCtrl',
                        size: 'lg',
                        appendTo: 'body',
                        windowClass: 'popup',
                        resolve: {
                            items: function () {
                                return {
                                    bookingId: reference
                                };
                            }
                        }
                    });

                    modalInstance.result.then(function (selectedItem) {
                        $scope.reference = reference;
                    }, function () {
                    });

                })
            }
        };
    }]);

expressAdminApp.controller('cancellationModalInstanceCtrl', ['$scope', 'cancellationService', '$rootScope', '$uibModalInstance', 'items',
    function($scope, cancellationService, $rootScope, $uibModalInstance, items){

        $scope.bookingData = null;
        $scope.errors = null;
        $scope.reference = items.bookingId;
        $scope.cancel = {
            hasRefund: false
        };

        //get booking details
        $scope.loading = true;
        //get booking's info
        cancellationService.getBookingDetails($scope.reference)
            .then(function (data) {
                if (!data.error) {
                    $scope.bookingData = data.booking;
                }else{
                    $scope.errors = data.error;
                }
                $scope.loading = false;
            }, function (error) {
                $scope.loading = false;
                $scope.errors = error;
            });

        $scope.cancelBooking = function(){
            if(confirm('Are you sure to cancel this booking?')){
                $scope.loading = true;
                cancellationService.cancelBooking($scope.reference, $scope.cancel)
                    .then(function (data) {
                        if (!data.error) {
                            $scope.resetForms();
                            $scope.Global.success.isSuccess = true;
                            $scope.Global.success.message = "Booking has been cancelled.";
                            //broadcast message to reload seats
                            $rootScope.$broadcast('reloadSeats');
                            //close popup
                            $scope.ok();
                        }else {
                            $scope.Global.error.hasError = true;
                            $scope.Global.error.message = data.error;
                        }
                        $scope.loading = false;
                    }, function (error) {
                        $scope.loading = false;
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = error;
                    });
            }
        };

        $scope.resetForms = function () {
            $scope.bookingRef = '';
            $scope.bookingData = null;
            $scope.cancellationCharge = 0;
            $scope.loading = false;
            $scope.cancel = {
                hasRefund: false
            };
            //set form to pristene
            $scope.cancelform.$setPristine();
            $scope.cancelform.$setUntouched();
        };

        $scope.ok = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);