/**
 * 
 */

Entities.district = function(nga, district) {
	//console.log("district");
    district.listView()
        .title('District')
        .fields([
            nga.field('code').label('Code'),
            nga.field('name').label('Name')
        ])
        .filters([
            nga.field('code')
                .label('Code')
                .attributes({'placeholder': 'Filter by code'})
				.pinned(true),
            nga.field('name')
                .label('Name')
                .pinned(true)
                .attributes({'placeholder': 'Filter by name'})
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    district.creationView()
        .fields([
            nga.field('code')
                .label('Code') 
                .validation({required: true, minlength: 2, maxlength: 4}),
            nga.field('name')
                .label('Name')
                .validation({required: true, minLength: 1, maxlength: 50})
            
        ]);
    
    district.editionView()
        .title('Edit District "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            district.creationView().fields()
        ]);

    district.showView()
        .title('district "{{ entry.values.name }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            district.editionView().fields()
        ]);

		
    return this;
};