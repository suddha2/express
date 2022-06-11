/**
 * Created by udantha on 9/8/16.
 */
Entities.busBusRoute = function(nga, busBusRoute) {

    var bus = nga.entity(Entities.Name.bus),
        busRoute = nga.entity(Entities.Name.busRoute);

    busBusRoute.listView()
        .title('Bus Routes')
        .fields([
            nga.field('busRoute')
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    busBusRoute.creationView()
        .title('Create new BusRoute for a Bus')
        .fields([
            nga.field('busId', 'reference')
                .label('Bus')
                .validation({required: true})
                .targetEntity(bus)
                .targetField(nga.field('plateNumber'))
                .sortField('plateNumber')
                .sortDir('ASC')
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'plateNumber*': search};
                    }
                }),
            nga.field('busRoute', 'reference')
                .label('Bus Route')
                .validation({required: true})
                .targetEntity(busRoute)
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
            nga.field('loadFactor', 'float')
                .label('Load Factor')
        ]);

    busBusRoute.editionView()
        .title('Edit Bus Route for Bus #{{ entry.values.id }}')
        .actions(['show', 'delete'])
        .fields([
            nga.field('id')
                .label('ID')
                .editable(false),
            busBusRoute.creationView().fields()
        ]);

    busBusRoute.showView()
        .title('Bus Route for Bus #{{ entry.values.id }}')
        .fields([
            nga.field('id')
                .label('ID')
                .editable(false),
            busBusRoute.editionView().fields()
        ]);

    return this;
};