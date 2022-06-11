/**
 * Created by Udantha on 2/27/17.
 *
 * Waiting List directive.
 * Can be placed anywhere on the front as a button. Which will fire up window with waitlist form
 */
expressFrontApp.directive('waitingList', ['$compile', '$animate', function ($compile, $animate) {
    return {
        restrict: 'EA',
        replace: true,
        transclude: true,
        template: '<button type="button" ng-click="openPopup()" ng-transclude></button>',
        scope: {
            scheduleId: '=',
            scheduleName: '='
        },
        controller: ['$scope', '$uibModal',
            function ($scope, $uibModal) {

                $scope.openPopup = function () {
                    /**
                     * Create a modal instance
                     */
                    var modalInstance = $uibModal.open({
                        animation: true,
                        templateUrl: '/app/index/loadpartial?p=popup-waitinglist',
                        controller: ['$scope', '$uibModalInstance', 'items', '$http', function (scp, $uibModalInstance, items, $http) {
                            scp.response = {
                                loading: false,
                                error: false,
                                success: false
                            };
                            scp.data = {
                                seats: 1
                            };
                            scp.scheduleName = $scope.scheduleName;

                            //submit the form
                            scp.submit = function () {
                                //reset values
                                scp.response = {
                                    loading: false,
                                    error: false,
                                    success: false
                                };
                                scp.response.loading = true;
                                scp.data.scid = items.scheduleId;
                                //send request to backend
                                $http.post('/app/search/waitinglist', scp.data).then(function (success) {
                                    //success respone
                                    if(success && success.data.success){
                                        scp.response.success = true;
                                    }else{
                                        scp.response.error = true;
                                    }
                                    scp.response.loading = false;
                                }, function (error) {
                                    scp.response.loading = false;
                                    scp.response.error = true;
                                })
                            };

                            scp.close = function () {
                                $uibModalInstance.dismiss('cancel');
                            };
                        }],
                        //size: 'lg',
                        appendTo: 'body',
                        windowClass: 'popup fullscreen-popup',
                        resolve: {
                            items: function () {
                                return {
                                    scheduleId: $scope.scheduleId,
                                    scheduleName: $scope.scheduleName
                                };
                            }
                        }
                    });

                    modalInstance.result.then(function (selectedItem) {
                        //$scope.bookingId = bookingId;
                    }, function () {
                    });

                };


            }],
        link: function ($scope, element, attrs) {

        }
    }
}]);