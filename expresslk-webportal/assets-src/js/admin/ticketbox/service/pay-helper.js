/**
 * Created by udantha on 1/28/16.
 */
expressAdminApp.service(
    "payHelperService", ['$http', '$q', function ($http, $q) {
        this._agents = false;
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
        this.getAgents = function() {
            var defer = $q.defer();
            //send request if not already loaded
            if(!this._agents){
                var request = $http.post('/admin-panel/ticketbox-ajax/getagents', {});
                request.then(function(response) {
                    //check if there is error
                    if(response.data.hasOwnProperty('error')){
                        defer.reject("An unknown error occurred.")
                    }
                    //save agents for later
                    this._agents = response.data;
                    defer.resolve(response.data);
                }, function(response) {
                    //normalize server response in case of page error
                    if (!angular.isObject(response.data) || !response.data.error) {
                        defer.reject("An unknown error occurred.")
                    }else{
                        // Otherwise, use expected error message.
                        defer.reject(response.data.error)
                    }
                });
            }else{
                defer.resolve(this._agents);
            }

            return defer.promise;
        }

    }]
);