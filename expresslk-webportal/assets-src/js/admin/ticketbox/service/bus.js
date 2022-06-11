/**
 * Created by udantha on 10/15/14.
 */
expressAdminApp.service(
    "busService", ['$http', '$q', function ($http, $q) {
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
        function getTicketSchedule(from, to, date, page) {
            var request = $http.post('/admin-panel/ticketbox-ajax/displayschedule', {
                from: from,
                to: to,
                date: date,
                page: page
            });
            return( request.then(handleSuccess, handleError) );
        }

        /**
         * Get seat information
         * @param boardingLocation
         * @param dropoffLocation
         * @param busType
         * @param scheduleId
         * @param session
         * @returns {IPromise<TResult>}
         */
        function getSeatInfo(boardingLocation, dropoffLocation, busType, scheduleId, session) {
            var request = $http.post('/admin-panel/ticketbox-ajax/seatinfo', {
                boardingLocation: boardingLocation,
                dropoffLocation: dropoffLocation,
                busType: busType,
                scheduleId: scheduleId,
                session: session
            });
            return( request.then(handleSuccess, handleError) );
        }

        /**
         * confirm
         * @param fromCity
         * @param toCity
         * @param resultIndex
         * @param seats
         * @param session
         * @returns {ng.IPromise<TResult>|*}
         */
        function confirmBooking(fromCity, toCity, resultIndex, bookingData, session) {
            var request = $http.post('/admin-panel/ticketbox-ajax/placebooking', {
                session: session,
                fromCity: fromCity,
                toCity: toCity,
                resultIndex: resultIndex,
                bookingData: bookingData
            });
            return( request.then(handleSuccess, handleError) );
        }

        function releaseHeldBooking(reference) {
            var request = $http.post('/admin-panel/ticketbox-ajax/releaseholded', {
                reference: reference
            });
            return( request.then(handleSuccess, handleError) );
        }

        /**
         * Set bus data
         * @param bus
         */
        function setSelectedBus(bus) {
            _bus = bus;
        }

        /**
         * get selected bus data
         * @returns {boolean}
         */
        function getSelectedBus() {
            return _bus;
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
            getTicketSchedule: getTicketSchedule,
            getSeatInfo: getSeatInfo,
            confirmBooking: confirmBooking,
            setSelectedBus: setSelectedBus,
            getSelectedBus: getSelectedBus,
            releaseHeldBooking: releaseHeldBooking
        });
    }]
);