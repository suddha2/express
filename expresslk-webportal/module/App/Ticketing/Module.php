<?php
namespace App\Ticketing;

use App\Ticketing\Entity\BookingEntity;
use App\Ticketing\Entity\ScheduleEntity;
use App\Ticketing\Model\Auth;
use App\Ticketing\Schedule\Availability;
use Application\Util\Language;
use Zend\ModuleManager\Feature\AutoloaderProviderInterface;
use Zend\Mvc\MvcEvent;

class Module implements AutoloaderProviderInterface
{
    public function onBootstrap(MvcEvent $e)
    {

        $application   = $e->getApplication();
        $sm            = $application->getServiceManager();

        $router = $sm->get('router');
        $request = $sm->get('request');
        $matchedRoute = $router->match($request);

        if (null !== $matchedRoute) {
            $params = $matchedRoute->getParams();
            $namespace = isset($params['__NAMESPACE__'])? $params['__NAMESPACE__'] : $params['controller'];

            //Check if this is the current module and controller is from this module
            if(substr($namespace, 0, strlen(__NAMESPACE__))==__NAMESPACE__){
                $this->_onlyForThisModule($e);
            }
        }

    }

    /**
     * @param MvcEvent $e
     */
    private function _onlyForThisModule($e)
    {
        $eventManager        = $e->getApplication()->getEventManager();
        /**
         * This is for translations for mobile apps
         * set to load translations from HTTP header always
         */
        $eventManager->attach(MvcEvent::EVENT_ROUTE, function ($e){
            //get language from header for apps
            $lang = Language::getLanguageFromHeader();
            Language::setLanguage($e, $lang);
        });
    }

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
                //Booking object
                'App\Ticketing\V1\Booking' => function ($sm) {
                    return new Booking($sm);
                },
                'App\Ticketing\V1\Schedule' => function ($sm) {
                    return new Schedule($sm);
                },
                'App\Ticketing\V1\Schedule\Availability' => function ($sm) {
                    return new Availability($sm);
                },
                //Entity objects
                'App\Ticketing\V1\Entity\Booking' => function ($sm) {
                    return new BookingEntity($sm);
                },
                'App\Ticketing\V1\Entity\Schedule' => function ($sm) {
                    return new ScheduleEntity($sm);
                },
                
                //serializer object
                'App\Ticketing\V1\Serializer' => function ($sm) {
                    return new Serializer($sm);
                },
            )
        );
    }
}
