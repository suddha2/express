/**
 * Created by udantha on 2/13/15.
 */
expressFrontApp.service(
    "cityDestinationService", ['$http', '$q', 'INJECTED_CITY_LIST',
        function ($http, $q, INJECTED_CITY_LIST) {
        // ---
        // PUBLIC METHODS.
        // ---
        /**
         * get destinations for city
         * @param cityId
         * @returns {ng.IPromise<TResult>|*}
         */
        function getDestinationForCity(cityId) {
            var request = $http.post('/app/search/getdestinations', {
                cityId: cityId
            });
            return( request.then(handleSuccess, handleError) );
        }

            function getCityList(){
                return $q.when(INJECTED_CITY_LIST);
            }

        // ---
        // PRIVATE METHODS.
        // ---
        function handleError(response) {
            //normalize server response in case of page error
            if (!angular.isObject(response.data) || !response.data.message) {
                return( $q.reject("An unknown error occurred.") );
            }

            // Otherwise, use expected error message.
            return( $q.reject(response.data.message) );

        }

        function handleSuccess(response) {
            //check if there is error
            if(response.data.hasOwnProperty('error')){

            }
            return( response.data );
        }

        // Return public API.
        return({
            getDestinationForCity: getDestinationForCity,
            getCityList: getCityList
        });
    }]
);