/**
 * Created by udantha on 6/9/14.
 */
expressFrontApp.controller('booking', ['$scope', '$http', '$location', '$rootScope', 'translate', '$state',
    function ($scope, $http, $location, $rootScope, translate, $state) {
        /**
         * Variable Definitions
         */
        $scope.bookingData = {
            passenger: [],
            contact: {},
            boardingLocation: null,
            dropoffLocation: null
        };
        $scope.errors = {};
        $scope.selected = false;
        $scope.journeyData = {};
        $scope.IPG = {
            postUrl: '',
            Data: []
        };
        $scope.boardingLocations = [];
        $scope.dropoffLocations = [];
        $scope.mySeatCheck = null;

        //assign route variables
        var resultIndex = null,
            from = null,
            to = null;

        /**
         * Load on select
         */
        $scope.$on('showBooking', function(event, args) {
			$("body").animate({scrollTop: 0}, "fast");

            $scope.bookingData.passenger = [];
            $scope.bookingData.contact = {};
            //selected seats
            $scope.selected = args.selected;
            resultIndex = args.resultIndex;
            from = args.from;
            to = args.to;
            $scope.journeyData = args.journeyData;
            //set duration
            $scope.jDuration = Util.getTimeDifferenceBetween($scope.journeyData.departure, $scope.journeyData.arrival);

            $scope.boardingLocations = args.boardingLocations;
            $scope.bookingData.boardingLocation = $scope.boardingLocations[0];
            $scope.dropoffLocations = args.dropoffLocations;
            $scope.bookingData.dropoffLocation = $scope.dropoffLocations[0];
        });

        /**
         * Tabs
         * @type {*[]}
         */
        $scope.tabs = [
         {
            title: translate.get('Credit/Debit Cards'),
            url: 'tab-credit',
            type: 'credit_card',
            active: true
         } ,
		 {
            title: translate.get('PayHere'),
            url: 'tab-payhere',
            type: 'payhere',
            active: false
         },
			//, {
                // title: translate.get('PayPal'),
                // url: 'tab-paypal',
                // type: 'paypal',
                // active: false
            // },
            // {
            // title: translate.get('Dialog eZcash'),
            // url: 'tab-ez',
            // type: 'ez_cash',
            // active: false
        // },
        //     {
        //     title: translate.get('Mobitel mCash'),
        //     url: 'tab-mcash',
        //     type: 'm_cash',
        //     active: false
        // },
            // {
            // title: translate.get('Etisalat eZcash'),
            // url: 'tab-etisalat',
            // type: 'ez_cash',
                // active: false
        // }, {
            // title: translate.get('Hutch eZcash'),
            // url: 'tab-hutch',
            // type: 'ez_cash',
                // active: false
        // }
        //    , {
        //    title: 'Other Payment Methods',
        //    url: 'tab-other',
        //    type: 'other_methods'
        //}
        ];

		// Exclude NDB card offer for superline website.
		
		// if($location.host().match("superline")==null){
			// ndb_promo_tab = {
								// title: translate.get('NDB Credit Cards - 10 % Off'),
								// url: 'tab-credit-ndb-promo',
								// type: 'ndb_card',
								// active: false
							// };
			// $scope.tabs.push(ndb_promo_tab);
		// }

        $scope.bookingData.type = $scope.tabs[0].type;

        $scope.onClickTab = function (tab) {
            $scope.bookingData.type = tab.type;
        };

        /**
         * Save booking details
         */
        $scope.placeBooking = function() {

            //reset errors
            $scope.errors = {
                passenger: false,
                contactData: false
            };

            //validate
            if(!$scope.bookingData.passenger || $scope.bookingData.passenger.length<1){
                $scope.errors.passenger = true;
                return;
            }

            if($.trim($scope.bookingData.contact.mobileNo)==='' && $.trim($scope.bookingData.contact.nic)==''){
                $scope.errors.contactData = true;
                return;
            }

            //check if bus exists
            if(!$scope.selected){
                $scope.Global.error.hasError = true;
                $scope.Global.error.message = translate.get('Unknown error occurred. Please refresh the web page.');
                return;
            }

            //if(confirm('Save Booking?')){
                //start loader
                $scope.Global.loading = true;

                var bookingData = $scope.bookingData,
                    session = $rootScope.session;
                //send request to save booking
                $http.post('/app/search/placebooking', {
                    fromCity: from,
                    toCity: to,
                    resultIndex: resultIndex,
                    bookingData: bookingData,
                    session: session
                })
                    .success(function(data){
                        if(!data.error){
                            $scope.IPG = {
                                postUrl: '',
                                Data: []
                            };
                            /**
                             * Check for IPG type and redirect to relevant IPG
                             */
                            //if dialog ezcash
                            if(data.IPGtype=='submitForm'){
                                var form = data['IPGForm'];
                                //post to IPG
                                $(form)
                                    .appendTo('body')
                                    .submit();
                            }else if(data.IPGtype=='iframeUrl'){
                                //set current loading screen
                                $rootScope.currentScreen = 'bookingPay';
                                $rootScope.redirectPayUrl = data['IPGForm'];
                                //redirect
                                $state.go('booking-iframe', {});
                                // $location.path('bus/placebooking/');
                            }
                            else{
                                $scope.Global.loading = false;
                                $scope.Global.error.hasError = true;
                                $scope.Global.error.message = translate.get('Error selecting payment option.');
                            }
                            //go to last screen
                            //$rootScope.currentScreen = 'result';
                        }else{
                            $scope.Global.loading = false;
                            $scope.Global.error.hasError = true;
                            $scope.Global.error.message = data.error;
                        }
                    })
                    .error(function(){
                        $scope.Global.loading = false;
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = translate.get('Unknown error occurred. Please try again.');
                    });

            //}
        };

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
        //set 0 as default
        $scope.mySeatCheck = 0;
}]);