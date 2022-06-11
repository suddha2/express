/**
 * Created by udantha on 6/28/15.
 */

Entities.bus = function(nga, bus, amenity, busRoute, busType, conductor, driver, supplier, travelClass, busImage, seatingProfile) {

    /**
     * Entity object imports. Lets define those here
     */
    var busBusRoute = nga.entity(Entities.Name.busBusRoute);

    bus.listView()
        .title('Busses')
        .url('busLight')
        .fields([
            nga.field('plateNumber').label('Plate number'),
            nga.field('name').label('Name')
        ])
        .filters([
            nga.field('plateNumber*')
                .label('Plate number')
                .pinned(true)
                .attributes({'placeholder': 'Filter by plate number'}),
            nga.field('name*')
                .label('Name')
                .attributes({'placeholder': 'Filter by name'})
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    bus.creationView()
        .fields([
            nga.field('name')
                .label('Name')
                .validation({required: true, minLength: 1, maxlength: 50}),
            nga.field('plateNumber')
                .label('Plate number')
                .attributes({placeholder: 'Eg: NA-0000'})
                .validation({required: true, minLength: 6, maxlength: 50}),
            nga.field('busType', 'reference')
                .label('Bus type')
                .isDetailLink(false)
                .validation({required: true})
                .targetEntity(busType)
                .targetField(nga.field('type'))
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .sortField('type')
                .sortDir('ASC'),
            nga.field('travelClass', 'reference')
                .label('Travel class')
                .isDetailLink(false)
                .validation({required: true})
                .targetEntity(travelClass)
                .targetField(nga.field('name'))
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .sortField('name')
                .sortDir('ASC'),
            nga.field('supplier', 'reference')
                .label('Supplier')
                .validation({required: true})
                .targetEntity(supplier)
                .targetField(nga.field('name'))
                //.detailLinkRoute('show')
                .sortField('name')
                .sortDir('ASC')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                }),
                // .remoteComplete(true, {
                    // searchQuery: function(search) {
                        // return {'name*': search};
                    // }
                // }),
            nga.field('contact')
                .label('Contact number (conductor SMS sent to this)')
                .validation({minLength: 9, maxlength: 10}),
            nga.field('adminContact')
                .label('Admin contact number (conductor SMS sent to this)')
                .validation({minLength: 9, maxlength: 10}),
            nga.field('notificationMethod', 'choice')
                .label('NotificationMethod')
                .validation({required: true})
                .choices([ // List the choice as object literals
                    { label: 'SMS', value: 'Sms' },
                    { label: 'Call', value: 'Call' },
                    { label: 'App', value: 'App' },
                    { label: 'Printout', value: 'Printout' }
                ]),
            nga.field('driver', 'reference')
                .label('Usual driver')
                .validation({required: true})
                .targetEntity(driver)
                .targetField(nga.field('fullName'))
                .detailLinkRoute('show')
                .sortField('fullName')
                .sortDir('ASC')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'fullName*': search};
                    }
                }),
            nga.field('conductor', 'reference')
                .label('Usual conductor')
                .validation({required: true})
                .targetEntity(conductor)
                .targetField(nga.field('fullName'))
                //.detailLinkRoute('show')
                .sortField('fullName')
                .sortDir('ASC')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                }),
                // .remoteComplete(true, {
                    // searchQuery: function(search) {
                        // return {'fullName*': search};
                    // }
                // }),
            nga.field('amenities', 'reference_many')
                .label('Amenities')
                .isDetailLink(false)
                .targetEntity(amenity)
                .targetField(nga.field('name'))
                .singleApiCall(function (amenityIds) {
                    return { 'id[]': amenityIds };
                })
                .sortField('name')
                .sortDir('ASC')
                .remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'name*': search};
                    }
                }),
            nga.field('cashOnDepartureAllowed', 'boolean')
                .label('Allow cash on departure'),
            nga.field('permitNumber')
                .label('Permit number')
                .validation({maxlength: 20}),
            nga.field('permitIssueDate', 'date')
                .label('Permit issue date')
                .format(nga.dateFormat)
                .parse(nga.parseDate),
            nga.field('permitExpiryDate', 'date')
                .label('Permit expiry date')
                .format(nga.dateFormat)
                .parse(nga.parseDate),
            nga.field('seatingProfile', 'reference')
                .label('Default seating profile')
                .targetEntity(seatingProfile)
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
                }),
            nga.field('busBusRoute', 'referenced_list')
                .label('Bus Routes')
                .targetEntity(busBusRoute)
                .targetReferenceField('busId')
                .targetFields([
                    nga.field('busId'),
                    nga.field('busRoute', 'reference')
                        .targetEntity(busRoute)
                        .targetField(nga.field('name'))
                        .singleApiCall(function (ids) {
                            return { 'id[]': ids };
                        }),
                    nga.field('loadFactor'),
                    //edit button
                    nga.field('', 'template')
                        .label('Actions')
                        .template('' +
                            '<ma-edit-button entry="entry" entity-name="busBusRoute" size="xs"></ma-edit-button>' +
                            '<ma-delete-button entry="entry" entity-name="busBusRoute" size="xs" ></ma-delete-button>')
                ])
                .sortField('id')
                .sortDir('DESC')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                }),
            nga.field('', 'template')
                .label('')
                .template('<ma-create-button entity-name="busBusRoute" size="sm" label="Add Bus Route" default-values="{ busId: entry.values.id }"></ma-create-button>'),
            nga.field('secondsBeforeTicketingActive', 'number')
                .label('Ticketing activation lead time (in seconds)'),
            nga.field('secondsBeforeTBEnd', 'number')
                .label('Ticketbox booking close lead time (in seconds)'),
            nga.field('secondsBeforeWebEnd', 'number')
                .label('Website ticketing close lead time (in seconds)')
        ]);

    bus.editionView()
        .title('Edit bus "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            bus.creationView().fields()
        ]);

    bus.showView()
        .title('Bus "{{ entry.values.name }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            bus.editionView().fields(),
            nga.field('images', 'referenced_list')
                .label('Images')
                .targetEntity(busImage)
                .targetReferenceField('busId')
                .targetFields([
                    nga.field('type')
                        .label('Type'),
                       nga.field('image', 'template')
                        .label('Image')
                        .template('<img ng-src="data:image/png;base64,{{entry.values.image}}" style="max-height: 60px;"/>'),
                    nga.field('', 'template')
                        .label('Actions')
                        .template('<ma-show-button entry="::entry" entity="::entity" size="xs"></ma-show-button>'
                                + '<ma-edit-button ng-if="::entity.editionView().enabled" entry="::entry" entity="::entity" size="xs"></ma-edit-button>'
                                + '<ma-delete-button ng-if="::entity.deletionView().enabled" entry="::entry" entity="::entity" size="xs"></ma-delete-button>')
                ]),
            nga.field('', 'template')
                .label('')
                .template('<ma-create-button entity-name="busImage" size="sm" label="Add image" default-values="{ busId: entry.values.id }"></ma-create-button>'),

        ]);

    return this;
};