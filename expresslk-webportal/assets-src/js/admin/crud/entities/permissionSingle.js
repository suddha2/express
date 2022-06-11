Entities.permissionSingle = function(nga, permissionSingle, module) {
    permissionSingle.listView()
        .title('Permissions')
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

    permissionSingle.creationView()
        .title('Create permission')
        .fields([
            nga.field('code')
                .label('Code') 
                .validation({required: true, minlength: 2, maxlength: 100}),
            nga.field('name')
                .label('Name')
                .validation({required: true, minLength: 1, maxlength: 50}),
            nga.field('description', 'text')
                .label('Description')
                .validation({maxlength: 100}),
            nga.field('module', 'reference')
                .label('Module')
                .validation({required: true})
                .targetEntity(module)
                .targetField(nga.field('name'))
                .sortField('name')
                .sortDir('ASC')
                .remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'name*': search};
                    }
                })
        ]);
    
    permissionSingle.editionView()
        .title('Edit permission "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            permissionSingle.creationView().fields()
        ]);

    permissionSingle.showView()
        .title('Permission "{{ entry.values.name }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            permissionSingle.editionView().fields()
        ]);

    return this;
};