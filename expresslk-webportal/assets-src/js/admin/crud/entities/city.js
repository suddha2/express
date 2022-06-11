/**
 * 
 */

Entities.city = function(nga, city,district) {
	//console.log("City");
    city.listView()
        .title('Cities')
        .fields([
            nga.field('code').label('Code'),
            nga.field('name').label('Name'),
            nga.field('nameSi').label('Name in Sinhala'),
			nga.field('nameTa').label('Name in Tamil'),
			nga.field('district', 'reference')
                 .label('District')
                 .targetEntity(district)
                 .targetField(nga.field('name'))
                 .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'name*': search};
                     }
                 }),
			// nga.field('active', 'boolean')
                // .label('Active')
                // .validation({required: true})
				
			nga.field('active', 'choice')
                 .label('Status')
                 .validation({required: true})
                 .choices([ // List the choice as object literals
                     { label: 'Active', value: 1 },
                     { label: 'Inactive', value: 0 }
                 ])
        ])
        .filters([
            nga.field('code^')
                .label('Code')
                .attributes({'placeholder': 'Filter by code'})
				.pinned(true),
            nga.field('name^')
                .label('Name')
                .pinned(true)
                .attributes({'placeholder': 'Filter by name'}),
			nga.field('district~', 'reference')
                 .label('District')
                 .targetEntity(district)
                 .targetField(nga.field('name'))
                 .validation({required: true})
                 .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'name*': search};
                     }
                 }),
				 
			nga.field('active', 'choice')
                 .label('Status')
                 .validation({required: true})
                 .choices([ // List the choice as object literals
                     { label: 'Active', value: 1 },
                     { label: 'Inactive', value: 0 }
                 ])
        ])
		.sortField('name')
		.sortDir('ASC')
        .actions(['filter', 'create'])
        .listActions([ 'edit', 'delete']);

    city.creationView()
        .fields([
            nga.field('code')
                .label('Code') 
                .validation({required: true, minlength: 2, maxlength: 4}),
            nga.field('name')
                .label('Name')
                .validation({required: true, minLength: 1, maxlength: 50}),
            nga.field('nameSi')
                .label('Name in Sinhala')
                .validation({ minLength: 1, maxlength: 50}),
			nga.field('nameTa')
                .label('Name in Tamil')
                .validation({ minLength: 1, maxlength: 50}),
				
			nga.field('active', 'boolean')
                .label('Active')
				.defaultValue({ eq : true })
                .validation({required: true})
				.choices([ { value: 1, label: 'Enabled' },
					   { value: 0, label: 'Disabled' }
					]),
			
			
			// nga.field('active', 'choice')
                 // .label('Status')
                 // .validation({required: true})
                 // .choices([ // List the choice as object literals
                     // { label: 'Active', value: '1' },
                     // { label: 'Inactive', value: '0' }
                 // ]),
			
			
			nga.field('district', 'reference')
                 .label('District')
                 .targetEntity(district)
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
    
    city.editionView()
        .title('Edit city "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            city.creationView().fields()
        ]);

    city.showView()
        .title('city "{{ entry.values.name }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            city.editionView().fields()
        ]);
	city.deletionView()
        .title('Delete city')
		.description('Are you sure you want to delete "{{ entry.values.name }}" ?');
    return this;
};