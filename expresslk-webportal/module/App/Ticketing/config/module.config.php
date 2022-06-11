<?php
return array(
    'router' => array(
        'routes' => array(
            'app-ticketing' => array(
                'type'    => 'Literal',
                'options' => array(
                    'route'    => '/app-api/ticketing/v1',
                    'defaults' => array(
                        '__NAMESPACE__' => 'App\Ticketing\Controller',
                        'controller'    => 'Auth',
                        'action'        => 'index',
                    ),
                ),
                'may_terminate' => true,
                'child_routes' => array(
                    'default' => array(
                        'type'    => 'Segment',
                        'options' => array(
                            'route'    => '/[:controller[/:action]][/]',
                            'constraints' => array(
                                'controller' => '[a-zA-Z][a-zA-Z0-9_-]*',
                                'action'     => '[a-zA-Z][a-zA-Z0-9_-]*',
                            ),
                            'defaults' => array(
                                'action'        => 'index',
                            ),
                        ),
                    ),
                ),
            ),
        ),
    ),
    'controllers' => array(
        'invokables' => array(
            'App\Ticketing\Controller\Auth' => 'App\Ticketing\Controller\AuthController',
            'App\Ticketing\Controller\Ticketbox' => 'App\Ticketing\Controller\TicketboxController',
            'App\Ticketing\Controller\Location' => 'App\Ticketing\Controller\LocationController',
        ),
    ),
    'view_manager' => array(
        'template_map' => array(),
        'controller_map' => array(
            //set up sub folders for sub module
            'App\Ticketing' => true,
        ),
        'template_path_stack' => array(
            __DIR__ . '/../view',
        ),
        // ...
    ),
);