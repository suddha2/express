
Entities.busImage = function(nga, busImage, bus) {

    busImage.creationView()
        .title('Create new bus image')
        .fields([
             nga.field('busId', 'reference')
                .label('Bus')
                .validation({required: true})
                .targetEntity(bus)
                .targetField(nga.field('plateNumber'))
                .sortField('plateNumber')
                .sortDir('ASC')
                .remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'plateNumber*': search};
                    }
                }),
            nga.field('type', 'choice')
                .label('Type')
                .choices([
                    { label: 'Thumbnail', value: 'Thumbnail' },
                    { label: 'Main', value: 'Main' },
                    { label: 'Other', value: 'Other' }
                ]),
            nga.field('image', 'file')
                .uploadInformation({
                    'url': '/admin-panel/crud_upload/'+ busImage.name(),
                    'data': {
                        'field_name': 'image'
                    }
                })
                .label('Image')
                .validation({required: true}) 
        ]);
    
    busImage.editionView()
        .title('Edit bus image #{{ entry.values.id }}')
        .actions(['show', 'delete'])
        .fields([
            nga.field('busId', 'reference')
                .label('Bus')
                .targetEntity(bus)
                .targetField(nga.field('plateNumber'))
                .editable(false),
            nga.field('type', 'choice')
                .label('Type')
                .choices([
                    { label: 'Thumbnail', value: 'Thumbnail' },
                    { label: 'Main', value: 'Main' },
                    { label: 'Other', value: 'Other' }
                ]),
            nga.field('image', 'template')
                .label('Image')
                .template('<img ng-src="data:image/png;base64,{{entry.values.image}}"/>')
        ]);
    
    busImage.showView()
        .title('Bus image #{{ entry.values.id }}')
        .fields([
            nga.field('id')
                .label('ID'),
            busImage.editionView().fields(),
        ]);
    
    return this;
};