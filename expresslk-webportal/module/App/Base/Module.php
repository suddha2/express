<?php
namespace App\Base;

use Zend\ModuleManager\Feature\AutoloaderProviderInterface;

class Module implements AutoloaderProviderInterface
{
    public function getConfig()
    {
        return include __DIR__ . '/config/module.config.php';
    }

    public function getAutoloaderConfig()
    {
        return array(
            'Zend\Loader\StandardAutoloader' => array(
                'namespaces' => array(
                    //change namespace to load sub module
                    __NAMESPACE__ => __DIR__ . '/src/' . str_replace("\\", "/", __NAMESPACE__),
                ),
            ),
        );
    }

    public function getServiceConfig()
    {

        return array(
            'factories' => array(
                //Search service
                'App\Auth' => function ($sm) {
                    return new Auth($sm);
                },
            )
        );
    }
}
