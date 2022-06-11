/**
 * Created by udantha on 2/7/16.
 */
expressAdminApp.controller('ticketboxSeatReport', ['$scope', 'busService', '$rootScope', '$http',
    function ($scope, busService, $rootScope, $http) {

        $scope.printSeats = function(sector, bus){
            var route = bus.sectors[0].busRoute;
            var boardingLocations = route.boardingStops;
            var dropoffLocations = route.dropStops;
            
            var params = {
                'boardingLocation': boardingLocations[0].id,
                'dropoffLocation': dropoffLocations[0].id,
                'busType': sector['bus']['busTypeId'],
                'scheduleId': sector['scheduleId'],
                'bus': bus.sectors[0].bus.plateNumber,
                'session': $rootScope.session
            };
            var url = '/admin-panel/ticketbox-ajax/seatreport?';
            for(var key in params){
                url = url + '&' + key +'='+ params[key];
            }
            window.open(url);


        };

        $scope.sendCondaSms = function(sector, bus){
            if(confirm('Send Booking summary to conductor?')){
                $scope.Global.loading = true;
                var route = bus.sectors[0].busRoute;
                var boardingLocations = route.boardingStops;
                var dropoffLocations = route.dropStops;

                var request = $http.post('/admin-panel/ticketbox-ajax/alertconda', {
                    'boardingLocation': boardingLocations[0].id,
                    'dropoffLocation': dropoffLocations[0].id,
                    'busType': sector['bus']['busTypeId'],
                    'scheduleId': sector['scheduleId'],
                    'bus': bus.sectors[0].bus.plateNumber,
                    'session': $rootScope.session
                });
                request.then(function(response){
                    if(!response.data.error){
                        //set success message
                        $scope.Global.loading = false;
                        $scope.Global.success.isSuccess = true;
                        $scope.Global.success.message = "message sent successfully.";
                    }else{
                        $scope.Global.loading = false;
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = response.data.error;
                    }
                }, function(){
                    var error;
                    if (!angular.isObject(response.data) || !response.data.error) {
                        error = "An unknown error occurred.";
                    }else{
                        error = response.data.error;
                    }
                    $scope.Global.loading = false;
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = error;
                })
            }
        };

    }]
);