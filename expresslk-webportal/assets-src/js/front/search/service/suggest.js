/**
 * Created by udantha on 2/13/15.
 */
expressFrontApp.service(
    "suggestionService", ['$http', '$q',
        function ($http, $q) {
            // ---
            // PUBLIC METHODS.
            // ---
            /**
             *
             * @param cityText
             * @returns {*}
             * @private
             */
            function _city(cityText) {
                var request = $http.post('/app/search/getcitysuggestion', {
                    term: cityText,
                    type: 'city'
                });
                return ( request.then(handleSuccess, handleError) );
            }

            /**
             *
             * @param originText
             * @param cityText
             * @returns {*}
             * @private
             */
            function _destination(originText, cityText) {
                var request = $http.post('/app/search/getcitysuggestion', {
                    term: cityText,
                    origin: originText,
                    type: 'dest'
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
                getCity: _city,
                getDestination: _destination
            });
        }]
);