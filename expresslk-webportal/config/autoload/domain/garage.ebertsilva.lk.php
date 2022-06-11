<?php
/**
 * Erbertsilva.lk specific config overrides
 */
return array(
    'system' => array(
        'serverName' => 'garage.ebertsilva.express.lk',
        'siteName' => 'ebertsilva.lk', //name used in branding
        'hotlinePhone' => '0115-116-117', //phone number used in branding
    ),
    'api' => array(
        'auth' => array(
            'username' => 'ebertsilvaweb',
            'password' => 'sltb418',
        ),
    ),
    'sms'   => array(
        'src'    => 'erbertsilva',
    ),
    'router' => array(
        'routes' => array(
            'home' => array(
                'type' => 'Zend\Mvc\Router\Http\Literal',
                'options' => array(
                    'route'    => '/',
                    'defaults' => array(
                        'controller' => 'Ebertsilva\Controller\Index',
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
                                'controller' => 'EbertSilva\Controller\Index',
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
                    'username' => 'Erbertsilva@gmail.com',
                    'password' => 'dltrthaiuijhvsot',
                    'ssl' => 'tls'
                ),
            ),
        ),
        'defaults' => array(
            'from' => array(
                'email' => 'Erbertsilva@gmail.com',
                'name'  => 'Erbertsilva.lk'
            ),
            'replyto' => array(
                'email' => 'Erbertsilva@gmail.com',
                'name'  => 'Erbertsilva.lk'
            ),
        ),
    ),
);