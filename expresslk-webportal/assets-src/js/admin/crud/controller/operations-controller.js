expressAdminCrudApp.controller('operationsController', [
    '$scope',
    '$window',
    'scheduleService',
    'reportService',
    '$rootScope',
    '$state',
    '$expConfirm',
    '$uibModal',
    function ($scope, $window, scheduleService, $reportService, $rootScope, $state, $expConfirm, $uibModal) {

        $scope.printBookingReport = function (scheduleId) {
            //$scope.viewBookingReport(scheduleId, true);
			
			
			var width = 960,
                height = 500;
            $window.open('/admin-panel/me/seatreservationprint?schedule=' + scheduleId,
                "Collection Report",
                "status=no,width=" + width + ",height=" + height + ",top=" + ((screen.height / 2) - (height / 2)) + ",left=" + ((screen.width / 2) - (width / 2)) + ",toolbar=no,menubar=no,scrollbars=no,location=no");

			
            return false;
        };

        $scope.viewBookingReport = function (scheduleId, print) {
            submitReport('ScheduleBooking', {SCHEDULE_ID: scheduleId}, print);
            return false;
        };

        $scope.printPaymentReport = function (scheduleId) {
            //$scope.viewPaymentReport(scheduleId, true);
            // New report screen 
			var width = 960,
                height = 500;
            $window.open('/admin-panel/me/tripcollectionprint?schedule=' + scheduleId,
                "Collection Report",
                "status=no,width=" + width + ",height=" + height + ",top=" + ((screen.height / 2) - (height / 2)) + ",left=" + ((screen.width / 2) - (width / 2)) + ",toolbar=no,menubar=no,scrollbars=no,location=no");

			
			return false;
        };

        $scope.viewPaymentReport = function (scheduleId, print) {
            submitReport('BusBookingSummary', {SCHEDULE_ID: scheduleId}, print);
            return false;
        };

        $scope.changeStageTo = function (scheduleId, stageCode) {
            $rootScope.Global.loading = true;
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: '/admin-panel/bus-service/mockschedulestage?scheduleId='+ scheduleId +'&stageCode='+stageCode,
                controller: ['$scope', '$modalInstance', 'items', function (scp, $modalInstance, items) {
                    $rootScope.Global.loading = false;
                    scp.items = items;

                    scp.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                    //confirm move to next stage
                    scp.ok = function () {
                        $modalInstance.dismiss('cancel');
                        //commit stage change
                        $scope.commitStageChange(items.scheduleId, items.stageCode);
                    };
                }],
                size: 'lg',
                appendTo: 'body',
                resolve: {
                    items: function () {
                        return {
                            scheduleId: scheduleId,
                            stageCode: stageCode
                        };
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $rootScope.Global.loading = false;
            }, function () {
            });
        };

        /**
         * Commit stage change to backend
         * @param scheduleId
         * @param stageCode
         */
        $scope.commitStageChange = function (scheduleId, stageCode) {
            $expConfirm({
                text: 'Are you sure you want to change stage to "'+ stageCode +'"?',
                title: 'Change the stage?'
            }).then(function() {
                //start loader
                $rootScope.Global.loading = true;
                scheduleService.setStage(scheduleId, stageCode)
                    .then(
                        function (data) {
                            $rootScope.Global.loading = false;
                            if (!data.error) {
                                $rootScope.Global.success.isSuccess = true;
                                $rootScope.Global.success.message = "Moved to new stage. Wait while list reloads.";
                                $state.reload();
                            } else {
                                $rootScope.Global.error.hasError = true;
                                $rootScope.Global.error.message = data.error;
                            }
                        }, function (error) {
                            $rootScope.Global.loading = false;
                            $rootScope.Global.error.hasError = true;
                            $rootScope.Global.error.message = error;
                        });
            });
        };

        // process report generation
        var submitReport = function (type, parameters, print) {
            $reportService.generateReport(type, parameters);
        };
		
		var validNumberPlate = function (plateNumber){
			
		};
		
    }
]);
expressAdminCrudApp.filter('regex', function() {
   return function(val){
	// Check for pattern 'Starting with Alphabet, followed by 3 digids
    //var RegExp = /^[A-Z]\d{3}/;
    var RegExp = /(^[A-Z]\d{3})|(^[A-Z]{2}\d)/;
	
    var match = RegExp.exec(val);
    // If there's a match then plate number is dummy. i.e: B099
	// then return FALSE
	return !match;
   };
});