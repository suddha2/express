/**
 * Created by udantha on 7/2/16.
 */
expressAdminApp.controller('loadBusLocation', ['$scope', 'busLocationService', 'uiGmapGoogleMapApi',
    '$timeout',
    function ($scope, busLocationService, uiGmapGoogleMapApi, $timeout) {
        //define variables
        $scope.busList = [];
        $scope.currentBus = {
            bus: []
        };
        $scope.mapDefaults = {
            center: { latitude: 6.933874, longitude: 79.855397 },
            zoom: 15,
            refresh: false
        };
        $scope.currentLocation = {};
        $scope.marker = {
            options: {
                icon: '/img/icons/bus/bus-location-pin.png',
                draggable: false,
                labelContent: "Label",
                labelAnchor: "100 0",
                labelClass: "marker-labels"
            },
            events: {
                click: function (marker, eventName, args) {
                    //console.log('asdasdasd')
                }
            }
        };
        $scope.locations = [];
        $scope.startDate = moment().subtract(1, 'days').toDate();
        $scope.endDate = false;
        //poliline config
        $scope.polyline = {};
        $scope.error = false;
        $scope.locLoading = false;

        var LiveReload = {
            timer: null,
            start: function () {
                //stop before starting
                this.stop();
                this.timer = $timeout(function () {
                    //call the updater function again
                    updateLocation();
                }, 1000);
            },

            stop: function () {
                var self = this;
                if (self.timer) {
                    $timeout.cancel(self.timer);
                }
            }
        };


        //load list from backend
        busLocationService.getBusList()
            .then(function (data) {
                if(!data.error){
                    $scope.busList = data.bus
                }else{
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = data.error;
                }
            }, function (error) {
                $scope.Global.error.hasError = true;
                $scope.Global.error.message = error;
            });

        /**
         * Initial bus loading
         */
        $scope.loadBusLocations = function () {
            $scope.error = false;
            $scope.locLoading = true;
            if($scope.currentBus.bus){
                busLocationService.getBusLocations($scope.currentBus.bus.id, $scope.startDate, $scope.endDate)
                    .then(function (data) {
                        if(!data.error){
                            //show error
                            if(data.locations && data.locations.length>0){
                                //only after map loaded
                                uiGmapGoogleMapApi.then(function () {
                                    $scope.currentLocation = data.current;
                                    $scope.locations = data.locations;
                                    //set default location
                                    $scope.mapDefaults.center.latitude = $scope.currentLocation.latitude;
                                    $scope.mapDefaults.center.longitude = $scope.currentLocation.longitude;

                                    //refresh
                                    $scope.mapDefaults.refresh = true;
                                });
                            }else{
                                $scope.error = "No location data.";
                            }

                        }else{
                            $scope.error = data.error;
                        }
                        $scope.locLoading = false;
                    }, function (error) {
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = error;
                        $scope.locLoading = false;
                    });
            }
        };

        /**
         * @todo update location by long polling
         * Send last known location timestamp with bus id and retrieve all the locations after that
         * timestamp
         */
        function updateLocation() {
            busLocationService.getBusLocations($scope.currentBus.bus.id, $scope.startDate, $scope.endDate)
                .then(function (data) {
                    if(!data.error){
                        //show error
                        if(data.locations && data.locations.length>0){
                            //only after map loaded
                            uiGmapGoogleMapApi.then(function () {
                                $scope.currentLocation = data.current;
                                $scope.locations = data.locations;
                                //set default location
                                $scope.mapDefaults.center.latitude = $scope.currentLocation.latitude;
                                $scope.mapDefaults.center.longitude = $scope.currentLocation.longitude;

                                //refresh
                                $scope.mapDefaults.refresh = true;
                            });
                        }
                    }else{
                        $scope.error = data.error;
                    }
                }, function (error) {
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = error;
                });
        }

        $scope.toggleLiveLocation = function () {
            if(moment($scope.endDate).isValid()){
                //clear date and set false
                $scope.endDate = false;
            }else{
                //set a valid date
                $scope.endDate = moment().toDate();
            }
        };

        //set up data after google maps loaded
        uiGmapGoogleMapApi.then(function () {
            //define plyines
            $scope.polyline = {
                stroke: {
                    color: '#6060FB',
                    weight: 5
                },
                editable: false,
                clickable: false,
                draggable: false,
                geodesic: true,
                visible: true,
                icons: [{
                    icon: {
                        path: google.maps.SymbolPath.BACKWARD_OPEN_ARROW
                    },
                    offset: '50px',
                    repeat: '90px'
                }]
            };
        })

    }]);