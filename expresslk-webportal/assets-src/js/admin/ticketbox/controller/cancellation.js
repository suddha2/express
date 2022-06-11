/**
 * Created by udantha on 10/6/14.
 */
expressAdminApp.controller('ticketCancellation', ['$scope', 'cancellationService',
    function ($scope, cancellationService) {

        $scope.bookingRef = '';
        $scope.bookingData = null;
        $scope.cancellationCharge = 0;
        $scope.loading = false;
        $scope.cancel = {};

        $scope.getInfo = function() {
            $scope.loading = true;
            cancellationService.getBookingDetails($scope.bookingRef)
                .then(function (data) {
                    if (!data.error) {
                        $scope.bookingData = data.booking;
                        $scope.cancellationCharge = data.cancellationCharge;
                        $scope.refundAmount = data.refundAmount;
                    }else {
                        $scope.Global.loading = false;
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = data.error;
                    }
                    $scope.loading = false;
                }, function (error) {
                    $scope.Global.loading = false;
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = error;
                    $scope.loading = false;
                });
        };
        
        $scope.cancelAndRefund = function (reference) {
            $scope.Global.loading = true;
            cancellationService.cancelBooking(reference, $scope.cancel)
                .then(function (data) {
                    if (!data.error) {
                        $scope.resetForms();
                        $scope.Global.success.isSuccess = true;
                        $scope.Global.success.message = "Booking has been cancelled.";
                    }else {
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = data.error;
                    }
                    $scope.Global.loading = false;
                }, function (error) {
                    $scope.Global.loading = false;
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = error;
                });
        };

        $scope.resetForms = function () {
            $scope.bookingRef = '';
            $scope.bookingData = null;
            $scope.cancellationCharge = 0;
            $scope.loading = false;
            $scope.cancel = {};
            //set form to pristene
            $scope.getbookingform.$setPristine();
            $scope.getbookingform.$setUntouched();
            //set form to pristene
            $scope.cancelform.$setPristine();
            $scope.cancelform.$setUntouched();
        };
        
    }]);