<?php
/**
 * superline.lk specific config overrides
 */
return array(
    'system' => array(
        'serverName' => 'superline.express.lk',
        'siteName' => 'Superline.lk', //name used in branding
        'hotlinePhone' => '0115-116-117', //phone number used in branding
        'webBookingExpireTime' => (60*60*2)
    ),
    'api' => array(
        'auth' => array(
            'username' => 'spl',
            'password' => 'HQ7ks1j0tkhNHgwVyjTeHBaJlNYF0MOax6NAokCjAPFw5Js8Z8JldDlIDNBj',
        ),
    ),
    'sms'   => array(
        'src'    => 'Superline',
    ),
    'router' => array(
        'routes' => array(
            'home' => array(
                'type' => 'Zend\Mvc\Router\Http\Literal',
                'options' => array(
                    'route'    => '/',
                    'defaults' => array(
                        'controller' => 'Superline\Controller\Index',
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
                                'controller' => 'Superline\Controller\Index',
                                'action'     => 'index',
                            ),
                        ),
                    ),

                ),
            )
        )
    ),
);