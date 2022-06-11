
Entities.supplierContactPerson = function(nga, contact, person, supplier) {

    contact.listView()
        .title('Contact personnel')
        .fields([
            nga.field('supplierId', 'reference')
                 .label('Supplier')
                 .targetEntity(supplier)
                 .targetField(nga.field('name'))
                 .singleApiCall(function (routeIds) {
		            return { 'id[]': routeIds };
		        }),
            nga.field('person.firstName').label('First name'),
            nga.field('person.lastName').label('Last name'),   
            nga.field('person.mobileTelephone').label('Mobile number')
        ])
        .filters([
            nga.field('supplierId', 'reference')
                .label('Supplier')
                .pinned(true)
                .targetEntity(supplier)
                .targetField(nga.field('name'))
                .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'name*': search};
                     }
                 })
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    contact.creationView()
        .fields([
             nga.field('supplierId', 'reference')
                 .label('Supplier')
                 .validation({required: true})
                 .targetEntity(supplier)
                 .targetField(nga.field('name'))
                 .sortField('name')
                 .sortDir('ASC')
                 .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'name*': search};
                     }
                 }),
            nga.field('person', 'reference')
                .label('Person')
                .validation({required: true})
                .targetEntity(person)
                .targetField(nga.field('fullName'))
                .remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'fullName*': search};
                    }
                }),
            nga.field('owner', 'boolean')
                .label('Owner')
                .validation({required: true}),
            nga.field('primary', 'boolean')
                .label('Primary contact')
                .validation({required: true})
        ]);

    contact.editionView()
        .title('Edit contact person "{{ entry.values.person.firstName }} {{ entry.values.person.lastName }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            contact.creationView().fields()
        ]);

    contact.showView()
        .title('Contact person "{{ entry.values.person.firstName }} {{ entry.values.person.lastName }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            contact.editionView().fields()
        ]);

    return this;
};