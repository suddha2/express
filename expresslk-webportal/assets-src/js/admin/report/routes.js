/**
 * Created by udantha on 10/14/14.
 */

expressAdminApp.config(['$stateProvider', function($stateProvider) {
    $stateProvider
        .state('reports', {
            parent: 'main',
            url: "/reports",
            templateUrl: "/admin-panel/report/"
        });
	 $stateProvider
        .state('expInv', {
            parent: 'main',
            url: "/expInv",
            templateUrl: "/admin-panel/report/expinvoice"
        });	
	$stateProvider
        .state('approvedRefunds', {
            parent: 'main',
            url: "/approvedRefunds",
            templateUrl: "/admin-panel/report/approvedRefunds"
        });
    $stateProvider
        .state('approvedRefundsV2', {
            parent: 'main',
            url: "/approvedRefundsV2",
            templateUrl: "/admin-panel/report/approvedRefundsV2"
        });
	$stateProvider
        .state('futureReservationReport', {
            parent: 'main',
            url: "/futureReservationReport",
            templateUrl: "/admin-panel/report/futureReservationReport"
        });	
	$stateProvider
        .state('dailyCashReconciliation', {
            parent: 'main',
            url: "/dailyCashReconciliation",
            templateUrl: "/admin-panel/report/dailyCashReconciliation"
        });	
    $stateProvider
        .state('dailyCashReconciliationV2', {
            parent: 'main',
            url: "/dailyCashReconciliationV2",
            templateUrl: "/admin-panel/report/dailyCashReconciliationV2"
        });	
	$stateProvider
        .state('cashReconciliation', {
            parent: 'main',
            url: "/cashReconciliation",
            templateUrl: "/admin-panel/report/cashReconciliation"
        });	
	$stateProvider
        .state('operatedBusFare', {
            parent: 'main',
            url: "/operatedBusFare",
            templateUrl: "/admin-panel/report/operatedBusFare"
        });	
	$stateProvider
        .state('operatedBusFee', {
            parent: 'main',
            url: "/operatedBusFee",
            templateUrl: "/admin-panel/report/operatedBusFee"
        }); 
	$stateProvider
        .state('ticketCounterReconciliation', {
            parent: 'main',
            url: "/ticketCounterReconciliation",
            templateUrl: "/admin-panel/report/ticketCounterReconciliation"
        }); 
	
	$stateProvider
        .state('depotOperatedBusFare', {
            parent: 'main',
            url: "/depotOperatedBusFare",
            templateUrl: "/admin-panel/report/depotOperatedBusFare"
        }); 
}]); 