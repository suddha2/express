/**
 * Created by udantha on 6/28/15.
 */

Entities.conductor = function(nga, conductor, person){

    conductor.listView()
        .title('Conductors')
        .fields([
            nga.field('person.firstName').label('First name'),
            nga.field('person.lastName').label('Last name'),
            nga.field('ntcRegistrationNumber').label('NTC registration'),            
            nga.field('person.mobileTelephone').label('Mobile number')
        ])
        .filters([
            nga.field('ntcRegistrationNumber~')
                .label('NTC registration')
                .attributes({'placeholder': 'Filter by NTC registration'})
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    conductor.creationView()
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
        ]);

    conductor.editionView()
        .title('Edit conductor "{{ entry.values.person.firstName }} {{ entry.values.person.lastName }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            conductor.creationView().fields()
        ]);

    conductor.showView()
    	.title('Conductor "{{ entry.values.person.firstName }} {{ entry.values.person.lastName }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            conductor.editionView().fields()
        ]);

    return this;
};