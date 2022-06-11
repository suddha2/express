<?php
$translator = new \Zend\I18n\Translator\Translator();

return array(
    'navigation' => array(
        'default'=> array(
            array(
                'label' => $translator->translate('Home'),
                'route' => 'home',
                'resource'=> 'Application\Controller\Index',
                'target'    => '_self',
                'priority'=> 1.0,
            ),
            array(
                'label' => $translator->translate('Experience'),
                'route' => 'home/default',
                'resource'=> 'Application\Controller\Index',
                'controller'   => 'index',
                'action'    => 'experience',
                'target'    => '_self',
                'priority'=> 0.1,
            ),
            array(
                'label' => $translator->translate('Katha Bus'),
                'route' => 'home/default',
                'resource'=> 'Application\Controller\Index',
                'controller'   => 'index',
                'action'    => 'blog',
                'target'    => '_self',
                'uri' => '/blog',
                'priority'=> 0.2,
            ),
            array(
                'label' => $translator->translate('About'),
                'route' => 'home/default',
                'resource'=> 'Application\Controller\Index',
                'controller'   => 'index',
                'target'    => '_self',
                'action'    => 'aboutus',
                'priority'=> 0.3,
            ),
            array(
                'label' => $translator->translate('Terms and Conditions'),
                'route' => 'home/default',
                'resource'=> 'Application\Controller\Index',
                'controller'   => 'index',
                'action'    => 'terms',
                'target'    => '_self',
                'priority'=> 0.7,
            ),
            array(
                'label' => $translator->translate('Privacy Policy'),
                'route' => 'home/default',
                'resource'=> 'Application\Controller\Index',
                'controller'   => 'index',
                'action'    => 'privacy',
                'target'    => '_self',
                'priority'=> 0.6,
            ),
            array(
                'label' => $translator->translate('FAQ'),
                'route' => 'home/default',
                'resource'=> 'Application\Controller\Index',
                'controller'   => 'index',
                'action'    => 'faq',
                'target'    => '_self',
                'priority'=> 0.5,
            ),
            array(
                'label' => $translator->translate('Contact Us'),
                'route' => 'home/default',
                'resource'=> 'Application\Controller\Index',
                'controller'   => 'index',
                'action'    => 'contact',
                'target'    => '_self',
                'priority'=> 0.4,
            ),
        ),
        
        'frontMain' => array(
            array(
                'label' => $translator->translate('Home'),
                'route' => 'home',
                'target'    => '_self',
                'resource'=> 'Application\Controller\Index',
            ),
            array(
                'label' => $translator->translate('Experience'),
                'route' => 'home/default',
                'resource'=> 'Application\Controller\Index',
                'controller'   => 'index',
                'target'    => '_self',
                'action'    => 'experience'
            ),
            array(
                'label' => $translator->translate('Katha Bus'),
                'route' => 'home/default',
                'resource'=> 'Application\Controller\Index',
                'controller'   => 'index',
                'action'    => 'blog',
                'target'    => '_self',
                'uri' => '/blog',
            ),
            array(
                'label' => $translator->translate('About'),
                'route' => 'home/default',
                'resource'=> 'Application\Controller\Index',
                'controller'   => 'index',
                'target'    => '_self',
                'action'    => 'aboutus'
            ),
            array(
                'label' => $translator->translate('Contact Us'),
                'route' => 'home/default',
                'resource'=> 'Application\Controller\Index',
                'controller'   => 'index',
                'target'    => '_self',
                'action'    => 'contact'
            ),
        ),
    )
);