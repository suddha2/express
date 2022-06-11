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
            'home' => array(
                'type' => 'Zend\Mvc\Router\Http\Literal',
                'options' => array(
                    'route'    => '/',
                    'defaults' => array(
                        'controller' => 'Application\Controller\Index',
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
                                'controller' => 'Application\Controller\Index',
                                'action'     => 'index',
                            ),
                        ),
                    ),
                    'login' => array(
                        'type' => 'Zend\Mvc\Router\Http\Literal',
                        'options' => array(
                            'route'    => 'login',
                            'defaults' => array(
                                'controller' => 'Application\Controller\Auth',
                                'action'     => 'login',
                            ),
                        ),
                    ),
                    //redirect all /bus requests to home page. JS will handle routing
                    'bus' => array(
                        'type' => 'Segment',
                        'options' => array(
                            'route'    => 'bus/:endpoint[/][:fromName][/][:toName][/][:from][/][:to][/][:date][/][:booking]',
                            'defaults' => array(
                                'action'     => 'index',
                            ),
                        ),
                    ),
                    'sitemap' => array(
                        'type' => 'Segment',
                        'options' => array(
                            'route'    => '[:type].xml',
                            'defaults' => array(
                                'action'     => 'sitemap',
                                'type'    => 'sitemap',
                            ),
                        ),
                    ),
                    'offercodes' => array(
                        'type' => 'Segment',
                        'options' => array(
                            'route'    => 'offer/:offerCode',
                            'defaults' => array(
                                'action'     => 'offercode',
                            ),
                        ),
                    ),
                ),
            ),

            //generic route for accessing controllers in application module
            'application' => array(
                'type'    => 'Literal',
                'options' => array(
                    'route'    => '/app',
                    'defaults' => array(
                        '__NAMESPACE__' => 'Application\Controller',
                        'controller'    => 'Index',
                        'action'        => 'index',
                    ),
                ),
                'may_terminate' => true,
                'child_routes' => array(
                    'default' => array(
                        'type'    => 'Segment',
                        'options' => array(
                            'route'    => '/[:controller[/:action]]',
                            'constraints' => array(
                                'controller' => '[a-zA-Z][a-zA-Z0-9_-]*',
                                'action'     => '[a-zA-Z][a-zA-Z0-9_-]*',
                            ),
                            'defaults' => array(
                                'action'     => 'index',
                            ),
                        ),
                    ),
                ),
            ),
        ),
    ),
    'service_manager' => array(
        'abstract_factories' => array(
            'Zend\Cache\Service\StorageCacheAbstractServiceFactory',
            'Zend\Log\LoggerAbstractServiceFactory',
        ),
        'aliases' => array(
            'Zend\Authentication\AuthenticationService' => 'AuthService',
            'translator' => 'MvcTranslator',
        ),
        'factories' => array(
            'navigation' => 'Zend\Navigation\Service\DefaultNavigationFactory',
            'frontMain' => 'Application\Navigation\Service\FrontMain',
        ),
    ),
    'translator' => array(
        'locale' => 'en_US',
        'translation_file_patterns' => array(
            array(
                'type'     => 'gettext',
                'base_dir' => __DIR__ . '/../language',
                'pattern'  => '%s.mo',
            ),
        ),
    ),
    'controllers' => array(
        'invokables' => array(
            'Application\Controller\Index' => 'Application\Controller\IndexController',
            'Application\Controller\Auth' => 'Application\Controller\AuthController',
            'Application\Controller\Search' => 'Application\Controller\SearchController',
            'Application\Controller\Payment' => 'Application\Controller\PaymentController',
            'Application\Controller\Console' => 'Application\Controller\ConsoleController',
            'Application\Controller\Test' => 'Application\Controller\TestController'
        ),
    ),
    'controller_plugins' => array(
        'invokables' => array(
            'Params' => 'Application\Controller\Plugin\Params',
        )
    ),
    'view_manager' => array(
        'display_not_found_reason' => true,
        'display_exceptions'       => true,
        'doctype'                  => 'HTML5',
        'not_found_template'       => 'error/404',
        'exception_template'       => 'error/index',
        'template_map' => array(
            'layout/layout'           	=> __DIR__ . '/../view/layout/layout.phtml',
            'layout/iframe'           	=> __DIR__ . '/../view/layout/layout_iframe.phtml',
            'application/index/index' 	=> __DIR__ . '/../view/application/index/index.phtml',
            'error/404'               	=> __DIR__ . '/../view/error/404.phtml',
            'error/index'             	=> __DIR__ . '/../view/error/index.phtml',
        ),
        'template_path_stack' => array(
            __DIR__ . '/../view',
        ),
        'strategies' => array(
            'ViewJsonStrategy',
        ),
    ),
    // Placeholder for console routes
    'console' => array(
        'router' => array(
            'routes' => array(
                //conductor SMS run per hour
                'cronroute' => array(
                    'options' => array(
                        'route'    => 'alert_conductor_schedule',
                        'defaults' => array(
                            'controller'    => 'Application\Controller\Console',
                            'action'        => 'conductorschedule'
                        )
                    )
                )
            )
        )
    ),
);
