<?php
return array(
    'router' => array(
        'routes' => array(
            'legacy' => array(
                'type'    => 'Literal',
                'options' => array(
                    'route'    => '/legacy',
                    'defaults' => array(
                        '__NAMESPACE__' => 'Legacy\Controller',
                        'controller'    => 'Ticketbox',
                    ),
                ),
                'may_terminate' => true,
                'child_routes' => array(
                    'default' => array(
                        'type'    => 'Segment',
                        'options' => array(
                            'route'    => '/[:controller[/:action]][/][:id]',
                            'constraints' => array(
                                'controller' => '[a-zA-Z][a-zA-Z0-9_-]*',
                                'action'     => '[a-zA-Z][a-zA-Z0-9_-]*',
                            ),
                            'defaults' => array(
                                'action'        => 'index',
                            ),
                        ),
                    ),
//                    'ticketbox' => array(
//                        'type'    => 'Segment',
//                        'options' => array(
//                            'route'    => '/:key/ticketbox[/:action][/][:id]',
//                            'constraints' => array(
//                                'controller' => '[a-zA-Z][a-zA-Z0-9_-]*',
//                                'action'     => '[a-zA-Z][a-zA-Z0-9_-]*',
//                            ),
//                            'defaults' => array(
//                                'action'        => 'index',
//                                'key'        => false,
//                            ),
//                        ),
//                    ),
                ),
            ),
        ),
    ),
    'controllers' => array(
        'invokables' => array(
            'Legacy\Controller\Ticketbox' => 'Legacy\Controller\TicketboxController',
            'Legacy\Controller\Bookings' => 'Legacy\Controller\BookingsController',
            'Legacy\Controller\User' => 'Legacy\Controller\UserController',
        ),
    ),
    'view_manager' => array(
        'display_not_found_reason' => true,
        'display_exceptions'       => true,
        'doctype'                  => 'HTML5',
        'not_found_template'       => 'legacyerror/404',
        'exception_template'       => 'legacyerror/index',
        'template_map' => array(
            'layout/legacy' 	        => __DIR__ . '/../view/layout/layout.phtml',
            'legacyerror/404'               	=> __DIR__ . '/../view/error/404.phtml',
            'legacyerror/index'             	=> __DIR__ . '/../view/error/index.phtml',
        ),
        'template_path_stack' => array(
            __DIR__ . '/../view',
        ),
        'strategies' => array(
            'ViewJsonStrategy',
        ),
    ),
    'module_layouts' => array(
        'Legacy' => 'layout/legacy',
    ),
);