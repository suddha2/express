Entities.booking = function (nga, booking, client, passenger, payment, refund, change, changeType, bookingItem, busStop, busSchedule, bookingStatus, cancellationCharge, rule, user, bus, bookingItemPassenger, agent, division) {

    booking.listView()
        .title('Bookings')
        .url('bookingLight')
        .fields([
            nga.field('bookingTime', 'datetime')
                .label('Booking time')
                .format(nga.timeFormat)
                .parse(nga.parseDate),
            nga.field('reference')
                .label('Reference'),
            nga.field('status')
                .label('Status'),
            nga.field('clientMobileTelephone')
                .label('Mobile'),
            nga.field('fromCity')
                .label('From'),
            nga.field('toCity')
                .label('To')
        ])
        .filters([
            nga.field('division', 'reference')
                .targetEntity(division)
                .targetField(nga.field('name'))
                .label('Booking division')
                .attributes({'placeholder': 'Filter by booking division'}),
            nga.field('agent', 'reference')
                .targetEntity(agent)
                .targetField(nga.field('name'))
                .label('Agent')
                .attributes({'placeholder': 'Filter by agent'}),
            nga.field('reference~')
                .label('Reference')
                .pinned(true)
                .attributes({'placeholder': 'Filter by reference'}),
            nga.field('clientNic~')
                .label('Client\'s NIC')
                .pinned(true)
                .attributes({'placeholder': 'Filter by client\'s NIC'}),
            nga.field('clientMobileTelephone')
                .label('Client\'s mobile')
                .pinned(true)
                .attributes({'placeholder': 'Filter by client\'s mobile'}),
            nga.field('clientName*')
                .label('Client\'s name')
                .attributes({'placeholder': 'Filter by client\'s name'})
        ])
        .actions(['filter'])
        .listActions(['show', 'edit']);

    booking.editionView()
        .title('Edit booking "{{ entry.values.reference }}"')
        .actions(['list', 'show'])
        .fields([
            // general
            nga.field('reference')
                .label('Reference')
                .editable(false),
            nga.field('', 'template')
                .label('Alerts')
                .template('<alert-sms booking-reference="entry.values.reference"></alert-sms> | ' +
                    '<alert-email booking-reference="entry.values.reference"></alert-email>'),
            nga.field('bookingTime', 'datetime')
                .label('Booking time')
                .format(nga.timeFormat)
                .parse(nga.parseDate)
                .editable(false),
                nga.field('status.code')
                    .label('Status')
                    .editable(false),
            nga.field('expiryTime', 'datetime')
                .label('Expiry time')
                .editable(false)
                .format(nga.timeFormat)
                .parse(nga.parseDate)
                .cssClasses(function (entry) {
                    if (entry && entry.values && entry.values['status.code'] == 'CONF') {
                        return 'ng-hide';
                    }
                }),

            // booking items
            nga.field('bookingItems', 'embedded_list')
                .label('Items')
                .targetEntity(bookingItem)
                .editable(false)
                .targetFields([
                    nga.field('id').detailLinkRoute('edit').label('ID'),
                    nga.field('', 'template')
                        .template("<span>{{entry.values['schedule.bus.plateNumber']}} - {{entry.values['schedule.busRoute.name']}}</span>")
                        .label('Bus'),
                    nga.field('schedule.departureTime', 'datetime')
                        .format(nga.timeFormat).parse(nga.parseDate).label('Departure'),
                    nga.field('fromBusStop.name').label('From'),
                    nga.field('toBusStop.name').label('To'),
                    nga.field('', 'template')
                        .template("<span ng-repeat='pass in entry.values.passengers'>{{pass.seatNumber}}{{$last ? '' : ', '}}</span>")
                        .label('Seat Numbers')
                ]),

            // client, passengers
            nga.field('', 'template')
                .template(" <div class='btn-group'>" +
                    /** This is a hack to get the link in. No other way to do this **/
                    "<span class='btn-edit'><a ng-href='#/client/edit/{{entry.values[\"client.id\"]}}' class='btn btn-default'>Edit</a></span>" +
                    "<div class='btn btn-default text-selectable'><i class='fa fa-male'></i> " +
                    "   <input type='text' readonly value='{{::entry.values[\"client.nic\"]}}' onclick='this.setSelectionRange(0, this.value.length)' class='submissive' />" +
                    "</div>" +
                    "<div class='btn btn-default text-selectable'><i class='fa fa-user'></i> " +
                    "   <input type='text' readonly value='{{::entry.values[\"client.name\"]}}' onclick='this.setSelectionRange(0, this.value.length)' class='submissive' />" +
                    "</div>" +
                    "<div class='btn btn-default text-selectable'><i class='fa fa-envelope pink-text'></i> " +
                    "   <input type='text' readonly value='{{::entry.values[\"client.email\"]}}' onclick='this.setSelectionRange(0, this.value.length)' class='submissive' />" +
                    "</div>" +
                    "<div class='btn btn-default text-selectable'><i class='fa fa-phone blue-text'></i> " +
                    "   <input type='text' readonly value='{{::entry.values[\"client.mobileTelephone\"]}}' onclick='this.setSelectionRange(0, this.value.length)' class='submissive' />" +
                    "</div>" +
                    "</div>")
                .label('Client'),
            nga.field('passengers', 'embedded_list')
                .label('Passengers')
                .editable(false)
                .targetEntity(passenger)
                .targetFields([
                    nga.field('name').label('Name'),
                    nga.field('nic').label('NIC'),
                    nga.field('gender').label('Gender'),
                    nga.field('passengerType').label('Type')
                ]),

            // booked by
            nga.field('user.username')
                .label('Booked by')
                .editable(false),

            // // agent
            nga.field('', 'template')
                .template("<span>{{entry.values['agent.name']}}</span>")
                .editable(false)
                .label('Agent'),

            // payable, payments and refunds
            nga.field('chargeable', 'number').format('0.00')
                .editable(false)
                .label('Chargeable'),
            nga.field('payments', 'embedded_list')
                .label('Payments')
                .editable(false)
                .targetEntity(payment)
                .targetFields([
                    //nga.field('id').detailLinkRoute('show').label('ID'),
                    nga.field('time', 'datetime').label('Time'),
                    nga.field('amount', 'number').format('0.00').label('Amount'),
                    nga.field('mode').label('Mode'),
                    nga.field('reference').label('Reference')
                ]),
            nga.field('', 'template')
                .label('')
                .template('<ma-create-button entity-name="payment" size="sm" label="Add payment" default-values="{ bookingId: entry.values.id }"></ma-create-button>'),
            nga.field('cancellations', 'embedded_list')
                .label('Cancellation charges')
                .editable(false)
                .targetEntity(cancellationCharge)
                .targetFields([
                    nga.field('cancellationSchemeId', 'reference')
                        .targetEntity(rule)
                        .targetField(nga.field('name'))
                        .label('Cancellation scheme'),
                    nga.field('amount', 'number').format('0.00').label('Amount')
                ]),
            nga.field('cancellationCause')
                .editable(false)
                .cssClasses(function(entry) {
                    if (entry && entry.values && entry.values['status.code'] != 'CANC') {
                        return 'ng-hide';
                    }
                })
                .label('Cancellation cause'),
            nga.field('refunds', 'embedded_list')
                .label('Refunds')
                .editable(false)
                .targetEntity(refund)
                .targetFields([
                    nga.field('id').detailLinkRoute('show').label('ID'),
                    nga.field('time', 'datetime').label('Time'),
                    nga.field('amount', 'number').format('0.00').label('Amount'),
                    nga.field('mode').label('Mode'),
                    nga.field('reference').label('Reference')
                ]),
            nga.field('', 'template')
                .label('')
                .template('<ma-create-button entity-name="refund" size="sm" label="Add refund" default-values="{ bookingId: entry.values.id }"></ma-create-button>'),

            // remarks, changes
            nga.field('remarks', 'text')
                .label('Remarks'),
            nga.field('changes', 'embedded_list')
                .label('Changes')
                .editable(false)
                .targetEntity(change)
                .targetFields([
                    nga.field('changeTime', 'datetime').label('Time'),
                    nga.field('type.code').label('Type'),
                    nga.field('description').label('Description')
                ])

        ]);

    booking.showView()
        .title('Booking "{{ entry.values.reference }}"')
        .actions(['list', 'edit'])
        .fields([
            // general
            nga.field('id')
                .label('ID'),
            nga.field('reference')
                .label('Reference'),
            nga.field('bookingTime', 'datetime')
                .label('Booking time')
                .format(nga.timeFormat)
                .parse(nga.parseDate),
            nga.field('status.code')
                .label('Status'),
            nga.field('expiryTime', 'datetime')
                .label('Expiry time')
                .format(nga.timeFormat)
                .parse(nga.parseDate)
                .cssClasses(function (entry) {
                    if (entry && entry.values && entry.values['status.code'] == 'CONF') {
                        return 'ng-hide';
                    }
                }),
 
            // booking items
            nga.field('bookingItems', 'embedded_list')
                .label('Items')
                .targetEntity(bookingItem)
                .targetFields([
                    nga.field('', 'template')
                        .template("<span>{{entry.values['schedule.bus.plateNumber']}} - {{entry.values['schedule.busRoute.name']}}</span>")
                        .label('Bus'),
                    nga.field('', 'template')
                        .template("<span>{{entry.values['schedule.busRoute.name']}}</span>")
                        .label('Route'),
                    nga.field('schedule.departureTime', 'datetime')
                        .format(nga.timeFormat).parse(nga.parseDate).label('Departure'),
                    //nga.field('schedule.id').label('Schedule'), // TODO add a link here to the schedule
                    nga.field('fromBusStop.name').label('From'),
                    nga.field('toBusStop.name').label('To'),
                    nga.field('', 'template')
                        .template("<span ng-repeat='pass in entry.values.passengers'>{{pass.seatNumber}}{{$last ? '' : ', '}}</span>")
                        .label('Seats')
                ]),

            // client, passengers
            nga.field('', 'template')
                .template(" <div class='btn-group'>" +
                    "<div class='btn btn-default text-selectable'><i class='fa fa-user'></i> {{entry.values['client.name']}}</div>" +
                    "<div class='btn btn-default text-selectable'><i class='fa fa-male'></i> {{entry.values['client.nic']}}</div>" +
                    "<div class='btn btn-default text-selectable'><i class='fa fa-phone blue-text'></i> {{entry.values['client.mobileTelephone']}}</div>" +
                    "<div class='btn btn-default text-selectable'><i class='fa fa-envelope pink-text'></i> {{entry.values['client.email']}}</div>" +
                    "</div>")
                .label('Client'),
            nga.field('passengers', 'embedded_list')
                .label('Passengers')
                .targetEntity(passenger)
                .targetFields([
                    nga.field('name').label('Name'),
                    nga.field('nic').label('NIC'),
                    nga.field('gender').label('Gender'),
                    nga.field('passengerType').label('Type')
                ]),

            // booked by
            nga.field('user.username')
                .label('Booked by'),
            // agent
            nga.field('agent.name')
                .label('Agent'),
                
            // payable, payments and refunds
            nga.field('chargeable', 'number').format('0.00')
                .label('Chargeable'),
            nga.field('payments', 'embedded_list')
                .label('Payments')
                .targetEntity(payment)
                .targetFields([
                    nga.field('id').detailLinkRoute('show').label('ID'),
                    nga.field('time', 'datetime').label('Time'),
                    nga.field('amount', 'number').format('0.00').label('Amount'),
                    // nga.field('mode').label('Mode'),
					// nga.field('vendorMode').label('VendorMode'),
					nga.field('Method').label('Mode').map(function (value, entry){return entry.mode+ ' '+ entry.vendorMode;}),
					nga.field('reference').label('Reference')
                ]),
            nga.field('cancellations', 'embedded_list')
                .label('Cancellation charges')
                .targetEntity(cancellationCharge)
                .targetFields([
                    nga.field('cancellationSchemeId', 'reference')
                        .targetEntity(rule)
                        .targetField(nga.field('name'))
                        .label('Cancellation scheme'),
                    nga.field('amount', 'number').format('0.00').label('Amount')
                ]),
            nga.field('cancellationCause')
                .cssClasses(function(entry) {
                    if (entry && entry.values && entry.values['status.code'] != 'CANC') {
                        return 'ng-hide';
                    }
                })
                .label('Cancellation cause'),
            nga.field('refunds', 'embedded_list')
                .label('Refunds')
                .targetEntity(refund)
                .targetFields([
                    nga.field('id').detailLinkRoute('show').label('ID'),
                    nga.field('time', 'datetime').label('Time'),
                    nga.field('amount', 'number').format('0.00').label('Amount'),
                    nga.field('mode').label('Mode'),
                    nga.field('reference').label('Reference')
                ]),

            // remarks, changes
            nga.field('remarks', 'text')
                .label('Remarks'),
            nga.field('changes', 'embedded_list')
                .label('Changes')
                .targetEntity(change)
                .targetFields([
                    nga.field('changeTime', 'datetime').label('Time'),
                    nga.field('type.code').label('Type'),
                    nga.field('description').label('Description')
                ])
        ]);

    return this;
};
