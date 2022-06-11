/**
 * 
 */

Entities.busStop = function(nga, busStop,city) {
	//console.log("City");
    busStop.listView()
        .title('Stations & Stops')
        .fields([
            nga.field('name').label('Name'),
            nga.field('nameSi').label('Name in Singhala'),
			nga.field('nameTa').label('Name in Tamil'),
			nga.field('city', 'reference')
                 .label('City')
                 .targetEntity(city)
                 .targetField(nga.field('name'))
                 .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'name*': search};
                     }
                 })
        ])
        .filters([
            nga.field('name')
                .label('Name')
                .pinned(true)
                .attributes({'placeholder': 'Filter by name'}),
			nga.field('city', 'reference')
                 .label('City')
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

    busStop.creationView()
        .fields([
            nga.field('name')
                .label('Name')
                .validation({required: true, minLength: 1, maxlength: 50}),
            nga.field('nameSi')
                .label('Name in Singhala')
                .validation({ minLength: 1, maxlength: 50}),
			nga.field('nameTa')
                .label('Name in Tamil')
                .validation({ minLength: 1, maxlength: 50}),
			
			nga.field('city', 'reference')
                 .label('City')
                 .targetEntity(city)
                 .targetField(nga.field('name'))
				 .sortField('name')
				 .sortDir('ASC')
                 .validation({required: true}),
				 
                 // .remoteComplete(true, {
                     // searchQuery: function(search) {
                         // return {'name*': search};
                     // }
                 // }),
			
        ]);
    
    busStop.editionView()
        .title('Edit Stops "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            busStop.creationView().fields()
        ]);

    busStop.showView()
        .title('Stop "{{ entry.values.name }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            busStop.editionView().fields()
        ]);

		
    return this;
};