Entities.operationalSchedule = function(nga, operationalSchedule, busSchedule, busRoute, city, division) {
   
    operationalSchedule.listView()
        .title('Operations')
        .fields([
            nga.field('routeNumberName')
                .label('Bus route'),
            nga.field('plateNumber')
                .label('Bus'),
            nga.field('arrivalTime', 'datetime')
                .label('Terminal in')
                .format(nga.timeFormat),
            nga.field('departureTime', 'datetime')
                // .cssClasses(['highlight-row'])
                .cssClasses(function(entry) {
                    if(entry && entry.values && entry.values.departureTime){
                        if(moment(entry.values.departureTime).startOf('day').isSame(moment().startOf('day'))){
                            return 'highlight-row';
                        }
                    }
                })
                .label('Terminal out')
                .format(nga.timeFormat),
            nga.field('conductorMobile')
                .label('Contact'),
            nga.field('seatReservations', 'template')
                .label('Reservations')
                .template('<span style="white-space: nowrap;" ng-controller="operationsController">' 
                        + '<ma-number-column value="::value"></ma-number-column>&nbsp;'
                        + '<a class="btn btn-default btn-xs" ng-click="printBookingReport(entry.values.id)">'
						//+ '<a class="btn btn-default btn-xs" ng-if="entry.values.plateNumber | regex "  ng-click="printBookingReport(entry.values.id)" >'
                        + '<span class="glyphicon glyphicon-print" aria-hidden="true"></span>&nbsp;Print'
                        + '</a>'
                        + '</span>'),
            nga.field('totalCost', 'template')
                .label('Payable')
                .cssClasses('pull-right')
                .template('<span style="white-space: nowrap;" ng-controller="operationsController">' 
                        // + '<ma-number-column value="::value"></ma-number-column>&nbsp;'
                         + '<a class="btn btn-default btn-xs" ng-click="printPaymentReport(entry.values.id)">'
                        //+ '<a class="btn btn-default btn-xs" data-ng-click=" disabled || printPaymentReport(entry.values.id)" data-ng-class="{ disabled: disabled }>'
                        //+ '<a class="btn btn-default btn-xs" ng-if="entry.values.plateNumber | regex  "  ng-click="printPaymentReport(entry.values.id)" >'			
                        + '<span class="glyphicon glyphicon-print" aria-hidden="true"></span>&nbsp;Print'
                        + '</a>'
						//+ '<span ng-hide="entry.values.plateNumber | regex  " style="font-family:Helvetica Neue,Helvetica,Arial,sans-serif;font-size:15px;color:red;" > Please Update Bus</span>'
                        + '</span>'),
            nga.field('stage', 'template')
                .label('Stage')
                .template('<span style="white-space: nowrap;" ng-controller="operationsController">' 
                        + '<ma-string-column value="::value"></ma-string-column>&nbsp;'
                        + '<a class="btn btn-default btn-xs pink-text" title="Close for bookings" ng-if="entry.values.stage==\'OFB\'" ng-click="changeStageTo(entry.values.id, \'CFB\')">'
                        + '<span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span>&nbsp;Close Bookings (alert)'
                        + '</a>'
                        + '<a class="btn btn-default btn-xs" title="Notify the bus" ng-if="entry.values.stage==\'CFB\'" ng-click="changeStageTo(entry.values.id, \'BN\')">'
                        + '<span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>&nbsp;Bus Notified'
                        + '</a>'
                        + '<a class="btn btn-default btn-xs" title="Pay the bus" ng-if="entry.values.stage==\'BN\'" ng-click="changeStageTo(entry.values.id, \'PP\')">'
                        + '<span class="glyphicon glyphicon-usd" aria-hidden="true"></span>&nbsp;Payed'
                        + '</a>'
                        + '</span>')
        ])
        .permanentFilters({ arrivalStart : moment().startOf('day').valueOf() })
        .filters([
            nga.field('divisionId', 'reference')
                .targetEntity(division)
                .targetField(nga.field('name'))
                .label('Booking division'),
            nga.field('fromCityId', 'reference')
                .label('Departure city')
                .pinned(true)
                .targetEntity(city)
                .targetField(nga.field('name'))
                .sortField('name')
                .sortDir('ASC')
                .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'name*': search};
                     }
                 }),
            nga.field('routeId', 'reference')
                .label('Bus route')
                .pinned(true)
                .targetEntity(busRoute)
                .targetField(nga.field('name'))
                .sortField('name')
                .sortDir('ASC')
                .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'name*': search};
                     }
                 }),
            // nga.field('departureOn', 'date')
                 // .label('Terminal out')
                 // .pinned(true)
                 // .format(nga.timeFormat)
                 // .parse(nga.parseDate),
            nga.field('departureStart', 'datetime')
                .label('Terminal out - from')
                .format(nga.timeFormat)
				.pinned(true)
                .parse(nga.parseDate),
            nga.field('departureEnd', 'datetime')
                .label('Terminal out - to')
                .format(nga.timeFormat)
				.pinned(true)
                .parse(nga.parseDate),
        ])
        .sortField('arrivalTime')
        .sortDir('ASC')
        .actions(['filter'])    
        .listActions(['show']);

    operationalSchedule.showView()
        .title('Operational schedule #{{ entry.values.id }}')
        .fields([
             nga.field('routeNumberName')
                 .label('Bus route'),
             nga.field('plateNumber')
                 .label('Bus'),
             nga.field('arrivalTime', 'datetime')
                 .label('Terminal in')
                 .format(nga.timeFormat),
             nga.field('departureTime', 'datetime')
                 .label('Terminal out')
                 .format(nga.timeFormat),
            nga.field('stage')
                .label('Operational Stage'),
             nga.field('conductorName')
                 .label('Conductor name'),
             nga.field('conductorMobile')
                 .label('Conductor mobile'),
             nga.field('driverName')
                 .label('Driver name'),
             nga.field('driverMobile')
                 .label('Driver mobile'),
             nga.field('seatReservations', 'template')
                 .label('Reservations')
                 .template('<span style="white-space: nowrap;" ng-controller="operationsController">' 
                         + '<ma-number-column value="::value"></ma-number-column>&nbsp;'
                         + '<a class="btn btn-default btn-xs" ng-click="viewBookingReport(entry.values.id)">'
                         + '<span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>&nbsp;Show'
                         + '</a>&nbsp;'
                         + '<a class="btn btn-default btn-xs" ng-click="printBookingReport(entry.values.id)">'
                         + '<span class="glyphicon glyphicon-print" aria-hidden="true"></span>&nbsp;Print'
                         + '</a>'
                         + '</span>'),
             nga.field('totalCost', 'template')
                 .label('Payable')
                 .template('<span style="white-space: nowrap;" ng-controller="operationsController">' 
                         //+ '<ma-number-column value="::value"></ma-number-column>&nbsp;'
                         + '<a class="btn btn-default btn-xs" ng-click="viewPaymentReport(entry.values.id)">'
                         + '<span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>&nbsp;Show'
                         + '</a>&nbsp;'
                         + '<a class="btn btn-default btn-xs" ng-click="printPaymentReport(entry.values.id)">'
                         + '<span class="glyphicon glyphicon-print" aria-hidden="true"></span>&nbsp;Print'
                         + '</a>'
                         + '</span>')
        ])
        .actions(['list']);

    return this;
};