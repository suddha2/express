expressAdminCrudApp.service("reportServiceCrud", ['$http', '$q',
    function ($http, $q) {

        // ---
        // PUBLIC METHODS.
        // ---
		/**
		 * Returns the list of reports
		 * 
		 * @returns {IPromise<TResult>}
		 */
		function getReportList() {
			var request = $http.post('/admin-panel/report/getreportlist');
            return(request.then(handleSuccess, handleError));
		}
	
		/**
		 * Generates a report
		 * 
		 * @param type
         * @param parameters
         * @returns {IPromise<TResult>}
		 */
		function generateReport(type, parameters) {
			var request = $http.post('/admin-panel/report/generatereport', {
                type: type,
                parameters: parameters
            });
            return(request.then(handleSuccess, handleError));
		}
		
		// ---
        // PRIVATE METHODS.
        // ---
        function handleError(response) {
            //normalize server response in case of page error
            if (!angular.isObject(response.data) || !response.data.error) {
                return($q.reject("An unknown error occurred."));
            }

            // Otherwise, use expected error message.
            return($q.reject(response.data.error));
        }

        function handleSuccess(response) {
            //check if there is error
            if (response.data.hasOwnProperty('error')){
            	return($q.reject(response.data.error));
            }
            return(response.data);
        }
        
        // Return public API.
        return({
        	getReportList: getReportList,
        	//generateReport: generateReport
        });
	}]);
        