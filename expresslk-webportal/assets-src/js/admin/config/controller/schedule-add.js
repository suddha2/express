/**
 * Created by udantha on 10/22/14.
 */

expressAdminApp.controller('addSchedule', ['$scope', '$http', 'busesService', 'scheduleService', '$timeout',
    function ($scope, $http, busesService, scheduleService, $timeout) {

        var lastStartTimes = {};
        $scope.routes = {};
        $scope.bus = {};
		$scope.bus_plate='';
        $scope.bus_id = '';
        $scope.seatingProfile = '';
        $scope.seatingProfileList = [];
        $scope.min_date = new Date();
        $scope.max_date = null;
        $scope.recur_from = new Date();
        $scope.recur_to =  new Date();
        $scope.recurring = '';
        $scope.recurringIsActive = false;
		$scope.alternate_days = false;

        //open datepicker
        $scope.open = function($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.opened = true;
        };
		
		
		$scope.$watch('recur_from', function(newVal, oldVal) {
			if (oldVal != newVal ) {
                for(var routeIx in $scope.bus.busRoutes){
					var busBusRoute = $scope.bus.busRoutes[routeIx];
                    var route = busBusRoute.busRoute;
					$scope.routes[route.id].startTime = newVal;
				}
            }
        });
        /**
         * Watch for bus drop downn changes
         */

        $scope.loadBus = function(item, model, label, event){
			
			$scope.bus_id=item.id;
			
            //reset
            $scope.routes = {};
            $scope.bus = {};
                //start loader
                $scope.Global.loading = true;
                //get bus data
                busesService.getBus($scope.bus_id)
                    .then(function(data){
                        if(data.bus){
                            //attache bus data to bus scope
                            $scope.bus = data.bus;
                            //set permit expiry date or driving licence expiry date 
                            //(whichever occurs earlier) as max date for calendars
                            var expiryDate = null;
                            if ($scope.bus.permitExpiryDate) {
                                expiryDate = $scope.bus.permitExpiryDate;
                            }
                            if ($scope.bus.drivingLicenceExpiryDate) {
                                if (!expiryDate || expiryDate < $scope.bus.drivingLicenceExpiryDate) {
                                    expiryDate = $scope.bus.drivingLicenceExpiryDate
                                }
                            }
                            $scope.max_date = expiryDate;

                            //get seating profile
                            var busSeatingProfile = ($scope.bus.seatingProfile && $scope.bus.seatingProfile.id)? $scope.bus.seatingProfile.id : null;
                            //set seating profile
                            for (var i in data.seatingProfiles){
                                //check for selected value
                                if(data.seatingProfiles[i].id == busSeatingProfile){
                                    $scope.seatingProfile = data.seatingProfiles[i].id;
                                }
                                //data.seatingProfiles[i].selected = (data.seatingProfiles[i].id==$scope.bus.seatingProfile.id);
                            }
                            $scope.seatingProfileList = data.seatingProfiles;
                            //activate recurring checkbox
                            $scope.recurringIsActive = true;

                            //calculate stops times
                            calculateStops();
                        }else{
                            $scope.Global.error.hasError = true;
                            $scope.Global.error.message = data.error;
                        }
                        $scope.Global.loading = false;
                    }, function(error){
                        $scope.Global.loading = false;
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = error;
                    });
            
        };

        $scope.reloadStops = function(){
            //calculate stops times
            calculateStops();
        };

        /**
         * Calculate times for bus stops
         */
        function calculateStops(){
            //check if bus data has been loaded
            if($scope.bus.id){
                var recurringShouldBeActive = true;
                //loop through bus routes
                for(var routeIx in $scope.bus.busRoutes){
                    var busBusRoute = $scope.bus.busRoutes[routeIx];
                    var route = busBusRoute.busRoute;

                    //set route of undefined
                    if(!$scope.routes.hasOwnProperty(route.id)){
                        $scope.routes[route.id] = {};
                    }
                    //set bus scedule default start/end dates
                    if(!$scope.routes[route.id].hasOwnProperty('startTime')){
                        $scope.routes[route.id].startTime = lastStartTimes.hasOwnProperty(route.id) ? lastStartTimes[route.id] : new Date();
                    }

                    //check for date time values
                    if((Object.prototype.toString.call($scope.routes[route.id].startTime) !== '[object Date]')){
                        $scope.routes[route.id].startTime = new Date();
                    }

                    //set load factor if not set
                    if(!$scope.routes[route.id].hasOwnProperty('loadFactor')){
                        $scope.routes[route.id].loadFactor = busBusRoute.loadFactor;
                    }
                    //disabled or not
                    if(!$scope.routes[route.id].hasOwnProperty('disabled')){
                        $scope.routes[route.id].disabled = false;
                    }

                    //save current start time
                    lastStartTimes[route.id] = $scope.routes[route.id].startTime;

                    //calculate route stops
                    if(route.hasOwnProperty('routeStops')){
                        /**
                         * calculate changed waiting and travel times
                         */
                        //only if input models has been generated
                        var busStopTimeCalc = {},
                            lastEndTime = null,
                            lastStopId = null;

                        if($scope.routes[route.id].hasOwnProperty('busStops')){
                            for(var tmpStopId in $scope.routes[route.id].busStops){
                                (function(){
                                    var tmpBusStop = $scope.routes[route.id].busStops[tmpStopId],
                                        tmpStartTime = tmpBusStop.startTime,
                                        tmpEndTIme = tmpBusStop.endTime;
                                    busStopTimeCalc[tmpStopId] = {};

                                    //get waiting time
                                    busStopTimeCalc[tmpStopId].waitingTime = Util.getTimeDifferenceBetween(tmpStartTime, tmpEndTIme);
                                    busStopTimeCalc[tmpStopId].travelTime = [0, 0, 0];

                                    //set up travel time between stops. Save this stop for next, get last stops end time for this stop
                                    if(lastStopId){
                                        //update last stop's travel time
                                        busStopTimeCalc[lastStopId].travelTime = Util.getTimeDifferenceBetween(lastEndTime, tmpStartTime);
                                    }

                                    //save stop id and end time for next stop
                                    lastStopId = tmpStopId;
                                    lastEndTime = tmpEndTIme;
                                })();
                            }

                        }

                        /**
                         * Calculate stop differences and set start end times for inputs
                         */
                        //define temp variables
                        var lastStopTime = null,
                            previousTravelTime = null,
                            busStops = {};

                        for(var busStopIx in route.routeStops){
                            /**
                             * Initialize variables
                             */
                            var busStop = route.routeStops[busStopIx],
                                stopId = 'stop-'+ busStop.id;

                            //set name for inputs index
                            $scope.bus.busRoutes[routeIx].busRoute.routeStops[busStopIx].itKey = stopId;

                            //get waiting time in H M S
                            var waitingTime = (typeof busStop.waitingTime === 'string') ? busStop.waitingTime.split(':') : [0, 0, 0];
                            var travelTime = (typeof busStop.travelTime === 'string') ? busStop.travelTime.split(':') : [0, 0, 0];
                            //check if the calculated time differences are available.
                            //if so change waiting and travel time accordingly
                            if(busStopTimeCalc.hasOwnProperty(stopId)){
                                waitingTime = busStopTimeCalc[stopId].waitingTime;
                                //travel time
                                travelTime = busStopTimeCalc[stopId].travelTime;
                            }

                            busStops[stopId] = {};
                            busStops[stopId].stopId = busStop.stop.id;
                            busStops[stopId].index = busStop.index;

                            //calculate start/end time
                            if(previousTravelTime){
                                busStops[stopId].startTime = previousTravelTime;
                                //add waiting time to start time
                                busStops[stopId].endTime = moment(busStops[stopId].startTime)
                                    .add(waitingTime[0], 'hours').add(waitingTime[1], 'minutes').add(waitingTime[2], 'seconds')
                                    .toDate();
                            }else{
                                //this is the first stop, means the starting station. So substract waiting time
                                busStops[stopId].startTime = moment($scope.routes[route.id].startTime)
                                    .subtract(waitingTime[0], 'hours').subtract(waitingTime[1], 'minutes').subtract(waitingTime[2], 'seconds')
                                    .toDate();
                                //this is the first stop, end time is the start time of the bus.
                                busStops[stopId].endTime = new Date($scope.routes[route.id].startTime);
                            }

                            //calculate next stop start time. Add waiting and travel time.
                            previousTravelTime = moment(busStops[stopId].endTime)
                                .add(travelTime[0], 'hours').add(travelTime[1], 'minutes').add(travelTime[2], 'seconds')
                                .toDate();
                            lastStopTime = new Date(busStops[stopId].startTime);
                        }
                        //save bus stops array
                        $scope.routes[route.id].busStops = busStops;
                        //set end time
                        $scope.routes[route.id].endTime = lastStopTime;
                    }

                    //check if start and end dates are different.
                    //If they are different cannot be added as recurring schedules
                    // if(!moment($scope.routes[route.id].startTime).startOf('day')
                            // .isSame(moment($scope.routes[route.id].endTime).startOf('day'))){
                        // recurringShouldBeActive = false;
                    // }
                }

                $scope.recurringIsActive = recurringShouldBeActive;
                //uncheck recurring if should be is false
                if (!recurringShouldBeActive){
                    $scope.recurring = false;
                }
            }
        }

        $scope.save = function() {
            //run validations
            if($scope.recurring && !confirm('Recurring date range active. Is it intended?')){
                return;
            }
            //create array
            var data = {
                busId: $scope.bus.id,
                seatingProfile: $scope.seatingProfile,
                routes: $scope.routes,
                driver: ($scope.bus.driver? $scope.bus.driver.id : null),
                conductor: ($scope.bus.conductor? $scope.bus.conductor.id : null),
                from: $scope.recur_from,
                to: $scope.recur_to,
                recurring: $scope.recurring,
				alternateDays : $scope.alternate_days
            };
			 
            $scope.Global.loading = true;
            //create schedule
            scheduleService.createSchedule(data)
                .then(function(data){
                    if(data.success){
                        var oldBusId = $scope.bus_id;
                        //set success
                        $scope.bus_id = '';
                        //reset
                        $scope.routes = {};
                        $scope.bus = {};
                        $scope.seatingProfile = '';
						$scope.bus_plate='';
						$scope.recurring='';
						$scope.recurringIsActive = false;
                        $scope.Global.success.isSuccess = true;
                        $scope.Global.success.message = 'Schedule(s) added successfully';

                        $timeout(function(){
                            $scope.bus_id = '';
                        }, 100);
                    }else{
                        var er = data.error ? data.error : 'Server error occurred. Please try again.';
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = er;
                    }
                    $scope.Global.loading = false;
                }, function(error){
                    $scope.Global.loading = false;
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = error;
                });
        };

		$scope.getBusList = function(val) {
								return $http.get('/admin-panel/BusSchedule/getBusByPlate?', {
								  params: {
									'plateNumber': val
								  }
								}).then(function(response){
									
								  return response.data.map(function(item){
										return item;
								  });
								});
							  };

		$scope.busList = function(bus) {
								if (bus == null || bus == undefined){
									return;
								}
								if (bus.plateNumber == '' || bus.plateNumber == undefined) {
								   return '';
								}
								return bus.plateNumber;
							};
    }]);