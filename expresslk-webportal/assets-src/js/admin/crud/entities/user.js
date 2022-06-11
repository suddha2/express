/**
 * Created by udantha on 5/4/16.
 */

Entities.user = function(nga, user, accountStatus, userGroup) {

    user.listView()
        .title('User')
        .url('userLight')
        .fields([
            nga.field('username').label('Username'),
            nga.field('firstName').label('First name'),
            nga.field('lastName').label('Last name'),
            nga.field('division').label('Division')
        ])
        .filters([
            nga.field('firstName*')
                .label('First name')
                .attributes({'placeholder': 'Filter by first name'}),
            nga.field('lastName*')
                .label('Last name')
                .attributes({'placeholder': 'Filter by last name'}),
            nga.field('username~')
                .label('Username')
                .attributes({'placeholder': 'Filter by username'})
                .pinned(true)
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    user.creationView()
        .title('Create user')
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
                 .validation({minlength: 10, maxlength: 12}),
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
                 .validation({required: false, minlength: 5, maxlength: 5}),
             nga.field('status', 'reference')
                 .label('Status')
                 .targetEntity(accountStatus)
                 .targetField(nga.field('name'))
                 .validation({required: true})
                 .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'name*': search};
                     }
                 }),
             nga.field('userGroups', 'reference_many')
                 .label('Roles')
                 .targetEntity(userGroup)
                 .targetField(nga.field('name'))
                 .singleApiCall(function (userGroupIds) {
                     return { 'id[]': userGroupIds };
                 })
                 .validation({required: true})
                 .remoteComplete(true, {
                     searchQuery: function(search) {
                         return {'name*': search};
                     }
                 })
        ]);

    user.editionView()
        .title('Edit user "{{ entry.values.firstName }} {{ entry.values.lastName }}"')
        .actions(['list', 'show'])
        .fields([
             user.creationView().fields()
        ]);

    user.showView()
        .title('User "{{ entry.values.firstName }} {{ entry.values.lastName }}"')
        .actions(['list', 'edit'])
        .fields([
            nga.field('id')
                .label('ID'),
            user.editionView().fields()
        ]);

    return this;
};