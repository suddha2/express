/**
 * Service to handle buses services
 */
expressAdminApp.service(
    "busesService", ['$http', '$q', function ($http, $q) {
        // ---
        // PUBLIC METHODS.
        // ---
        /**
         * get bus details
         * @param idBus
         * @returns {ng.IPromise<TResult>}
         */
        function getBus(idBus) {
            var request = $http.post('/admin-panel/bus-service/bus', {
                idBus: idBus
            });
            return( request.then(handleSuccess, handleError) );
        }

        // ---
        // PRIVATE METHODS.
        // ---
        function handleError(response) {
            //normalize server response in case of page error
            if (!angular.isObject(response.data) || !response.data.error) {
                return( $q.reject("An unknown error occurred. Please try again") );
            }

            // Otherwise, use expected error message.
            return( $q.reject(response.data.error) );

        }

        function handleSuccess(response) {
            return( response.data );
        }

        // Return public API.
        return({
            getBus: getBus
        });
    }]
);