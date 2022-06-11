<?php
/**
 * superline.lk specific config overrides
 */
return array(
    'system' => array(
        'serverName' => 'mobitel.express.lk',
        'siteName' => 'mobitel.lk', //name used in branding
        'hotlinePhone' => '365', //phone number used in branding
        'webBookingExpireTime' => (60*60*2)
    ),
    'api' => array(
        'auth' => array(
            'username' => 'bbk',
            'password' => 'vS0bgsikVKSLnQ0JtcuHqie791wOVImF7SFAGpMGgn8T8vKWcZSjhGGzCtrM',
        ),
    ),
    'client-api' => array(
        'mobitel' => array(
            'cgw' => array(
                'wsdl'  => 'http://mapps.mobitel.lk/M_CGW/MCAccMngr?wsdl',
                'username'  => 'busbuser',
                'password'  => 'B3!u$9@',
                'serviceId'  => 'BUSB',
            )
        )
    ),
    'router' => array(
        'routes' => array(
            //override admin routes so mobitel people cannot go in
            'admin' => array(
                'type'    => 'Literal',
                'options' => array(
                    'route'    => '/admin-panel',
                    'defaults' => array(
                        '__NAMESPACE__' => 'Legacy\Controller',
                        'controller'    => 'Ticketbox',
                    ),
                ),
            ),
            'home' => array(
                'type' => 'Zend\Mvc\Router\Http\Literal',
                'options' => array(
                    'route'    => '/',
                    'defaults' => array(
                        'controller' => 'Legacy\Controller\Ticketbox',
                        'action'     => 'index',
                    ),
                ),
                'may_terminate' => true,
                'child_routes' => array(
                    'default' => array(
                        'type'    => 'Segment',
                        'options' => array(
                            'route'    => ':action',
                            'constraints' => array(
                                'controller' => '[a-zA-Z][a-zA-Z0-9_-]*',
                                'action'     => '[a-zA-Z][a-zA-Z0-9_-]*',
                            ),
                            'defaults' => array(
                                'controller' => 'Legacy\Controller\Ticketbox',
                                'action'     => 'index',
                            ),
                        ),
                    ),

                ),
            )
        )
    ),
);