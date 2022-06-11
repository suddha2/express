/**
 * Created by udantha on 6/9/14.
 */
expressAdminApp.controller('ticketboxSeatBooking', ['$scope', '$rootScope', '$window', 'busService',
    'payHelperService', '$interval', '$expConfirm',
    function ($scope, $rootScope, $window, busService, payHelperService, $interval, $expConfirm) {
        /**
         * Variable Definitions
         */
        $scope.bookingData = {
            passenger: [],
            contact: {},
            boardingLocation: null,
            dropoffLocation: null,
            remarks: '',
            alertCustomer: true,
			warrantPassRef:null,
			warrantPass:null,
			payment:{}
        };
        $scope.errors = {};
        $scope.options = {
            //dummy: [], customer: []
        };
        $scope.today = new Date();
        $scope.passengerAsContact = false;
        $scope.printReceipt = true;
        $scope.boardingLocations = [];
        $scope.dropoffLocations = [];
        $scope.genderRequired = false;
		$scope.personRequired = true;
        $scope.agentData = [];
        $scope.mySeatCheck = 0;
		$scope.hasWarrant = false;
		$scope.hasPass = false;
		
        /**
         * Tabs
         * @type {*[]}
         */
        $scope.tabs = [];

        var addBBKTab = function (tabData) {
            //set vendor false since this is bbk payment type
            $scope.tabs.push({
                title: tabData.title + ' (BBK)',
                url: tabData.url,
                type: tabData.type,
                vendor: false,
                active: false
            });
        };

        var tabCash = {
            title: 'Cash',
            url: 'tab-cash',
            type: 'Cash',
            vendor: true,
            active: false
        };
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodCash)) {
            $scope.tabs.push(tabCash);
        }
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodCashBBK)) {
            addBBKTab(tabCash);
        }
        //ezcash
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodEzCash)) {
            $scope.tabs.push({
                title: 'eZ Cash',
                url: 'tab-ezCash',
                type: 'ezCash',
                vendor: true,
                active: false
            });
        }
        //ezcash ref
        var tabezCashRef = {
            title: 'eZ Cash Ref',
            url: 'tab-ezCash-ref',
            type: 'ezCashRef',
            vendor: true,
            active: false
        };
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodEzCashRef)) {
            $scope.tabs.push(tabezCashRef);
        }
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodEzCashBBK)) {
            addBBKTab(tabezCashRef);
        }

        if(ACL.fragmentCanExecute(Fragment.Name.payMethodMCash)) {
            $scope.tabs.push({
                title: 'mCash',
                url: 'tab-mCash',
                type: 'mCash',
                vendor: true,
                active: false
            });
        }

        //agent payment
        var tabAgent = {
            title: 'Agents',
            url: 'tab-agents',
            type: 'agents',
            vendor: true,
            active: false
        };
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodAgent)) {
            $scope.tabs.push(tabAgent);
        }
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodAgentBBK)) {
            addBBKTab(tabAgent);
        }

        if(ACL.fragmentCanExecute(Fragment.Name.payMethodPayAtBus)) {
            $scope.tabs.push({
                title: 'Pay at Bus',
                url: 'tab-atBus',
                type: 'AtBus',
                vendor: true,
                active: false
            });
        }
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodPayLater)) {
            $scope.tabs.push({
                title: 'Pay Later',
                url: 'tab-diifpay',
                type: 'diifPay',
                vendor: true,
                active: false
            });
        }

        //credit card
        var tabCC = {
            title: 'Credit Card',
            url: 'tab-credit',
            type: 'Card',
            vendor: true,
            active: false
        };
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodCard)) {
            $scope.tabs.push(tabCC);
        }
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodCardBBK)) {
            addBBKTab(tabCC);
        }

        //bank transfer
        var tabBT = {
            title: 'Bank Transfer',
            url: 'tab-banktransfer',
            type: 'BankTr',
            vendor: true,
            active: false
        };
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodBankTransfer)) {
            $scope.tabs.push(tabBT);
        }
        if(ACL.fragmentCanExecute(Fragment.Name.payMethodBankTransferBBK)) {
            addBBKTab(tabBT);
        }

        $scope.onClickTab = function (tab) {
            $scope.bookingData.type = tab.type;
            $scope.bookingData.vendor = tab.hasOwnProperty('vendor')? tab.vendor : true;
            //set active tab
            for(var i in $scope.tabs){
                //if the same, set true
                if($scope.tabs[i] === tab){
                    $scope.tabs[i].active = true;
                }else{
                    $scope.tabs[i].active = false;
                }
            }
        };

        //select first tab
        $scope.onClickTab($scope.tabs[0]);

        //load agents
        payHelperService.getAgents()
            .then(function(data) {
                $scope.agentData = data;
            });
		
		
		//Watch change in mobile number entnry for contact person and enable/disable  sms sending option.
		// $scope.$watch('bookingData.contact.mobileNo', function (newVal, oldVal) {
			// console.log("Current Value for mobile no : "+$scope.bookingData.contact.mobileNo);
			// if($scope.bookingData.contact.mobileNo.length==10){
				// console.log(" is valid mobile number ");
				// $scope.bookingData.payment.alertCashCustomer=true;
			// }else{
				// $scope.bookingData.payment.alertCashCustomer=false;
			// }
			// //$scope.bookingData.payment.alertCashCustomer=true;
			
        // });
		
		//Watch to make sure only one of Warrant / Pass is selected per booking.
		$scope.$watch('hasWarrant', function (newVal, oldVal) {
			$scope.bookingData.warrantPass='';
			$scope.bookingData.warrantPassRef='';
			if (newVal) {
				// Uncheck Pass option
				$scope.hasPass=false;
				// Set Warrant/Pass
				$scope.bookingData.warrantPass='warrant';
			}
        });
		
		
		
		//Watch to make sure only one of Warrant / Pass is selected per booking.
		$scope.$watch('hasPass', function (newVal, oldVal) {
			$scope.bookingData.warrantPass='';
			$scope.bookingData.warrantPassRef='';
			if (newVal) {
				// Uncheck warrant option
				$scope.hasWarrant=false;
				// Set Warrant/Pass
				$scope.bookingData.warrantPass='pass';
			}
        });
		// Watch if a warrant / pass reference number is entered and broadcast event for 
		// ticketboxSeatSelection (bus-seatselection.js) controller to compute totals for warrants/passes included booking.
		$scope.$watch('bookingData.warrantPassRef', function (newVal, oldVal) {
			if (newVal) {
				$scope.$emit('bookingHasWarrantsOrPasses', true);
			}else{
				$scope.$emit('bookingHasWarrantsOrPasses', false);
			}
        });
			
        /**
         * Watch for seat array changes and update booking array relavantly
         */
        $scope.$watchCollection('selected.seatNos', function (newVal, oldVal) {
			if (newVal) {
                if ($scope.bookingData.passenger) {
                    var passengerData = $scope.bookingData.passenger;
                    for (var i in passengerData) {
                        //check if seat number exists
                        if ($.inArray(passengerData[i].seatNo, $scope.selected.seatNos) < 0) {
                            //remove seat row
                            $scope.bookingData.passenger = jQuery.grep($scope.bookingData.passenger, function (value) {
                                return value.seatNo != passengerData[i].seatNo;
                            });
                        }
                    }
                }
            }
        });

        var openPrintWindow = function (data) {
            var width = 960,
                height = 500;
            $window.open('/admin-panel/ticketbox/printticket?data=' + data,
                "Ticket",
                "status=no,width=" + width + ",height=" + height + ",top=" + ((screen.height / 2) - (height / 2)) + ",left=" + ((screen.width / 2) - (width / 2)) + ",toolbar=no,menubar=no,scrollbars=no,location=no");

            return $window;
        };

        /**
         * Handle IPG window
         */
        var timeoutHandlerPay;
        var openPayWindow = function(form, reference, callback) {
            var w = angular.element($window),
                width = w.width() - (w.width()/8),
                height = w.height() - (w.height()/8);
            var win = $window.open('about:blank',
                "Ticket",
                "status=no,width=" + width + ",height=" + height + ",top=" + ((w.height() / 2) - (height / 2)) + ",left=" + ((w.width() / 2) - (width / 2)) + ",toolbar=no,menubar=no,scrollbars=yes,location=no");
            win.document.write('<html><head></head><body style="background: url(/img/preloader/Preloader_9.gif) no-repeat center; padding: 50px;">'+ form +'</body></html>');
            win.document.forms[0].submit();

            /**
             * Release held items
             */
            clearPayTimer();
            timeoutHandlerPay = $interval(function(){
                //check if closed
                if(win.closed){
                    //clear close timeout
                    clearPayTimer();
                    //confirm cancellation
                    //call callback
                    callback.apply(this, [false]);

                    //if(confirm('Cancel the booking? ("Ok" only if the booking failed)')){
                    //    releaseSeats(reference, callback);
                    //}else{
                    //    //call callback
                    //    callback.apply(this, [false]);
                    //}
                }
            }, 500);

            //attach callback
            win.ipgResponse = function(payStatus) {
                //clear close timeout
                clearPayTimer();
                //call callback
                callback.apply(this, [payStatus]);
                //close popup
                win.close();
            };

            return win;
        };
        var clearPayTimer = function() {
            if (angular.isDefined(timeoutHandlerPay)) {
                $interval.cancel(timeoutHandlerPay);
                timeoutHandlerPay = undefined;
            }
        };
        var releaseSeats = function(reference, callback) {
            busService.releaseHeldBooking(reference)
                .then(function (data) {
                    if (!data.error) {
                        //call callback
                        callback.apply(this, [false]);
                    } else {
                        $scope.Global.loading = false;
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = data.error;
                    }
                }, function (error) {
                    $scope.Global.loading = false;
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = error;
                });
        };
        $scope.$on('$destroy', function() {
            // Make sure that the interval is destroyed too
            clearPayTimer();
        });

        //reset on shoSeats event
        $scope.$on('showSeats', function (event, args) {
            $scope.bookingData.passenger = [];
            $scope.bookingData.contact = {};
            $scope.options = {};
            $scope.onClickTab($scope.tabs[0]);

            $scope.boardingLocations = args.boardingLocations;
            $scope.bookingData.boardingLocation = $scope.boardingLocations[0];
            $scope.dropoffLocations = args.dropoffLocations;
            $scope.bookingData.dropoffLocation = $scope.dropoffLocations[0];
            $scope.genderRequired = args.genderRequired;
			$scope.personRequired = args.personRequired;
        });

        /**
         * Save booking details
         * @type {boolean}
         */
        $scope.placeBooking = function (event) {
            //event.preventDefault();
            //reset errors
            $scope.errors = {
                passenger: false,
                contactData: false
            };

            //validate
            if (!$scope.bookingData.passenger || $scope.bookingData.passenger.length < 1) {
                $scope.errors.passenger = true;
                return;
            }

            if ($.trim($scope.bookingData.contact.mobileNo) === '' && $.trim($scope.bookingData.contact.nic) == '' && $.trim($scope.bookingData.contact.name) == '') {
                $scope.errors.contactData = true;
                return;
            }

            //get selected bus infor from the bus service
            var currentBus = busService.getSelectedBus();

            //check if bus exists
            if (!currentBus) {
                $scope.Global.error.hasError = true;
                $scope.Global.error.message = 'Unknown error occurred. Please refresh the web page.';
                return;
            }

            $expConfirm({
                text: 'Click "OK" to save.',
                title: 'Save the booking?'
            }).then(function() {
                //start loader
                $scope.Global.loading = true;

                var fromCity = currentBus.sectors[0].fromCityId,
                    toCity = currentBus.sectors[0].toCityId,
                    resultIndex = currentBus.resultIndex,
                    bookingData = $scope.bookingData,
                    session = $rootScope.session;

                //send request to save booking
                busService.confirmBooking(fromCity, toCity, resultIndex, bookingData, session)
                    .then(function (data) {
                        if (!data.error) {

                            //set IPG type
                            if(data.IPGtype == 'submitForm'){
                                var form = data['IPGForm'],
                                    reference = data['reference'];
                                //put form in a window and submit it
                                openPayWindow(form, reference, function(){
                                    $scope.Global.loading = false;
                                    //reset variables
                                    resetForm();
                                });
                            }else{
								//ask to print tickets
								if ($scope.printReceipt) {
									var win = openPrintWindow(data.ticketData);
								}
                                //set success message
                                $scope.Global.loading = false;
                                $scope.Global.success.isSuccess = true;
                                $scope.Global.success.message = "Seats booked successfully.";
                                //reset variables
                                resetForm();
                            }

                        } else {
                            $scope.Global.loading = false;
                            $scope.Global.error.hasError = true;
                            $scope.Global.error.message = data.error;
                        }
                    }, function (error) {
                        $scope.Global.loading = false;
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = error;
                    });
            });
        };

        //reset function
        function resetForm() {
            //reset variables
            $scope.bookingData = {
                passenger: [],
                contact: {},
                boardingLocation: $scope.boardingLocations[0],
                dropoffLocation: $scope.dropoffLocations[0],
                remarks: '',
                alertCustomer: true,
				warrantPassRef:null,
				warrantPass:null
            };
            $scope.mySeatCheck = 0;
            $scope.options = {};
            $scope.errors = {};
            $scope.onClickTab($scope.tabs[0]);
            $scope.passengerAsContact = false;
            //set form to pristene
            $scope.booking_form.$setPristine();
			$scope.hasWarrant = false;
			$scope.hasPass = false;
            //broadcast message to reload seats
            $rootScope.$broadcast('reloadSeats');
        }

        /**
         * ============= My seat checkbox =========
         */
        //track my seat checkbox
        var fnCopyContactToPassenger = function(ind){
            if(!$scope.bookingData.passenger[ind]){
                return;
            }
            $scope.bookingData.passenger[ind].nic = $scope.bookingData.contact.nic;
            $scope.bookingData.passenger[ind].name = $scope.bookingData.contact.name;
            $scope.bookingData.passenger[ind].mobileNo = $scope.bookingData.contact.mobileNo;
        };

        $scope.$watchCollection('bookingData.contact', function(newVal, oldVal){
            if($scope.mySeatCheck!==null){
                //newVal is the index of the passenger booking data. Copy over the customer data
                fnCopyContactToPassenger($scope.mySeatCheck);
            }
        });
        //clear text boxes if my seat is deseleted
        $scope.$watch('mySeatCheck', function(newVal, oldVal){
            if(newVal!==oldVal && oldVal!==null){
                var ind = oldVal;
                //clear passenger data if exists
                if($scope.bookingData.passenger[ind]){
                    $scope.bookingData.passenger[ind].nic = '';
                    $scope.bookingData.passenger[ind].name = '';
                    $scope.bookingData.passenger[ind].mobileNo = '';
                }
            }
            //copy over contact data on a new check box select
            if(newVal!==null){
                fnCopyContactToPassenger(newVal);
            }
		});
		
    }]);