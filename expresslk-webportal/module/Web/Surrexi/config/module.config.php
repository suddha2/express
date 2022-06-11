<?php
return array(
    'router' => array(
        'routes' => array(

        ),
    ),
    'controllers' => array(
        'invokables' => array(
            'Web\Surrexi\Controller\Index' => 'Web\Surrexi\Controller\IndexController',
        ),
    ),
    'view_manager' => array(
        'display_not_found_reason' => true,
        'display_exceptions'       => true,
        'doctype'                  => 'HTML5',
        'not_found_template'       => 'error/404',
        'exception_template'       => 'error/index',
        'template_map' => array(
            'layout/surrexi'           	=> __DIR__ . '/../view/layout/layout.phtml',
        ),
        //for sub modules
        'controller_map' => array(
            'Web\Surrexi' => true,
        ),
        'template_path_stack' => array(
            __DIR__ . '/../view',
        ),
    ),
    'module_layouts' => array(
        'Surrexi' => 'layout/surrexi',
    ),
    //site layouts
    'site_layouts' => array(
        \Application\Domain::NAME_SURREXI => 'layout/surrexi',
    ),
);