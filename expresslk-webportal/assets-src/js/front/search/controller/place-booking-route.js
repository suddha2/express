/**
 * Created by udantha on 1/29/15.
 */
expressFrontApp.controller('placeBookingRoute', ['$scope', '$window', '$rootScope', '$sce','$timeout',
    function ($scope, $window, $rootScope, $sce,$timeout) {
        //if not current screen redirect backwards
        if($rootScope.currentScreen !== 'bookingPay'){
            //redirect back to previous url
            $window.history.back();
        }

        var redirectUrl = $rootScope.redirectPayUrl;

        /**
         * Variable initialization
         */
        $scope.iframeUrl = false;

        /**
         * Load on redirect to pay event
         */
        //set as a trusted url
        $scope.iframeUrl = $sce.trustAsResourceUrl(redirectUrl);
        //set preloader to off state
        $scope.Global.loading = false;
		
		// Wait time is set to 10 mins
		$scope.waitTime=600000;
		
		// Redirect after 10 Minutes.
		$scope.timeoutPromise = $timeout(redirect,$scope.waitTime);
		
		
		// $scope.alertMe = function(){
            // alert("Sorry your payment has timedout!. You will be redirected to previous page.");
			// $window.history.back();
		// };
		
		function redirect(){
			alert("Your transaction is not successful as we didn't receive any payment for the reservation during the allocated period of time. You will be redirected to the initial step. Please try again.");
			$window.history.back();
		}
		
    }]);