<?php
/**
 * surrexi.lk specific config overrides
 */
return array(
    'system' => array(
        'serverName' => 'surrexi.express.lk',
        'siteName' => 'Surrexi.lk', //name used in branding
        'hotlinePhone' => '077 256 256 1', //phone number used in branding
        'webBookingExpireTime' => (60*60*2)
    ),
    'api' => array(
        'auth' => array(
            'username' => 'srx_web',
            'password' => 'jeGXTEemnUBL5IOJ4mBsZsmImuDxufBDNxw5lqwnPku6nzjYYkztcYkLFkmB',
        ),
    ),
    'sms'   => array(
        'src'    => 'Surrexi',
    ),
    'router' => array(
        'routes' => array(
            'home' => array(
                'type' => 'Zend\Mvc\Router\Http\Literal',
                'options' => array(
                    'route'    => '/',
                    'defaults' => array(
                        'controller' => 'Web\Surrexi\Controller\Index',
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
                                'controller' => 'Web\Surrexi\Controller\Index',
                                'action'     => 'index',
                            ),
                        ),
                    ),

                ),
            )
        )
    ),
    //change email sender
    'mail' => array(
        'transport' => array(
            'options' => array(
                'host'              => 'smtp.gmail.com',
                'connection_class'  => 'plain',
                'port' => 587,
                'connection_config' => array(
                    'username' => 'surrexilk@gmail.com',
                    'password' => 'yhtkmtnepvffxzbl',
                    'ssl' => 'tls'
                ),
            ),
        ),
        'defaults' => array(
            'from' => array(
                'email' => 'surrexilk@gmail.com',
                'name'  => 'Surrexi.lk'
            ),
            'replyto' => array(
                'email' => 'surrexilk@gmail.com',
                'name'  => 'Surrexi.lk'
            ),
        ),
    ),
);