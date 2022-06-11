/**
 * Created by udantha on 4/25/14.
 */
/**
 * Controller to handle search results.
 */
expressFrontApp.controller('searchList', ['$scope', '$http', '$stateParams', '$location', '$rootScope', '$compile', 'translate','$state',
    function ($scope, $http, $stateParams, $location, $rootScope, $compile, translate,$state) {
        //start loader
        $scope.loading = true;
        $scope.tableHeads = [];
        $scope.resultView = Util.isMobile() ? 'mobileSearchResult' : 'desktopSearchResult';
        $scope.schedules = [];
        $scope.suggested = false;

        //set current loading screen
        $rootScope.currentScreen = 'result';
        $scope.seatsLoading = false;

        //get search terms
        $scope.searchTerms = {
            getFromId: function () {
                return $stateParams.from;
            },
            getFromName: function () {
                return decodeURIComponent($stateParams.fromName);
            },
            getToId: function () {
                return $stateParams.to;
            },
            getToName: function () {
                return decodeURIComponent($stateParams.toName);
            },
            getDate: function () {
                if($stateParams.date){
                    return $stateParams.date;
                }else{
                    return Util.getSearchDefaultDate();
                }
            },
            getOfferCode: function () {
                return $stateParams.offerCode;
            }
        };

        $scope.subSearchFormData = {
            start: {
                id: ($scope.searchTerms.getFromId()),
                name: ($scope.searchTerms.getFromName())
            },
            end: {
                id: ($scope.searchTerms.getToId()),
                name: ($scope.searchTerms.getToName())
            },
            date: new Date($scope.searchTerms.getDate()),
            offercode: ($scope.searchTerms.getOfferCode())
        };

        $("body").animate({scrollTop: 0}, "fast");
		
		$scope.$watchCollection('subSearchFormData', function(newVal, oldVal) {
			
			
			if(newVal !== oldVal && newVal && ((newVal.date!==oldVal.date) && (newVal.start.id >0) && (newVal.end.id >0))) {
				var rawdate = $scope.subSearchFormData.date;
				$state.go('search-result', {
                            fromName: $scope.subSearchFormData.start.name,
                            toName: $scope.subSearchFormData.end.name,
                            from: $scope.subSearchFormData.start.id,
                            to: $scope.subSearchFormData.end.id,
                            date: rawdate.getFullYear() +'-'+ ("0" + (rawdate.getMonth() + 1)).slice(-2) +'-'+ ("0" + rawdate.getDate()).slice(-2),
                            offerCode: $scope.subSearchFormData.offercode
                        });
			}
        },true);
		
        /**
         * Get schedules from server
         */
        var fnGetBusSchedules = function(){
            // ajax request to api
            $http.post('/app/search/search', {
                from: ($scope.searchTerms.getFromId()),
                fromName: ($scope.searchTerms.getFromName()),
                to: ($scope.searchTerms.getToId()),
                toName: ($scope.searchTerms.getToName()),
                date: $scope.searchTerms.getDate(),
                offercode: ($scope.searchTerms.getOfferCode())
            })
                .success(function (data) {
                    //stop loader
                    $scope.loading = false;
                    if (data && data.result) {
                        $scope.schedules = data.result['oneway'];
                        //set session in root scope
                        $rootScope.session = data.session;
                        //get suggested status
                        $scope.suggested = data.suggested;
						$scope.scheduleTimlineMarker();
                    } else {
                        //set empty
                        $scope.schedules = [];
                        //show error
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = data.error;
                    }
                })
                .error(function () {
                    //stop loader
                    $scope.loading = false;
                    //set empty
                    $scope.schedules = [];
                });
        };
		
		
		$scope.scheduleTimlineMarker = function(){
			
			for (var i = 0, length = $scope.schedules.length; i < length; i++) {
				var item = $scope.schedules[i];
				var timeLineCircles = "";
				var timeLineConnectors = "";
				var timeLineTexts = "";
				
				if(item.sectors[0].fromCityName === item.sectors[0].schedule.actualFromCity){
					timeLineCircles ='A';
					//timeLineCircles = '<li class="active"><a href="#" class="node-dot"></a></li>';
					//timeLineTexts = "<li> {{bus.sectors[0].fromCityName}} <br />  {{bus.sectors[0].arrivalTime | moment:'YYYY-MM-DD hh:mm A'}} </li>";
				} 
				if(item.sectors[0].fromCityName != item.sectors[0].schedule.actualFromCity){
					timeLineCircles ='DA';
					// timeLineCircles = '<li ><a href="#" class="node-dot"></a></li><li class="active"><a href="#" class="node-dot"></a></li> ';
					// timeLineTexts = "<li> {{bus.sectors[0].actualFromCity}} <br />  {{bus.sectors[0].schedule.arrivalTime | moment:'YYYY-MM-DD hh:mm A'}} </li>"+
									// "<li> {{bus.sectors[0].fromCityName}} <br />  {{bus.sectors[0].arrivalTime | moment:'YYYY-MM-DD hh:mm A'}} </li>";
				} 
				if(item.sectors[0].toCityName != item.sectors[0].schedule.actualToCity){
					timeLineCircles +='AD';
					// timeLineCircles = '<li class="active"><a href="#" class="node-dot"></a></li><li ><a href="#" class="node-dot"></a></li> ';
					// timeLineTexts = "<li> {{bus.sectors[0].toCityName}} <br />  {{bus.sectors[0].arrivalTime | moment:'YYYY-MM-DD hh:mm A'}} </li>"+
									// "<li> {{bus.sectors[0].actualToCity}} <br />  {{bus.sectors[0].schedule.arrivalTime | moment:'YYYY-MM-DD hh:mm A'}} </li>";
									// ;
				}
				if(item.sectors[0].toCityName === item.sectors[0].schedule.actualToCity){
					timeLineCircles +='A';
					// timeLineCircles = '<li class="active"><a href="#" class="node-dot"></a></li>';
				}
				
				
				$scope.schedules[i].timeLineCircles = timeLineCircles;
				$scope.schedules[i].timeLineConnectors = timeLineConnectors;
				$scope.schedules[i].timeLineTexts = timeLineTexts;
			}
		}
		
        fnGetBusSchedules();

        $scope.datesAreEqual = function(dt1, dt2){
            return (moment(dt1).startOf('day').diff(moment(dt2).startOf('day')) === 0)
        };

        /**
         * ========== show seat layout logic ==========
         */
        $scope.seatLayoutIsVisible = false;
        /**
         * Toggle view seats
         */
        var childScope;
        $scope.toggleSeatLayout = function(busSector, bus, $event){
            var elem = $event.currentTarget || $event.srcElement,
                container = $(elem).closest('.booking-schedule-container');

            //close if layout is available
            $scope.closeLayout();

            if (!$scope.seatLayoutIsVisible){
                $scope.seatLayoutIsVisible = true;

                //add selected row class
                container.addClass('seats-displayed');

                childScope = $scope.$new();
                //set bus data into scope
                childScope.busSector = busSector;
                childScope.bus = bus;
                var compiledDirective = $compile($('#showSeatsTmpl').html());
                var directiveElement = compiledDirective(childScope);
                container.find('.booking-item-container')
                    .append(directiveElement);
                //animate height
                //directiveElement.animate({opacity: 1, height: 'auto'}, 'slow');
                //directiveElement.slideDown(1000);

                //add seat displayed class to body
                $('body').addClass('seatlayout-displayed');
            }
        };

        $scope.closeLayout = function(){
            //remove selected row classes
            $('.seats-displayed').removeClass('seats-displayed');
            //close seats before showing a list
            if($scope.seatLayoutIsVisible){
                var rows = $('.seat-info-row');
                childScope.$destroy();
                rows.remove();
                //rows.slideUp(1000, function () {
                //    childScope.$destroy();
                //    $(this).remove();
                //});
                $scope.seatLayoutIsVisible = false;
                //remove displayed class
                $('.seatlayout-displayed').removeClass('seatlayout-displayed');
            }
        };

        //register functions on destroy
        $scope.$on('$destroy', function(){
            //close if layout is available to remove unwanted classes
            $scope.closeLayout();
        });
    }]);
