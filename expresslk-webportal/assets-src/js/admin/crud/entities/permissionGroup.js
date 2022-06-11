Entities.permissionGroup = function(nga, permissionGroup, permissionSingle, module) {
	permissionGroup.listView()
        .title('Permission groups')
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

	permissionGroup.creationView()
	    .title('Create permission group')
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
                .permanentFilters({code: 'PERMGROUP'}),
            nga.field('permissions', 'reference_many')
                .label('Permissions')
                .targetEntity(permissionSingle)
                .targetField(nga.field('name'))
                .validation({required: true})
                .remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'name*': search};
                    }
                })
        ]);
    
	permissionGroup.editionView()
        .title('Edit permission group "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            permissionGroup.creationView().fields()
        ]);

	permissionGroup.showView()
        .title('Permission group "{{ entry.values.name }}"')
        .fields([
            nga.field('id')
                .label('ID'),
                permissionGroup.editionView().fields()
        ]);

    return this;
};