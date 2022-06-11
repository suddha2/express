/**
 * Created by udantha on 4/25/14.
 */
/**
 * Controller to handle search results.
 */
expressAdminApp.controller('ticketboxSchedule', ['$scope', '$http', '$stateParams', 'ngTableParams', '$rootScope', '$location', '$state', 'busService', 'cityDestinationService', '$timeout',
    function ($scope, $http, $stateParams, ngTableParams, $rootScope, $location, $state, busService, cityDestinationService, $timeout) {
        //start loader
        $scope.loading = false;
        $scope.hasData = false;

        $scope.searchFormData = {};
        $scope.searchFormData.date = $stateParams.date;
        $scope.scheduleIndex = false;

        //variable definitions
        var defaultDestinations = [],
            loadTime = null;
        $scope.destinations = [];
        $scope.cityList = [];
        $scope.departuresLoading = true;
        $scope.destinationsLoading = false;
        $scope.destinationPlaceHolder = 'select';
        $scope.formTitle = false;
        $scope.formTitleIsOpen = true;

        //populate city list
        cityDestinationService.getCityList()
            .then(function(data) {
                $scope.cityList = data;
                $scope.departuresLoading = false;
                if ($stateParams.from) {
                    // set selected city for departure location
                    var fromCity = null;
                    for (var i = 0; i < $scope.cityList.length; i++) {
                        if ($scope.cityList[i].id == $stateParams.from) {
                            fromCity = $scope.cityList[i];
                            break;
                        }
                    }
                    if (fromCity) {
                        $scope.searchFormData.start = fromCity;
                    }
                }
            });

        /**
         * Watch for start dropdown changes
         */
        $scope.$watchCollection('searchFormData.start', function(newVal, oldVal) {
            if (newVal && newVal !== '') {
                getDestinations(newVal.id);
            }
        });
		
		
		// $scope.$watchCollection('searchFormData.end.id', function(newVal, oldVal) {
			// console.log('End city Changed!!! =================');
            // if (newVal && newVal !== '') {
                // if($scope.searchFormData.date==''){
					// console.log('Setting date to current =================');
					// $scope.searchFormData.date=new Date();
				// }
            // }
        // });
		
        //Collapse/expand search box based on result. Commented out to make use of it for Express
        //$scope.$watchCollection('searchFormData.end', function(newVal, oldVal) {
        //    if (newVal && newVal !== '') {
        //        if($scope.searchFormData.date){
        //            $scope.formTitleIsOpen = false;
        //        }
        //    }
        //});

        var curDate = new Date();
        //check if user has permission to load history
        if(ACL.fragmentCanExecute(Fragment.Name.ticketBoxHistory)){
            //set date to far earlier date
            curDate = (new Date()).setYear( curDate.getYear()-2 );
        }
        //date picker settings
        $scope.minDate = curDate;
        $scope.maxDate = (new Date()).setMonth( (new Date()).getMonth()+2 );
        $scope.opened = false;
        //open datepicker
        $scope.openCal = function($event) {
            //$event.preventDefault();
            //$event.stopPropagation();

            $scope.opened = !$scope.opened;
        };
        //datepicker options
        $scope.dateOptions = {
            showWeeks:'false'
        };

        //process search form
        $scope.submitSearch = function () {
			//check if the parameters are correct
            if(!$scope.searchFormData || !$scope.searchFormData.start || !$scope.searchFormData.end || !$scope.searchFormData.date){
                return;
            }
            var from = $scope.searchFormData.start.id;
            var to = $scope.searchFormData.end.id;
            var rawdate = new Date($scope.searchFormData.date),
                date = rawdate.getFullYear() +'-'+ ("0" + (rawdate.getMonth() + 1)).slice(-2) +'-'+ ("0" + rawdate.getDate()).slice(-2);

            //reload if same parameters
            if(from==$stateParams.from && to==$stateParams.to && date==$stateParams.date){
                $state.transitionTo($state.current, $stateParams, {
                    reload: true,
                    inherit: false,
                    notify: true
                });
            }else{
                $state.go('choose', {from: from, to: to, date: date, page: 1});
            }
        };

        var dataOneWay = [];

        /**
         * @param params
         */
        var getScheduleData = function(params){
            //stop if there are no params
            if(!(params.date && params.from && params.to)){
                return;
            }

            $scope.loading = true;
            // ajax request to api
            //var timestamp = (new Date(params.date)).getTime();
            busService.getTicketSchedule(params.from, params.to, params.date, params.page)
                .then(function (data) {
                    //stop loader
                    $scope.loading = false;
                    if (data.result) {
                        var oneway = data.result['oneway'];

                        //set session in root scope
                        $rootScope.session = data.session;

                        dataOneWay = oneway;

                        //set head title collapse to false if the data is empty
                        if(oneway.length==0){
                            $scope.formTitleIsOpen = true;
                        }
                        //set has data variable for displaying
                        $scope.hasData = (oneway.length!==0);
                        //reload data table
                        if ($scope.tableParams){
                            $scope.tableParams.reload();
                        }
                    }else{
                        //show error
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = data.error;
                    }
                }, function(error){
                    //stop loader
                    $scope.loading = false;
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = error;
                });
        };

        //load data from server
        $scope.tableParams = new ngTableParams({
            page: ($stateParams.page ? $stateParams.page : 1),            // show first page
            count: 5
        }, {
            total: 0,           // length of data
            getData: function ($defer, params) {
                // update table params
                params.total(dataOneWay.length);
                //set new data
                $defer.resolve(dataOneWay.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });

        /**
         * Load data
         */
        getScheduleData($stateParams);

        //set up change page event handler
        $scope.goToPage = function(page){
            $state.transitionTo('choose', {
                from: $stateParams.from,
                to: $stateParams.to,
                date: $stateParams.date,
                page: page});
        };

        /**
         * View seats
         */
        $scope.viewSeats = function(sector, bus){
            //save selected bus in bus service
            busService.setSelectedBus(bus);

            var busroute = bus.sectors[0].busRoute;
            var boardingLocations = busroute.boardingStops;
            var dropoffLocations = busroute.dropStops;

            //broadcast event
            $rootScope.$broadcast('showSeats', {
                boardingLocations: boardingLocations,
                dropoffLocations: dropoffLocations,
                busType: sector['bus']['busTypeId'],
                scheduleId: sector['scheduleId'],
                busId: sector.bus.id,
                seatPrice : bus.prices,
                journeyData : {
                    fromName: sector.fromCityName,
                    toName: sector.toCityName,
                    busPlate: sector.bus.plateNumber,
                    busName: sector.bus.name,
                    departure: sector.departureTime
                },
                genderRequired: bus.sectors[0].busRoute.genderRequired
            });
            //set selected indes
            $scope.scheduleIndex = bus.resultIndex;
        };

        /**
         * Get destinations and populate array
         *
         * @param cityId
         */
        function getDestinations(cityId) {
            //clear timeout and start new one
            //$timeout.cancel(loadTime);
            //loadTime = $timeout(function() {
            //    $scope.destinationsLoading = true;
            //}, 500);

            //reset destination dropdown
            $scope.destinations = defaultDestinations;
            //set loading
            $scope.destinationPlaceHolder = 'loading';
            $scope.searchFormData.end = null;

            // call service and get data
            cityDestinationService.getDestinationForCity(cityId)
                .then(function(data){
                    if (!data.error){
                        // set destinations in array
                        $scope.destinations = data.destinations;
                        
                        // if empty, show message
                        if (data.destinations.length == 0) {
                            $scope.destinationPlaceHolder = 'empty'
                        } else {
                            $scope.destinationPlaceHolder = 'select';
                        }
                        
                        if ($stateParams.to) {
                            // set destination in the dropdown
                            var toCity = null;
                            for (var i = 0; i < $scope.destinations.length; i++) {
                                if ($scope.destinations[i].id == $stateParams.to) {
                                    toCity = $scope.destinations[i];
                                    break;
                                }
                            }
                            if (toCity) {
                                $scope.searchFormData.end = toCity;
                            }
                        }
                    } else {
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = data.error;
                    }
                    $timeout.cancel(loadTime);
                    $scope.destinationsLoading = false;
                }, function() {
                    $timeout.cancel(loadTime);
                    $scope.destinationsLoading = false;
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = 'Network error occurred. May be your connection is too slow. Please try again.';
                });
        }
    }]);