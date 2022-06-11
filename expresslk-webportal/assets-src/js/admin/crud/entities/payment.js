Entities.payment = function(nga, payment, booking) {

    // allowed vendor modes
    var vendorModes = [];
    ACL.fragmentCanExecute(Fragment.Name.payMethodCash, function () {
        vendorModes.push({ label: 'Cash', value: 'Cash' });
    });    
    ACL.fragmentCanExecute(Fragment.Name.payMethodCard, function () {
        vendorModes.push({ label: 'Card', value: 'Card' });
    });
    ACL.fragmentCanExecute(Fragment.Name.payMethodMCash, function () {
        vendorModes.push({ label: 'mCash', value: 'mCash' });
    });
    ACL.fragmentCanExecute(Fragment.Name.payMethodEzCash, function () {
        vendorModes.push({ label: 'eZCash', value: 'eZCash' });
    });
    ACL.fragmentCanExecute(Fragment.Name.payMethodBankTransfer, function () {
        vendorModes.push({ label: 'Bank transfer', value: 'BankTransfer' });
    });

    // allowed payment modes
    var payModes = [];
    ACL.fragmentCanExecute(Fragment.Name.payMethodCashBBK, function () {
        payModes.push({ label: 'Cash', value: 'Cash' });
    });    
    ACL.fragmentCanExecute(Fragment.Name.payMethodCardBBK, function () {
        payModes.push({ label: 'Card', value: 'Card' });
    });
    ACL.fragmentCanExecute(Fragment.Name.payMethodMCashBBK, function () {
        payModes.push({ label: 'mCash', value: 'mCash' });
    });
    ACL.fragmentCanExecute(Fragment.Name.payMethodEzCashBBK, function () {
        payModes.push({ label: 'eZCash', value: 'eZCash' });
    });
    ACL.fragmentCanExecute(Fragment.Name.payMethodPayPalBBK, function () {
        payModes.push({ label: 'PayPal', value: 'PayPal' });
    });
    ACL.fragmentCanExecute(Fragment.Name.payMethodBankTransferBBK, function () {
        payModes.push({ label: 'Bank transfer', value: 'BankTransfer' });
    });
    ACL.fragmentCanExecute(Fragment.Name.payMethodVoucherBBK, function () {
        payModes.push({ label: 'Voucher', value: 'Voucher' });
    });
    payModes.push({ label: 'Vendor (Chose from "Vendor mode")', value: 'Vendor' });
    
    // allowed currencies
    var currencies = [{ label: 'LKR', value: 'LKR' }];
    ACL.fragmentCanExecute(Fragment.Name.payMethodPayPalBBK, function () {
        currencies.push({ label: 'USD', value: 'USD' });
    });

    payment.creationView()
        .title('Add new payment')
        .fields([
             nga.field('type', 'choice')
                 .label('Type')
                 .validation({required: true})
                 .defaultValue('Payment')
                 .editable(false)
                 .choices([
                     { label: 'Payment', value: 'Payment' }
                 ]),
             nga.field('bookingId')
                 .label('Booking Id')
                 .editable(false),
             nga.field('time', 'datetime')
                 .label('Time')
                 .editable(false)
                 .defaultValue(new Date())
                 .validation({required: true}),
             nga.field('actualCurrency', 'choice')
                 .label('Currency')
                 .validation({required: true})
                 .choices(currencies),
             nga.field('actualAmount', 'float')
                 .format('0.00')
                 .label('Amount')
                 .validation({required: true}),
             nga.field('amount', 'float')
                 .format('0.00')
                 .label('Amount in LKR')
                 .validation({required: true}),
             nga.field('mode', 'choice')
                 .label('Mode')
                 .validation({required: true})
                 .choices(payModes),
             nga.field('vendorMode', 'choice')
                 .label('Vendor Mode')
                 .choices(vendorModes),
             nga.field('reference')
                 .label('Reference')
        ]);
    
    payment.editionView()
        .title('Edit payment "{{ entry.values.id }}"')
        .actions(['show'])
        .fields([
            payment.creationView().fields()
        ]);

    payment.showView()
        .title('Payment "{{ entry.values.id }}"')
        .actions(['edit'])
        .fields([
             nga.field('id')
                 .label('ID'),
             payment.editionView().fields()
        ]);

    return this;
}