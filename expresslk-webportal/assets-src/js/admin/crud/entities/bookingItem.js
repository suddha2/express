Entities.bookingItem = function(nga, bookingItem, busStop, busSchedule, bookingItemMarkup, bookingItemDiscount, bookingItemTax, bookingItemCharge, rule, change, changeType, bookingItemPassenger, passenger) {

    bookingItem.editionView()
        .title('<a href="/admin-panel/#/booking/show/{{ entry.values.bookingId }}">Booking #{{ entry.values.bookingId }}</a> / Item #{{ entry.values.index }}')
        .actions(['show'])
        .fields([
            //schedule
            nga.field('schedule', 'reference')
                .targetEntity(busSchedule)
                .targetField(nga.field('id'))
                .detailLinkRoute('show')
                .label('Schedule')
                .editable(false),

            // journey
            nga.field('fromBusStop', 'reference')
                .targetEntity(busStop)
                .targetField(nga.field('name'))
                .sortField('name')
                .sortDir('ASC')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'name*': search};
                    }
                })
                .label('From')
                .validation({required: true}),
            nga.field('toBusStop', 'reference')
                .targetEntity(busStop)
                .targetField(nga.field('name'))
                .sortField('name')
                .sortDir('ASC')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'name*': search};
                    }
                })
                .label('To')
                .validation({required: true}),

            // passengers
            nga.field('passengers', 'referenced_list')
                .label('Passengers')
                .targetEntity(bookingItemPassenger)
                .targetReferenceField('bookingItemId')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .targetFields([
                    nga.field('passengerId', 'reference')
                        .targetEntity(passenger)
                        .targetField(nga.field('name'))
                        .label('Name'),
                    nga.field('seatNumber').label('Seat'),
                    // nga.field('journeyPerformed', 'boolean').label('Journey performed')
                ]),

            // pricing
            nga.field('fare', 'number').format('0.00')
                .label('Fare')
                .editable(false),
            nga.field('markups', 'referenced_list')
                .label('Markup')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .targetEntity(bookingItemMarkup)
                .targetReferenceField('bookingItemId')
                .targetFields([
                    nga.field('markupSchemeId', 'reference')
                        .targetEntity(rule)
                        .targetField(nga.field('name'))
                        .label('Markup scheme'),
                    nga.field('amount', 'number').format('0.00').label('Amount'),
                ])
                .permanentFilters({
                    isMargin: false // hide margins
                }),
            nga.field('grossPrice', 'number').format('0.00')
                .label('Gross price')
                .editable(false),
            nga.field('discounts', 'referenced_list')
                .label('Discounts')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .targetEntity(bookingItemDiscount)
                .targetReferenceField('bookingItemId')
                .targetFields([
                    nga.field('discountSchemeId', 'reference')
                        .targetEntity(rule)
                        .targetField(nga.field('name'))
                        .label('Dicount scheme'),
                    nga.field('amount', 'number').format('0.00').label('Amount'),
                ]),
            nga.field('priceBeforeTax', 'number').format('0.00')
                .label('PBT')
                .editable(false),
            nga.field('taxes', 'referenced_list')
                .label('Taxes')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .targetEntity(bookingItemTax)
                .targetReferenceField('bookingItemId')
                .targetFields([
                    nga.field('taxSchemeId', 'reference')
                        .targetEntity(rule)
                        .targetField(nga.field('name'))
                        .label('Tax scheme'),
                    nga.field('amount', 'number').format('0.00').label('Amount'),
                ]),
            nga.field('charges', 'referenced_list')
                .label('Agent charges')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .targetEntity(bookingItemCharge)
                .targetReferenceField('bookingItemId')
                .targetFields([
                    nga.field('chargeSchemeId', 'reference')
                        .targetEntity(rule)
                        .targetField(nga.field('name'))
                        .label('Charge scheme'),
                    nga.field('amount', 'number').format('0.00').label('Amount'),
                ]),
            nga.field('price', 'number').format('0.00')
                .label('Price')
                .editable(false),
                
            // remarks, changes
            nga.field('remarks', 'text')
                .label('Remarks'),
            nga.field('changes', 'referenced_list')
                .label('Changes')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .targetEntity(change)
                .targetReferenceField('bookingItemId')
                .targetFields([
                    nga.field('changeTime', 'datetime').label('Time'),
                    nga.field('type', 'reference')
                        .targetEntity(changeType)
                        .targetField(nga.field('code'))
                        .label('Type'),
                    nga.field('description').label('Description')
                ])
        ]);

    bookingItem.showView()
        .title('<a href="/admin-panel/#/booking/show/{{ entry.values.bookingId }}">Booking #{{ entry.values.bookingId }}</a> / Item #{{ entry.values.index }}')
        .actions(['edit'])
        .fields([
             bookingItem.editionView().fields()
        ]);

    return this;
}