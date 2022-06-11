expressAdminApp.service("reportService", ['$http', '$q', '$window',
    function ($http, $q, $window) {

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
            return (request.then(handleSuccess, handleError));
        }
		function getSupplierList() {
            var request = $http.post('/admin-panel/report/getsupplierlist');
            return (request.then(handleSuccess, handleError));
        }
        /**
         * Generates a report
         *
         * @param type
         * @param parameters
         * @returns {IPromise<TResult>}
         */
        function generateReport(type, parameters) {
            return openReportWindow({
                url: '/admin-panel/report/generatereport',
                title: 'Report Viewer',
                params: {
                    type: type,
                    parameters: parameters
                }
            });
        }
		
		function getExpInvList(month){
			var request = $http.post('/admin-panel/report/expinvoiceList', {
					from: month 
				});
			return( request.then(handleSuccess, handleError) );
		}
		
		function getApprovedRefundReport(month){
			var request = $http.post('/admin-panel/report/approvedrefundsList', {
					from: month 
				});
			return( request.then(handleSuccess, handleError) );
		}
		function getApprovedRefundV2Report(month){
			var request = $http.post('/admin-panel/report/approvedrefundsV2List', {
					from: month 
				});
			return( request.then(handleSuccess, handleError) );
		}
		function getCashReconReport(month){
			var request = $http.post('/admin-panel/report/cashreconList', {
					from: month 
				});
			return( request.then(handleSuccess, handleError) );
		}
		function getDailyCashReconReport(month){
			var request = $http.post('/admin-panel/report/dailycashreconList', {
					from: month 
				});
			return( request.then(handleSuccess, handleError) );
        }
        function getDailyCashReconV2Report(month){
			var request = $http.post('/admin-panel/report/dailycashreconV2List', {
					from: month 
				});
			return( request.then(handleSuccess, handleError) );
		}
		function getFutureResReport(month){
			var request = $http.post('/admin-panel/report/futureresList', {
					from: month 
				});
			return( request.then(handleSuccess, handleError) );
		}
		function getBusFareReport(month){
			var request = $http.post('/admin-panel/report/busfareList', {
					from: month 
				});
			return( request.then(handleSuccess, handleError) );
		}
		function getCounterCashReconReport(month){
			var request = $http.post('/admin-panel/report/ticketCounterReconciliationList', {
					from: month 
				});
			return( request.then(handleSuccess, handleError) );
		}
		function getDepotBusFareReport(month,supplier){
			var request = $http.post('/admin-panel/report/depotBusfareList', {
					from: month,
					supplier : supplier
				});
			return( request.then(handleSuccess, handleError) );
		}
        // ---
        // PRIVATE METHODS.
        // ---
        /**
         * Open a window for report viewer
         * @param data
         * @returns {*}
         */
        function openReportWindow(data) {
            var width = 960, height = 500;
            var options = "status=no,toolbar=no,menubar=no,scrollbars=no,location=no"
                + ",width=" + width
                + ",height=" + height
                + ",top=" + ((screen.height / 2) - (height / 2))
                + ",left=" + ((screen.width / 2) - (width / 2));
            var win = $window.open('', data.title, options);
            //build html
            var html = '<form action="' + data.url + '" method="post">';
            //add params
            for (var key in data.params) {
                var param = data.params[key];
                //if this is string, append it
                if ($.type(param) === 'string') {
                    html += '<input type="hidden" name="' + key + '" value="' + param + '" />';
                } else {
                    //loop and append children
                    for (var key2 in param) {
                        html += '<input type="hidden" name="' + key + '[' + key2 + ']" value=' + JSON.stringify(param[key2]) + ' />';
                    }
                }
            }
            html += '</form>';
            win.document.write('<html><head></head><body>' + html + ' <div style="text-align: center;">Loading..</div></body></html>')
            //compile and attach to body
            $(win.document).find('form').submit();
            return win;
        }

        function handleError(response) {
            //normalize server response in case of page error
            if (!angular.isObject(response.data) || !response.data.error) {
                return ($q.reject("An unknown error occurred."));
            }

            // Otherwise, use expected error message.
            return ($q.reject(response.data.error));
        }

        function handleSuccess(response) {
            //check if there is error
            if (response.data.hasOwnProperty('error')) {
                return ($q.reject(response.data.error));
            }
            return (response.data);
        } 

        // Return public API.
        return ({
            getReportList: getReportList,
			getSupplierList : getSupplierList,
            // generateReport: generateReport,
			getExpInvList : getExpInvList,
            getApprovedRefundReport : getApprovedRefundReport,
            getApprovedRefundV2Report : getApprovedRefundV2Report,
			getCashReconReport : getCashReconReport,
            getDailyCashReconReport : getDailyCashReconReport,
            getDailyCashReconV2Report : getDailyCashReconV2Report,
			getFutureResReport : getFutureResReport,
			getBusFareReport : getBusFareReport,
			getCounterCashReconReport : getCounterCashReconReport,
			getDepotBusFareReport : getDepotBusFareReport
        });
    }]);
        