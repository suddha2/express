
Entities.agent = function(nga, agent) {
    agent.listView()
        .title('Booking agents')
        .fields([            
            nga.field('name').label('Name')
        ])
        .filters([
            nga.field('name*')
                .label('Name')
                .pinned(true)
                .attributes({'placeholder': 'Filter by name'})
        ])
        .actions(['batch', 'filter', 'create'])
        .listActions(['show', 'edit', 'delete']);

    agent.creationView()
        .fields([
            nga.field('name')
                .label('Name')
                .validation({required: true, minLength: 1, maxlength: 50})
        ]);
    
    agent.editionView()
        .title('Edit booking agent "{{ entry.values.name }}"')
        .actions(['list', 'show', 'delete'])
        .fields([
            agent.creationView().fields()
        ]);

    agent.showView()
        .title('Booking agent "{{ entry.values.name }}"')
        .fields([
            nga.field('id')
                .label('ID'),
            agent.editionView().fields()
        ]);

    return this;
};