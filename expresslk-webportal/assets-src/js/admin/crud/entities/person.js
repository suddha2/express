/**
 * Created by udantha on 6/28/15.
 */

Entities.person = function(nga, person) {

    person.listView()
        .title('People')
        .fields([
            nga.field('firstName').label('First name'),
            nga.field('lastName').label('Last name'),
            nga.field('nic').label('NIC'),
            nga.field('email').label('E-mail')
        ])
        .filters([
            nga.field('firstName*')
                .label('First name')
                .pinned(true)
                .attributes({'placeholder': 'Filter by first name'}),
            nga.field('lastName*')
                .label('Last name')
                .pinned(true)
                .attributes({'placeholder': 'Filter by last name'}),
            nga.field('nic~')
                .label('NIC')
                .attributes({'placeholder': 'Filter by NIC'})
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    person.creationView()
        .fields([
            nga.field('firstName')
                .label('First name')
                .validation({required: true, minlength: 1, maxlength: 50}),
            nga.field('middleName')
                .label('Middle name')
                .validation({required: false, minlength: 1, maxlength: 50}),
            nga.field('lastName')
                .label('Last name')
                .validation({required: false, minlength: 1, maxlength: 50}),
            nga.field('nic')
                .label('NIC')
                .attributes({placeholder: 'Eg: 123456789V'})
                .validation({required: false, minlength: 10, maxlength: 12}),
            nga.field('gender', 'choice')
                .label('Gender')
                .validation({required: false})
                .choices([ // List the choice as object literals
                    { label: 'Male', value: 'Male' },
                    { label: 'Female', value: 'Female' },
                    { label: 'Other', value: 'Other' }
                ]),            
            nga.field('dob', 'date')
                .label('DoB')
                .format(nga.dateFormat)
                .parse(nga.parseDate)
                .attributes({placeholder: 'Eg: 1975-02-25'}),
            nga.field('email')
                .label('E-mail')
                .validation({required: false, minlength: 3, maxlength: 75}),
            nga.field('mobileTelephone')
                .label('Mobile telephone')
                .validation({required: false, minlength: 9, maxlength: 10}),
            nga.field('homeTelephone')
                .label('Land telephone')
                .validation({required: false, minlength: 8, maxlength: 20}),
            nga.field('workTelephone')
                .label('Work telephone')
                .validation({required: false, minlength: 8, maxlength: 20}),
            nga.field('houseNumber')
                .label('House number')
                .validation({required: false, minlength: 1, maxlength: 100}),
            nga.field('street')
                .label('Street')
                .validation({required: false, minlength: 1, maxlength: 100}),
            nga.field('city')
                .label('City')
                .validation({required: false, minlength: 1, maxlength: 30}),
            nga.field('postalCode', 'number')
                .format('#####')
                .label('Postal code')
                .validation({required: false, minlength: 5, maxlength: 5})
        ]);

    person.editionView()
        .title('Edit person "{{ entry.values.firstName }} {{ entry.values.lastName }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            person.creationView().fields()
        ]);

    person.showView()
        .title('Person "{{ entry.values.firstName }} {{ entry.values.lastName }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            person.editionView().fields()
        ]);

    return this;
};