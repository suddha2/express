/**
 * Created by udantha on 9/20/14.
 */
expressAdminApp.controller('ticketboxSeatSelection', ['$scope', '$location',
    '$rootScope', '$stateParams', 'busService', '$anchorScroll', 'payHelperService',
    function ($scope, $location,
              $rootScope, $stateParams, busService, $anchorScroll, payHelperService) {
        //initialise variables
        var init = function(){
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
                total: 0,
                busId: 0,
				seatPassengerType :[]
            };
            $scope.journeyData = {};
            $scope.loaded = false;
            $scope.seatPrice = false;
            $scope.totX = 0;
            $scope.totY = 0;
            $scope.gridY = [];
        };
        $scope.agentInfo = [];
        $scope.availableAgents = [];
        $scope.selectedBookingRef = null;
		$scope.hasWarrantOrPass = false;
		$scope.warrantPassDeductAmount = 0;
        //broadcasted bus information
        var busInfo = {};

        init();
		
		
		// Watch event bookingHasWarrantsOrPasses emitted by ticketboxSeatBooking controller 
		// and compute totals for warrants/passes included booking.
		$scope.$on('bookingHasWarrantsOrPasses', function(event, args) {
				$scope.hasWarrantOrPass = args;
				$scope.computeTicketTotal();
			}
		);
		
        //load agents
        payHelperService.getAgents()
            .then(function(data) {
                //change ids
                for(var i in data){
                    $scope.agentInfo[data[i].id] = data[i];
                }
            });
	
        var displaySeats = function(boardingLocation, dropoffLocation, busType, scheduleId, scroll){

            //start loader
            $scope.Global.loading = true;
            // ajax request to api to get seat info for a vehicle
            busService.getSeatInfo(
                    boardingLocation,
                    dropoffLocation,
                    busType,
                    scheduleId,
                    $rootScope.session
                ).then(function (data) {
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

                            //get agent of the seat
                            var agentStyle = '';
                            if(seat.agentDefault && $scope.agentInfo[seat.agentDefault]){
                                var agentDt = $scope.agentInfo[seat.agentDefault];
                                agentStyle = 'outline-color: '+ agentDt['color'] +';';
                                //save agent for show in legend
                                $scope.availableAgents[agentDt.id] = agentDt;
                            }

                            result.seats[k].style = 'right: '+ ySize +'px; top: '+ xSize +'px; ';
                            result.seats[k].agent_style = agentStyle;

                            //get the maximum x and y to calculate total size
                            x = seat['x']>x ? seat['x'] : x;
                            y = seat['y']>y ? seat['y'] : y;

                            if (seat.dummy) { // ignore dummy bookings in ticket box
                                result.seats[k].booked = false;
                                delete result.seats[k].dummy;
                            }

                            //add to y grid
                            var yGridVal = seat['x']*seatSize;
                            //add value if doesn't already exists
                            if($.inArray(yGridVal, $scope.gridY)===-1){
                                $scope.gridY.push(yGridVal);
                            }
                        }
                        $scope.totX = (y + 1) * seatSize;
                        $scope.totY = (x + 1) * seatSize;
                        //divide seats into rows
                        $scope.seatInfo = result.seats;
                        //sort grid
                        $scope.gridY.sort(function(a, b){
                            return a - b;
                        });

                        //show input
                        $scope.loaded = true;
						console.log("scroll is set to "+scroll);
                        if (scroll) {
							$location.hash('seat-selection');
							$anchorScroll();
                        }else {
							$location.hash('page-top');
							$anchorScroll();
						}
                    }else{
                        //show error
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = data.error;
                    }
                    //stop loader
                    $scope.Global.loading = false;
                }, function(error){
                    //stop loader
                    $scope.Global.loading = false;
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = error;
                });
        };
		
		$scope.setPassengerType = function(obj,type){
			var seatNumber = $scope.selected.seatNos[obj];
			// set Passenger type locally to perform calculation.			
			for (i = 0; i < $scope.selected.seatPassengerType.length; i++) { 
				if ($scope.selected.seatPassengerType[i].seat==seatNumber){
					$scope.selected.seatPassengerType[i].type=type;
					break;
				}
			}
			$scope.computeTicketTotal();
		};
		
		
		
		$scope.computeTicketTotal = function (){
			// Reset current fields.
			$scope.selected.amount = 0;
			$scope.selected.baseMarkups = 0;
			$scope.selected.baseDiscount = 0;
			$scope.selected.baseTax = 0;
			$scope.selected.baseCharges = 0;
			$scope.warrantPassDeductAmount =0;
			$scope.selected.total = 0;
			
			// Compute total for all selected seats.
			for (i = 0; i < $scope.selected.seatPassengerType.length; i++) { 
				$scope.selected.baseMarkups     += parseFloat($scope.seatPrice.baseMarkups);
                $scope.selected.baseDiscount    += parseFloat($scope.seatPrice.baseDiscount);
                $scope.selected.baseTax         += parseFloat($scope.seatPrice.baseTax);
                $scope.selected.baseCharges     += parseFloat($scope.seatPrice.baseCharges);
				if (($scope.selected.seatPassengerType[i].type==='Child')){
					$scope.selected.amount      += parseFloat($scope.seatPrice.childFare);
					$scope.selected.total       += parseFloat($scope.seatPrice.childFare)+parseFloat($scope.seatPrice.baseMarkups)+
													parseFloat($scope.seatPrice.baseDiscount)+
													parseFloat($scope.seatPrice.baseTax)+parseFloat($scope.seatPrice.baseCharges);
				}else{
					$scope.selected.amount      += parseFloat($scope.seatPrice.fare);
					$scope.selected.total       += parseFloat($scope.seatPrice.fare)+parseFloat($scope.seatPrice.baseMarkups)+
													parseFloat($scope.seatPrice.baseDiscount)+
													parseFloat($scope.seatPrice.baseTax)+parseFloat($scope.seatPrice.baseCharges);	
				}
			}
			
			// If Booking has warrants or pass we set off total ticket fare ( $scope.selected.amount) 
			// and only booking fee is collected.
			if($scope.hasWarrantOrPass){
				$scope.warrantPassDeductAmount = $scope.selected.amount;
				$scope.selected.total -= $scope.warrantPassDeductAmount;
			}
		};
		
        /**
         * Choose selected seat
         */
        $scope.chooseSeat = function(seat, $event){
            var $element = $($event.target).closest('div');
            //if seat is booked or not available, break
            if(seat['available']!==true || seat['booked']===true){
                //highlight same seats
                $scope.selectedBookingRef = seat.booking_ref;
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
				$scope.selected.seatPassengerType.splice(arrayIndex, 1);
				$scope.computeTicketTotal();
                $element.removeClass('seat-selected');
            }else{

                //if selected is more than allowed
                if($scope.selected.seats.length > 8){
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = "You are not allowed to book so many seats :-)";
                    return;
                }

                //add to the array
                $scope.selected.seats.push(seat['id']);
                $scope.selected.seatNos.push(seat['number']);
				$scope.selected.seatPassengerType.push({seat:seat['number'], type:'A'});
				$scope.computeTicketTotal();
                $element.addClass('seat-selected');
            }
			
        };

        //load seats on shoSeats event
        $scope.$on('showSeats', function(event, args) {
			//save bus info
            busInfo = args;
            //reset all the variables
            init();
            //load seats
            displaySeats.apply(this, [
                args.boardingLocations[0].id,
                args.dropoffLocations[0].id,
                args.busType,
                args.scheduleId,
                true
            ]);
            //save bus id
            $scope.selected.busId = args.busId;
            $scope.seatPrice = args.seatPrice;
            $scope.journeyData = args.journeyData;
        });

        //reload seats of the bus
        $scope.$on('reloadSeats', function(){
			//reset all the variables
            init();
            //load seats
            displaySeats.apply(this, [
                busInfo.boardingLocations[0].id,
                busInfo.dropoffLocations[0].id,
                busInfo.busType,
                busInfo.scheduleId,
                false
            ]);
            //save bus id
            $scope.selected.busId = busInfo.busId;
            $scope.seatPrice = busInfo.seatPrice;
            $scope.journeyData = busInfo.journeyData;
        });

    }]);