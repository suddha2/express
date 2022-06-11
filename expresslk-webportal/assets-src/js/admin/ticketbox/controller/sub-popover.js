/**
 * Created by udantha on 2/15/16.
 */
expressAdminApp.controller('subBookingPopOver', ['$scope', '$uibModal',
    function ($scope, $uibModal) {

        $scope.showBookingDetails = function (bookingId) {

            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'bookking_model',
                controller: 'bookingModalInstanceCtrl',
                size: 'lg',
                appendTo: 'body',
                windowClass: 'popup fullscreen-popup',
                resolve: {
                    items: function () {
                        return {
                            bookingId: bookingId
                        };
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.bookingId = bookingId;
            }, function () {
            });

        }
    }]);

expressAdminApp.controller('bookingModalInstanceCtrl', ['$scope', '$uibModalInstance', 'items', '$sce',
    function ($scope, $uibModalInstance, items, $sce) {

        $scope.popupUrl =  '/index.php/admin-panel/?type=min#/booking/edit/'+ items.bookingId;

        $scope.ok = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);