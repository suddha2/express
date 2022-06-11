/**
 * Created by udantha on 9/17/16.
 */

Entities.client = function(nga, client) {

    var user = nga.entity(Entities.Name.user);

    client.listView()
        .title('Client')
        .fields([
            nga.field('name').label('Name'),
            nga.field('email').label('Email'),
            nga.field('mobileTelephone').label('Phone')
        ])
        .actions(['batch', 'filter'])
        .listActions(['show', 'edit']);

    client.editionView()
        .title('Edit client "{{ entry.values.name }}"')
        .actions([])
        .fields([
            nga.field('id')
                .editable(false)
                .label('ID'),
            nga.field('name')
                .label('Name')
                .validation({required: true, minlength: 1, maxlength: 50}),
            nga.field('nic')
                .label('NIC')
                .attributes({placeholder: 'Eg: 123456789V'})
                .validation({minlength: 10, maxlength: 12}),
            nga.field('email')
                .label('E-mail')
                .validation({required: false, minlength: 3, maxlength: 75}),
            nga.field('mobileTelephone')
                .label('Mobile telephone')
                .validation({required: false, minlength: 9, maxlength: 10}),
            nga.field('user', 'reference')
                .label('Linked User')
                .targetEntity(user)
                .targetField(nga.field('firstName'))
                .editable(false)

        ]);

    client.showView()
        .title('Client "{{ entry.values.name }}"')
        .actions([])
        .fields([
            user.editionView().fields()
        ]);

    return this;
};