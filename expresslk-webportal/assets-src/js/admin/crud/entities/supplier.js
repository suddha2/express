
Entities.supplier = function(nga, supplier, supplierGroup, supplierContactPerson, supplierAccount) {
    supplier.listView()
        .title('Suppliers')
        .fields([            
            nga.field('name').label('Name'),
            nga.field('group', 'reference')
                .targetEntity(supplierGroup)
                .targetField(nga.field('name'))
                .singleApiCall(function (routeIds) {
		            return { 'id[]': routeIds };
		        })
                .isDetailLink(false)
                .label('Supplier group'),
            nga.field('preferredPaymentMode').label('Preferred payment mode')            
        ])
        .filters([
            nga.field('name*')
                .label('Name')
                .pinned(true)
                .attributes({'placeholder': 'Filter by name'}),
            nga.field('group', 'reference')
                .label('Supplier group')
                .targetEntity(supplierGroup)
                .targetField(nga.field('name'))
                .sortField('name')
                .sortDir('ASC')
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    supplier.creationView()
        .fields([
            nga.field('name')
                .label('Name')
                .validation({required: true, minLength: 1, maxlength: 50}),
            nga.field('group', 'reference')
                .label('Supplier group')
                .isDetailLink(false)
                .targetEntity(supplierGroup)
                .targetField(nga.field('name'))
                .sortField('name')
                .sortDir('ASC'),
            nga.field('preferredPaymentMode', 'choice')
                .label('Preferred payment mode')
                .choices([ // List the choice as object literals
                    { label: 'Cash', value: 'Cash' },
                    { label: 'mCash', value: 'mCash' },
                    { label: 'eZCash', value: 'eZCash' },
                    { label: 'Bank transfer', value: 'BankTransfer' },
                    { label: 'Cheque', value: 'Cheque' }
                ])
        ]);
    
    supplier.editionView()
        .title('Edit supplier "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            supplier.creationView().fields(),
            nga.field('contactPersonnel', 'referenced_list')
                .label('Contact personnel')
                .targetEntity(supplierContactPerson)
                .targetReferenceField('supplierId')
                .targetFields([
                    nga.field('person.firstName').label('First name'),
                    nga.field('person.lastName').label('Last name'),
                    nga.field('person.mobileTelephone').label('Mobile telephone')
                ]),
            nga.field('accounts', 'referenced_list')
                .label('Accounts')
                .targetEntity(supplierAccount)
                .targetReferenceField('supplierId')
                .targetFields([
                    nga.field('name').label('Name'),
                    nga.field('bank').label('Bank'),
                    nga.field('branch').label('Branch'),
                    nga.field('number').label('A/C number')
                ])
        ]);

    supplier.showView()
        .title('Supplier "{{ entry.values.name }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            supplier.editionView().fields()
        ]);

    return this;
};