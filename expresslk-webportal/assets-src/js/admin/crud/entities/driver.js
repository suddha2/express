/**
 * Created by udantha on 6/28/15.
 */

Entities.driver = function(nga, driver, person){

    driver.listView()
        .title('Drivers')
        .fields([
            nga.field('person.firstName').label('First name'),
            nga.field('person.lastName').label('Last name'),
            nga.field('ntcRegistrationNumber').label('NTC registration'),
            nga.field('drivingLicence').label('Driving licence'),    
            nga.field('person.mobileTelephone').label('Mobile number')
        ])
        .filters([
            nga.field('ntcRegistrationNumber~')
                .label('NTC registration')
                .attributes({'placeholder': 'Filter by NTC registration'}),
            nga.field('drivingLicence~')
                .label('Driving licence')
                .attributes({'placeholder': 'Filter by driving licence'})
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    driver.creationView()
        .fields([
            nga.field('person', 'reference')
                .label('Person')
                .validation({required: true})
                .targetEntity(person)
                .targetField(nga.field('fullName'))
                .detailLinkRoute('show')
                .remoteComplete(true, {
                    searchQuery: function(search) {
                        return {'fullName*': search};
                    }
                }),
            nga.field('nickName')
                .label('Nickname')
                .validation({minlength: 1, maxlength: 50}),
            nga.field('ntcRegistrationNumber')
                .label('NTC registration')
                .validation({minlength: 3, maxlength: 20}),
            nga.field('ntcRegistrationExpiryDate', 'date')
                .label('NTC registration expiry date')
                .format(nga.dateFormat)
                .parse(nga.parseDate),
            nga.field('drivingLicence')
                .label('Driving licence')
                .validation({minlength: 5, maxlength: 12}),
            nga.field('drivingLicenceIssueDate', 'date')
                .label('Driving licence issue date')
                .format(nga.dateFormat)
                .parse(nga.parseDate),
            nga.field('drivingLicenceExpiryDate', 'date')
                .label('Driving licence expiry date')
                .format(nga.dateFormat)
                .parse(nga.parseDate)
        ]);

    driver.editionView()
        .title('Edit driver "{{ entry.values.person.firstName }} {{ entry.values.person.lastName }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            driver.creationView().fields()
        ]);

    driver.showView()
        .title('Driver "{{ entry.values.person.firstName }} {{ entry.values.person.lastName }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            driver.editionView().fields()
        ]);

    return this;
};