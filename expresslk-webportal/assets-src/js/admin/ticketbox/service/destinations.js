/**
 * Created by udantha on 2/13/15.
 */
expressAdminApp.service(
    "cityDestinationService", ['$http', '$q', '$injector', '$timeout',
        function ($http, $q, $injector, $timeout) {
            // ---
            // PUBLIC METHODS.
            // ---
            /**
             * get destinations for city
             * @param cityId
             * @returns {ng.IPromise<TResult>|*}
             */
            function getDestinationForCity(cityId) {
                var request = $http.post('/admin-panel/ticketbox-ajax/getdestinations', {
                    cityId: cityId
                });
                return ( request.then(handleSuccess, handleError) );
            }

            function getCityList() {
                var request = $http.post('/admin-panel/ticketbox-ajax/getcities');
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
                getDestinationForCity: getDestinationForCity,
                getCityList: getCityList
            });
        }]
);