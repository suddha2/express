/**
 * Created by udantha on 5/8/16.
 */
var Fragment = Fragment ? Fragment : {};

Fragment.Name = {
    ticketBox: 'TicketBox',
    reportsView: 'ReportsView',
    alertConductor: 'AlertConductor',
    printSeat   : 'printSeat',
    ticketBoxHistory: 'TicketBoxHistory',
	expressInvoiceReport : 'ExpressInvoiceReport',
	approvedRefundReport : 'ApprovedRefundReport',
	futureReservationReport  : 'FutureReservationReport',
	dailyCashReconciliation  : 'DailyCashReconciliation',
	cashReconciliation  : 'CashReconciliation',
	operatedBusFare  : 'OperatedBusFare',
	operatedBusFee : 'OperatedBusFee',
	ticketCounterReconciliation  : 'TicketCounterReconciliation',
	depoOperatedBusFare  : 'DepoOperatedBusFare',

    // for vendor mode
    payMethodCash: 'payMethodCash',
    payMethodCard: 'payMethodCard',
    payMethodMCash: 'payMethodMCash',
    payMethodEzCash: 'payMethodEzCash',
    payMethodBankTransfer: 'payMethodBankTransfer',

    // for payment refund mode
    payMethodCashBBK     : 'payMethodCashBBK',
    payMethodCardBBK     : 'payMethodCardBBK',
    payMethodMCashBBK    : 'payMethodMCashBBK',
    payMethodEzCashBBK   : 'payMethodEzCashBBK',
    payMethodPayPalBBK   : 'payMethodPayPalBBK',
    payMethodBankTransferBBK : 'payMethodBankTransferBBK',
    payMethodVoucherBBK  : 'payMethodBankVoucherBBK',

    payMethodPayLater    : 'payMethodPayLater',
    payMethodAgent       : 'payMethodAgent',
    payMethodPayAtBus    : 'payMethodPayAtBus',
    payMethodAgentBBK    : 'payMethodAgentBBK',
	
	// Restructured Fragments
	dashboard: 'Dashboard',
	operations: 'Operations',
	issueTickets: 'issueTickets',
	recentTickets : 'recentTickets',
	bookingListing : 'bookingListing',
	bookingCancel : 'bookingCancel',
	busListing : 'busListing',
	busLocation : 'busLocation',
	managePerson : 'managePerson',
	manageConductor : 'manageConductor',
	manageDriver : 'manageDriver',
	bookingAagent : 'bookingAagent',
	amenities : 'Amenities',
	manageSupplier : 'manageSupplier',
	manageAccount : 'manageAccount',
	contactPersonal : 'contactPersonal',
	scheduleList : 'scheduleList',
	scheduleAdd : 'scheduleAdd',
	manageUser : 'manageUser',
	manageRole : 'manageRole',
	manageSinglePermission : 'manageSinglePermission',
	manageGroupPermission : 'manageGroupPermission',
	operationReport : 'operationReport',
	performanceReport : 'performanceReport',
	manageCity : 'manageCity',
	manageStop : 'manageStop',
	manageRoute : 'manageRoute'
	
};

// var ht = '';
// for (var i in Fragment.Name){
//     ht += 'insert into permission (code, name, description, module_id) values ("'+ i +'/execute", "'+ i +' permission", "Allow payment method '+ i +' to be Accessed", 3); \n'
// }
// console.log(ht)