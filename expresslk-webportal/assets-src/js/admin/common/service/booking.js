/**
 * Created by udantha on 2/8/16.
 */
expressAdminApp.service(
    "bookingService", ['$http', '$q', function ($http, $q) {
        // ---
        // PUBLIC METHODS.
        // ---
        /**
         * get bus details
         * @param idBus
         * @returns {ng.IPromise<TResult>}
         */
        function resedSms(reference) {
            var request = $http.post('/admin-panel/ticketbox-ajax/resendsms', {reference: reference});
            return( request.then(handleSuccess, handleError) );
        }

        /**
         *
         * @param reference
         * @returns {ng.IPromise<TResult>|*}
         */
        function resedEmail(reference) {
            var request = $http.post('/admin-panel/ticketbox-ajax/resendemail', {reference: reference});
            return( request.then(handleSuccess, handleError) );
        }

        // ---
        // PRIVATE METHODS.
        // ---
        function handleError(response) {
            //normalize server response in case of page error
            if (!angular.isObject(response.data) || !response.data.error) {
                return( $q.reject("An unknown error occurred.") );
            }

            // Otherwise, use expected error message.
            return( $q.reject(response.data.error) );

        }

        function handleSuccess(response) {
            return( response.data );
        }

        // Return public API.
        return({
            resedSms: resedSms,
            resedEmail: resedEmail
        });
    }]
);