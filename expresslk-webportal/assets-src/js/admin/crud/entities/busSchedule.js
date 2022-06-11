Entities.busSchedule = function(nga, busSchedule, bus, busRoute, conductor, driver, busScheduleBusStop, seatingProfile,travelClass) {
    
    busSchedule.listView()
        .title('Bus schedules')
        .url('busScheduleLight')
        .fields([
            nga.field('busRouteName')
                .label('Bus route'),
            nga.field('busPlateNumber')
                .label('Bus'),
            nga.field('departureTime', 'datetime')
                .label('Departure time')
                .format(nga.timeFormat),
            nga.field('arrivalTime', 'datetime')
                .label('Arrival time')
                .format(nga.timeFormat),
            nga.field('', 'template')
                .label('Reservations')
                .template('<span style="white-space: nowrap;" ng-controller="operationsController">'
                    + '<a href="javascript:;" class="btn btn-default btn-xs" ng-click="printBookingReport(entry.values.id)">'
                    + '<span class="glyphicon glyphicon-print" aria-hidden="true"></span>&nbsp;Print'
                    + '</a>'
                    + '</span>')
        ])
        .filters([
            nga.field('busRoute', 'reference')
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
            nga.field('bus', 'reference')
                .label('Bus')
                .pinned(true)
                .targetEntity(bus)
                .targetField(nga.field('plateNumber'))
                .sortField('plateNumber')
                .sortDir('ASC')
                .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'plateNumber*': search};
                     }
                 }),
            nga.field('departureOn', 'date')
                .label('Departure date')
                .pinned(true)
                .format(nga.timeFormat)
                .parse(nga.parseDate), 
            nga.field('departureStart', 'datetime')
                .label('Departure time - from')
                .format(nga.timeFormat)
                .parse(nga.parseDate),
            nga.field('departureEnd', 'datetime')
                .label('Departure time - to')
                .format(nga.timeFormat)
                .parse(nga.parseDate),
            nga.field('arrivalStart', 'datetime')
                .label('Arrival time - from')
                .format(nga.timeFormat)
                .parse(nga.parseDate),
            nga.field('arrivalEnd', 'datetime')
                .label('Arrival time - to')
                .format(nga.timeFormat)
                .parse(nga.parseDate)
        ])
        .actions('<ma-view-batch-actions buttons="batchButtons()" selection="selection" entity="entity"></ma-view-batch-actions>'
                + '<ma-filter-button filters="filters()" enabled-filters="enabledFilters" enable-filter="enableFilter()" class="ng-scope ng-isolate-scope"></ma-filter-button>')
        .listActions(['show', 'edit', 'delete']);

    busSchedule.editionView()
        .title('Edit bus schedule #{{ entry.values.id }}')
        .actions(['list', 'show', 'delete'])

        .fields([
            nga.field('busRoute', 'reference')
                .label('Bus route')
                .targetEntity(busRoute)
                .targetField(nga.field('name'))
                .isDetailLink(false)
                .editable(false)
                .sortField('name')
                .sortDir('ASC'),
            nga.field('departureTime', 'datetime')
                .label('Departure time')
                .editable(false),
            nga.field('arrivalTime', 'datetime')
                .label('Arrival time')
                .editable(false),
            nga.field('bus', 'reference')
                .label('Bus')
                .targetEntity(bus)
                .targetField(nga.field('plateNumber'))
                //.sortField('plateNumber')
                //.sortDir('ASC')
				.remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'plateNumber^': search};
                    }
                }),
			
			nga.field('driver', 'reference')
                .label('Driver')
                .targetEntity(driver)
                .targetField(nga.field('fullName'))
                .detailLinkRoute('show')
                .validation({required: true})
                //.sortField('fullName')
                //.sortDir('ASC')
				.remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'fullName*': search};
                    }
                }),
            nga.field('conductor', 'reference')
                .label('Conductor')
                .targetEntity(conductor)
                .targetField(nga.field('fullName'))
                .detailLinkRoute('show')
                .validation({required: true})
                //.sortField('fullName')
                //.sortDir('ASC')
				.remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'fullName*': search};
                    }
                }),
            nga.field('active', 'boolean')
                .label('Active')
                .validation({required: true}),
            nga.field('webBookingEndTime', 'datetime')
                .label('Website booking end time')
                .template('' +
                    '<div class="datetime_widget pull-left"><ma-date-field value="value" field="::field" size="xs"></ma-date-field></div>' +
                    '<div class="pull-left btn btn-default ml10">{{entry.values.webBookingEndTime | date:\'yyyy-MM-dd @ h:mma\'}}</div> '),
                // .parse(nga.parseDate),
            nga.field('tbBookingEndTime', 'datetime')
                .label('TB booking end time')
                .template('' +
                    '<div class="datetime_widget pull-left"><ma-date-field value="value" field="::field" size="xs"></ma-date-field></div>' +
                    '<div class="pull-left btn btn-default ml10">{{entry.values.tbBookingEndTime | date:\'yyyy-MM-dd @ h:mma\'}}</div> '),
            // .parse(nga.parseDate),
            nga.field('ticketingActiveTime', 'datetime')
                .label('App ticketing active time')
                .template('' +
                    '<div class="datetime_widget pull-left"><ma-date-field value="value" field="::field" size="xs"></ma-date-field></div>' +
                    '<div class="pull-left btn btn-default ml10">{{entry.values.ticketingActiveTime | date:\'yyyy-MM-dd @ h:mma\'}}</div> '),
            // .parse(nga.parseDate),
            nga.field('seatingProfile', 'reference')
                .label('Seating profile')
                .targetEntity(seatingProfile)
                .targetField(nga.field('name'))
                .editable(true)
                .isDetailLink(false),
            nga.field('loadFactor', 'float')
                .label('Load Factor'),
            nga.field('stage')
                .label('Operational stage')
                .editable(false)
        ]);

    busSchedule.showView()
        .title('Bus schedule #{{ entry.values.id }}')
        .fields([
            nga.field('id')
                .label('ID'),
            busSchedule.editionView().fields()
        ]);
    return this;
};