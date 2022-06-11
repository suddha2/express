
Entities.supplierAccount = function(nga, supplierAccount, supplier) {
    supplierAccount.listView()
        .title('Accounts')
        .fields([
            nga.field('supplierId', 'reference')
                .label('Supplier')
                .targetEntity(supplier)
                .targetField(nga.field('name'))
                .singleApiCall(function (routeIds) {
		            return { 'id[]': routeIds };
		        }),
            nga.field('name').label('Name')          
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
                 }),
            nga.field('name*')
                .label('Name')
                .attributes({'placeholder': 'Filter by name'})
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    supplierAccount.creationView()
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
            nga.field('name')
                .label('Name')
                .validation({required: true, minLength: 1, maxlength: 100}),
            nga.field('number')
                .label('Account number')
                .validation({required: true, minLength: 5, maxlength: 20}),
            nga.field('bank')
                .label('Bank')
                .validation({required: true, minLength: 1, maxlength: 100}),
            nga.field('branch')
                .label('Branch')
                .validation({required: true, minLength: 1, maxlength: 50}),
            nga.field('isPrimary', 'boolean')
                .label('Primary account')
                .validation({required: true}),
            nga.field('swift')
                .label('Swift')
                .validation({minLength: 1, maxlength: 20}),
            /*nga.field('type', 'choice')
                .label('Account type')
                .choices([ // List the choice as object literals
                    { label: 'Savings', value: 'Savings' },
                    { label: 'Current', value: 'Current' }
                ]),*/
        ]);
    
    supplierAccount.editionView()
        .title('Edit supplier account "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            supplierAccount.creationView().fields()
        ]);

    supplierAccount.showView()
        .title('Supplier account "{{ entry.values.name }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            supplierAccount.editionView().fields()
        ]);

    return this;
};