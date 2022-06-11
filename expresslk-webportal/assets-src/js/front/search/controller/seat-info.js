/**
 * Created by udantha on 4/25/14.
 */
/**
 * Controller for Seat info
 */
expressFrontApp.controller('seatInfoSubCtrl', ['$scope', '$http', '$state', '$stateParams', '$rootScope', 'translate',
    function ($scope, $http, $state, $stateParams, $rootScope, translate) {
    //initialize
    $scope.seatInfo = {};
    $scope.selected = {
        seats: [],
        seatNos: [],
        amount: 0,
        baseMarkups: 0,
        baseDiscount: 0,
        baseTax: 0,
        baseCharges: 0,
        total: 0
    };
    $scope.loaded = false;
    $scope.totX = 0;
    $scope.totY = 0;
    //mobile identifyer
    $scope.isMobile = Util.isMobile();


    //start loader
    $scope.$parent.seatsLoading = true;

    var seatPrice = $scope.bus.prices,
        busId = $scope.busSector.bus.id,
        resultIndex = $scope.bus.resultIndex,
        sector = $scope.busSector;

    var busroute = $scope.busSector.busRoute;
    var boardingLocations = busroute.boardingStops;
    var dropoffLocations = busroute.dropStops;

    // ajax request to api to get seat info for a vehicle
    $http.post('/app/search/seatinfo', {
        //bus: $scope.bus,
        boardingLocation: boardingLocations[0].id,
        dropoffLocation: dropoffLocations[0].id,
        busType: $scope.busSector['bus']['busTypeId'],
        scheduleId: $scope.busSector['scheduleId'],
        session: $rootScope.session
    })
        .success(function (data) {
            if (data.result) {
                var result = data.result;
                var seatSize = 40,
                    x = 0,
                    y = 0;

                //loop through and create widths
                for(var k in result.seats){
                    var seat = result.seats[k],
                        xSize = seat['x']*seatSize,
                        ySize = seat['y']*seatSize;

                    //if mobile
                    if(Util.isMobile()){
                        result.seats[k].style = 'right: '+ ySize +'px; top: '+ xSize +'px;';
                    }else{
                        result.seats[k].style = 'left: '+ xSize +'px; top: '+ ySize +'px;';
                    }

                    //get the maximum x and y to calculate total size
                    x = seat['x']>x ? seat['x'] : x;
                    y = seat['y']>y ? seat['y'] : y;

                }
                $scope.totX = (Util.isMobile() ? (y+1):(x+1)) * seatSize;
                $scope.totY = (Util.isMobile() ? (x+1):(y+1)) * seatSize;
                //divide seats into rows
                $scope.seatInfo = result.seats;

                //show input
                $scope.loaded = true;
            }else{
                $scope.Global.error.hasError = true;
                $scope.Global.error.message = data.error;
            }
            //stop loader
            $scope.$parent.seatsLoading = false;
        })
        .error(function(){
            //stop loader
            $scope.$parent.seatsLoading = false;
        });

    /**
     * Choose selected seat
     */
    $scope.chooseSeat = function(seat, $event){
        var $element = $($event.target).closest('div');
        //if seat is booked or not available, break
        if(seat['available']!==true || seat['booked']===true){
            return;
        }

        //check if seat exists in the array
        var arrayIndex = $scope.selected.seats.indexOf( seat['id'] );
        if(arrayIndex > -1){
            //disable errors
            $scope.Global.error.hasError = false;
            //remove element from array
            $scope.selected.seats.splice(arrayIndex, 1);
            //remove face value
            $scope.selected.seatNos.splice(arrayIndex, 1);
            //deduct price
            $scope.selected.amount          -= parseFloat(seatPrice.fare);
            $scope.selected.baseMarkups     -= parseFloat(seatPrice.baseMarkups);
            $scope.selected.baseDiscount    -= parseFloat(seatPrice.baseDiscount);
            $scope.selected.baseTax         -= parseFloat(seatPrice.baseTax);
            $scope.selected.baseCharges     -= parseFloat(seatPrice.baseCharges);
            $scope.selected.total           -= parseFloat(seatPrice.total);
            $element.removeClass('seat-selected');
        }else{

            //if selected is more than allowed
            if($scope.selected.seats.length > 8){
                $scope.Global.error.hasError = true;
                $scope.Global.error.message = translate.get("TooManySeatsSelected");
                return;
            }

            //add to the array
            $scope.selected.seats.push(seat['id']);
            $scope.selected.seatNos.push(seat['number']);

            $scope.selected.amount          += parseFloat(seatPrice.fare);
            $scope.selected.baseMarkups     += parseFloat(seatPrice.baseMarkups);
            $scope.selected.baseDiscount    += parseFloat(seatPrice.baseDiscount);
            $scope.selected.baseTax         += parseFloat(seatPrice.baseTax);
            $scope.selected.baseCharges     += parseFloat(seatPrice.baseCharges);
            $scope.selected.total           += parseFloat(seatPrice.total);
            $element.addClass('seat-selected');
        }
        //save bus id
        $scope.selected.busId = busId;
    };

    /**
     * Forward to booking view
     */
    $scope.continueBooking = function(){
        if($scope.selected.seats.length <1){
            $scope.Global.error.hasError = true;
            $scope.Global.error.message = translate.get("Please select at least one seat.");
        }else{
            //disable errors
            $scope.Global.error.hasError = false;
			
			if(sector.bus.id==28){
				$window.location.href = 'http://www.google.com';
			}
			
            //broadcast event so booking screen can get data
            $rootScope.$broadcast('showBooking', {
                selected: $scope.selected,
                from: $stateParams.from,
                to: $stateParams.to,
                resultIndex: resultIndex,
                journeyData : {
                    fromName: sector.fromCityName,
                    toName: sector.toCityName,
                    busPlate: sector.bus.plateNumber,
                    busName: sector.bus.name,
                    departure: sector.departureTime,
                    arrival: sector.arrivalTime,
					travelClass  : sector.bus.travelClassName
                },
                boardingLocations: boardingLocations,
                dropoffLocations: dropoffLocations
            });
            //set current loading screen
            $rootScope.currentScreen = 'booking';

            //change the url
            $state.go('search-result', {
                fromName: $stateParams.fromName,
                toName: $stateParams.toName,
                from: $stateParams.from,
                to: $stateParams.to,
                date: ($stateParams.date || Util.getSearchDefaultDate()),
                offerCode: ($stateParams.offerCode || ''),
                booking: (new Date()).getTime().toString()
            }, {notify: false});
            //$location.path('/bus/placebooking/');
        }
    };

}]);