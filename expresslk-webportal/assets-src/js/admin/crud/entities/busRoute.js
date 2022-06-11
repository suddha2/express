/**
 * 
 */

Entities.busRoute = function(nga, busRoute,city) {
	//console.log("City");
    busRoute.listView()
        .title('Bus Route')
        .fields([
            nga.field('routeNumber').label('Route Number'),
			
            nga.field('displayNumber').label('Display Number'),
			
			nga.field('name').label('Name')
			
			// nga.field('fromCity', 'reference')
                 // .label('From City')
                 // .targetEntity(city)
                 // .targetField(nga.field('name')),
			// nga.field('toCity', 'reference')
                 // .label('To City')
                 // .targetEntity(city)
                 // .targetField(nga.field('name')), 
			// nga.field('genderRequired', 'boolean')
                // .label('Gender Required')
                // .validation({required: true})
				// .choices([ { value: true, label: 'Enabled' },
					   // { value: false, label: 'Disabled' }
					// ]),
        ])
        .filters([
            nga.field('name')
                .label('Name')
                .pinned(true)
                .attributes({'placeholder': 'Filter by name'})
				,
				
			nga.field('routeNumber')
				.label('Route Number')
				.attributes({'placeholder': 'Filter by Route Number'}),
			
			nga.field('fromCity', 'reference')
                 .label('From City')
                 .targetEntity(city)
                 .targetField(nga.field('name'))
                 .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'name*': search};
                     }
                 }),
			nga.field('toCity', 'reference')
                 .label('To City')
                 .targetEntity(city)
                 .targetField(nga.field('name'))
                 .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'name*': search};
                     }
                 })
			
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    busRoute.creationView()
        .fields([
            nga.field('routeNumber').label('Route Number'),
            nga.field('displayNumber').label('Display Number'),
			nga.field('name').label('Name'),
			nga.field('fromCity', 'reference')
                 .label('From City')
                 .targetEntity(city)
                 .targetField(nga.field('name'))
				 .sortField('name')
				 .sortDir('ASC'),
			nga.field('toCity', 'reference')
                 .label('To City')
                 .targetEntity(city)
                 .targetField(nga.field('name'))
				 .sortField('name')
				 .sortDir('ASC'), 
			nga.field('genderRequired', 'boolean')
                .label('Gender Required')
                .validation({required: true})
				.choices([ { value: true, label: 'Enabled' },
					   { value: false, label: 'Disabled' }
					]),
			
        ]);
    
    busRoute.editionView()
        .title('Edit Route "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            busRoute.creationView().fields()
        ]);

    busRoute.showView()
        .title('Route "{{ entry.values.name }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            busRoute.editionView().fields()
        ]);

		
    return this;
};