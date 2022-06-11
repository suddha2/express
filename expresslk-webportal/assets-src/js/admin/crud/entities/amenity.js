/**
 * Created by udantha on 6/28/15.
 */

Entities.amenity = function(nga, amenity) {
    amenity.listView()
        .title('Amenities')
        .fields([
            nga.field('code').label('Code'),
            nga.field('name').label('Name'),
            nga.field('description').label('Description')            
        ])
        .filters([
            nga.field('code~')
                .label('Code')
                .attributes({'placeholder': 'Filter by code'}),
            nga.field('name*')
                .label('Name')
                .pinned(true)
                .attributes({'placeholder': 'Filter by name'})
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    amenity.creationView()
        .fields([
            nga.field('code')
                .label('Code') 
                .validation({required: true, minlength: 2, maxlength: 4}),
            nga.field('name')
                .label('Name')
                .validation({required: true, minLength: 1, maxlength: 50}),
            nga.field('description', 'text')
                .label('Description')
                .validation({maxlength: 100})
        ]);
    
    amenity.editionView()
        .title('Edit amenity "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            amenity.creationView().fields()
        ]);

    amenity.showView()
        .title('Amenity "{{ entry.values.name }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            amenity.editionView().fields()
        ]);

    return this;
};