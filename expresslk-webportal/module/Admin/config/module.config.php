<?php
/**
 * Zend Framework (http://framework.zend.com/)
 *
 * @link      http://github.com/zendframework/ZendSkeletonApplication for the canonical source repository
 * @copyright Copyright (c) 2005-2014 Zend Technologies USA Inc. (http://www.zend.com)
 * @license   http://framework.zend.com/license/new-bsd New BSD License
 */

return array(
    'router' => array(
        'routes' => array(
            'admin' => array(
                'type'    => 'Literal',
                'options' => array(
                    'route'    => '/admin-panel',
                    'defaults' => array(
                        '__NAMESPACE__' => 'Admin\Controller',
                        'controller'    => 'Index',
                        //'action'        => 'index',
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
                    'crud' => array(
                        'type'    => 'Segment',
                        'options' => array(
                            'route'    => '/cruds/[:entity][/][:id]',
                            'defaults' => array(
                                'controller' => 'Admin\Controller\Crud',
                            ),
                        ),
                    ),
                    'crud_upload' => array(
                        'type'    => 'Segment',
                        'options' => array(
                            'route'    => '/crud_upload/[:entity][/][:id]',
                            'defaults' => array(
                                'controller' => 'Admin\Controller\Crud',
                                'action' => 'upload',
                            ),
                        ),
                    ),
                ),
            ),
        ),
    ),
    'controllers' => array(
        'invokables' => array(
    		'Admin\Controller\Index' => 'Admin\Controller\IndexController',
    		'Admin\Controller\Ticketbox' => 'Admin\Controller\TicketboxController',
    		'Admin\Controller\TicketboxAjax' => 'Admin\Controller\TicketboxAjaxController',
    		'Admin\Controller\Bus' => 'Admin\Controller\BusController',
    		'Admin\Controller\BusService' => 'Admin\Controller\BusServiceController',
    		'Admin\Controller\BusSchedule' => 'Admin\Controller\BusScheduleController',
    		'Admin\Controller\User' => 'Admin\Controller\UserController',
    		'Admin\Controller\Crud' => 'Admin\Controller\CrudController',
    		'Admin\Controller\Report' => 'Admin\Controller\ReportController',
    		'Admin\Controller\Me' => 'Admin\Controller\MeController',
        ),
    ),
    'view_manager' => array(
        'display_not_found_reason' => true,
        'display_exceptions'       => true,
        'doctype'                  => 'HTML5',
        'not_found_template'       => 'error/404',
        'exception_template'       => 'error/index',
        'template_map' => array(
            'layout/admin' 	        => __DIR__ . '/../view/layout/layout.phtml',
            'layout/admin-print' 	=> __DIR__ . '/../view/layout/print.phtml',
        ),
        'template_path_stack' => array(
            __DIR__ . '/../view',
        ),
        'strategies' => array(
            'ViewJsonStrategy',
        ),
    ),
    'module_layouts' => array(
        'Admin' => 'layout/admin',
    ),
);
