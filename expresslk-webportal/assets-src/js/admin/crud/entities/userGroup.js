/**
 * Created by udantha on 5/4/16.
 */

Entities.userGroup = function(nga, userGroup, permission) {

    userGroup.listView()
        .title('Roles')
        .fields([
            nga.field('code').label('Code'),
            nga.field('name').label('Name')
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

    userGroup.creationView()
        .title('Create role')
        .fields([
            nga.field('code')
                .label('Code')
                .validation({required: true, minLength: 1, maxlength: 50}),
            nga.field('name')
                .label('Name')
                .validation({required: true, minLength: 1, maxlength: 50}),
            nga.field('description', 'text')
                .label('Description')
        ]);

    userGroup.editionView()
        .title('Edit role "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            nga.field('code')
                .label('Code')
                .editable(false),
            nga.field('name')
                .label('Name')
                .validation({required: true, minLength: 1, maxlength: 50}),
            nga.field('description', 'text')
                .label('Description'),
            nga.field('permission', 'reference_many')
                .label('Permissions')
                .targetEntity(permission)
                .targetField(nga.field('name'))
                .singleApiCall(function (ids) {
                    return { 'id[]': ids };
                })
                .remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'name*': search};
                    }
                })
                .perPage(50)
        ]);

    userGroup.showView()
        .title('Role "{{ entry.values.name }}"')
        .actions(['list', 'edit', 'delete'])
        .fields([
            nga.field('id')
                .label('ID'),
            userGroup.editionView().fields()
        ]);

    return this;
};