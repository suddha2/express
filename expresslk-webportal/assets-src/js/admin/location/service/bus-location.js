/**
 * Created by udantha on 7/2/16.
 */
expressAdminApp.service(
    "busLocationService", ['$http', '$q', function ($http, $q) {
        var _bus = false;
        // ---
        // PUBLIC METHODS.
        // ---
        /**
         *
         * @returns {*}
         * @private
         */
        function _getBusList() {
            var request = $http.post('/admin-panel/bus/loadlocationbuslist', {});
            return( request.then(handleSuccess, handleError) );
        }

        function _getBusLocations(_busId, _fromDate, _toDate) {
            var request = $http.post('/admin-panel/bus/getlocations', {
                busId: _busId,
                fromDate: _fromDate,
                toDate: _toDate
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
            getBusList: _getBusList,
            getBusLocations: _getBusLocations
        });
    }]
);