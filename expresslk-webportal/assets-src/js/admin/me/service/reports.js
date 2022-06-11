/**
 * Created by udantha on 12/17/16.
 */
expressAdminApp.service(
    "meReportService", ['$http', '$q', function ($http, $q) {
        var _bus = false;
        // ---
        // PUBLIC METHODS.
        // ---
        /**
         * get ticket schedule
         * @param from
         * @param to
         * @param date
         * @param page
         * @returns {*}
         */
        function _getCollection(from, to) {
            var request = $http.post('/admin-panel/me/collectionreport', {
                from: from,
                to: to
            });
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
            //check if there is error
            if(response.data.hasOwnProperty('error')){

            }
            return( response.data );
        }

        // Return public API.
        return({
            getCollection: _getCollection
        });
    }]
);