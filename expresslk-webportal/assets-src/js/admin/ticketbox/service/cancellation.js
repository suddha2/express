/**
 * Created by udantha on 2/13/15.
 */
expressAdminApp.service(
    "cancellationService", ['$http', '$q',
        function ($http, $q) {
            // ---
            // PUBLIC METHODS.
            // ---
            /**
             *
             * @param bookingRef
             * @returns {*}
             */
            function getBookingDetails(bookingRef) {
                var request = $http.post('/admin-panel/ticketbox/cancelbooking', {
                    ref: bookingRef,
                    mode: 'getBooking'
                });
                return ( request.then(handleSuccess, handleError) );
            }

            /**
             *
             * @param bookingRef
             * @returns {*}
             */
            function cancelBooking(bookingRef, cancellData) {
                var request = $http.post('/admin-panel/ticketbox/cancelbooking', {
                    ref: bookingRef,
                    data: cancellData,
                    mode: 'cancelBooking'
                });
                return ( request.then(handleSuccess, handleError) );
            }

            // ---
            // PRIVATE METHODS.
            // ---
            function handleError(response) {
                //normalize server response in case of page error
                if (!angular.isObject(response.data) || !response.data.message) {
                    return ( $q.reject("An unknown error occurred.") );
                }

                // Otherwise, use expected error message.
                return ( $q.reject(response.data.message) );

            }

            function handleSuccess(response) {
                //check if there is error
                if (response.data.hasOwnProperty('error')) {

                }
                return ( response.data );
            }

            // Return public API.
            return ({
                getBookingDetails: getBookingDetails,
                cancelBooking: cancelBooking
            });
        }]
);