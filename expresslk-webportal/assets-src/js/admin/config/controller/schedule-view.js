/**
 * Created by udantha on 10/22/14.
 */
expressAdminApp.controller('viewSchedule', ['$scope', 'ngTableParams', 'scheduleService', '$filter', '$interval',
    function ($scope, ngTableParams, scheduleService, $filter, $interval) {
        $scope.form = {};
        var scheduleData = [];

        //load data from server
        function loadSchedules(){
            // ajax request to api
            scheduleService.getSchedules()
                .then(function (data) {
                    if (data.result) {
                        scheduleData = data.result;
                        //reload data table
                        if ($scope.tableParams){
                            $scope.tableParams.reload();
                        }
                    }else{
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = data.error? data.error : 'Unknown error occurred.';
                    }
                }, function(error){
                    $scope.Global.loading = false;
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = error;
                });
        }

        $scope.tableParams = new ngTableParams({
            page: 1,            // show first page
            count: 10,
            sorting: {
                departureTime: 'desc'     // initial sorting
            }
        }, {
            total: 0,           // length of data
            getData: function ($defer, params) {
                scheduleData = params.sorting() ?
                    $filter('orderBy')(scheduleData, params.orderBy()) :
                    scheduleData;
                // update table params
                params.total(scheduleData.length);
                //set new data
                $defer.resolve(scheduleData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });

        $scope.search = function() {
            $scope.tableParams.reload();
        };

        loadSchedules();

        //update after 1 min
        //var interval = $interval(function () {
        //    loadSchedules();
        //}, 2000);

        $scope.getTrClass = function(schedule){
            var today = moment(),
                trDate = moment(schedule.departureTime);
            return {
                'success': today.startOf('day').isSame(trDate.startOf('day'))
            };
        };
    }]);