/**
 * Service to handle buses services
 */
expressAdminApp.service(
    "scheduleService", ['$http', '$q', function ($http, $q) {
        // ---
        // PUBLIC METHODS.
        // ---
        /**
         * Send create schedule bacend request
         * @param schedule
         * @returns {*}
         */
        function createSchedule(schedule) {
            var request = $http.post('/admin-panel/bus-service/createschedule', schedule);
            return( request.then(handleSuccess, handleError) );
        }

        /**
         *
         * @param schedule
         * @returns {*}
         */
        function getSchedules(schedule) {
            var request = $http.post('/admin-panel/bus-service/schedulelist', {
                scheduleId: schedule
            });
            return( request.then(handleSuccess, handleError) );
        }

        /**
         * Update operational stage of a schedule
         * @param scheduleId
         * @param stageCode
         * @returns {*}
         */
        function setStage(scheduleId, stageCode) {
            var request = $http.post('/admin-panel/bus-service/setschedulestage', {
                scheduleId: scheduleId,
                stageCode: stageCode
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
            return( response.data );
        }

        // Return public API.
        return({
            createSchedule: createSchedule,
            getSchedules: getSchedules,
            setStage: setStage
        });
    }]
);