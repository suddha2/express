<?php


return array(
    'router' => array(
        'routes' => array(

        ),
    ),
    'controllers' => array(
        'invokables' => array(
            'Sltb\Controller\Index' => 'Sltb\Controller\IndexController',
        ),
    ),
    'view_manager' => array(
        'display_not_found_reason' => true,
        'display_exceptions'       => true,
        'doctype'                  => 'HTML5',
        'not_found_template'       => 'error/404',
        'exception_template'       => 'error/index',
        'template_map' => array(
            'layout/sltb'           	=> __DIR__ . '/../view/layout/layout.phtml',
            'error/404'               	=> __DIR__ . '/../view/error/404.phtml',
            'error/index'             	=> __DIR__ . '/../view/error/index.phtml',
        ),
        'template_path_stack' => array(
            __DIR__ . '/../view',
        ),
    ),
    'module_layouts' => array(
        'Sltb' => 'layout/sltb',
    ),
    //site layouts
    'site_layouts' => array(
        \Application\Domain::NAME_SLTB => 'layout/sltb',
    ),
);