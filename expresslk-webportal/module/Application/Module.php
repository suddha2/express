<?php
/**
 * Zend Framework (http://framework.zend.com/)
 *
 * @link      http://github.com/zendframework/ZendSkeletonApplication for the canonical source repository
 * @copyright Copyright (c) 2005-2014 Zend Technologies USA Inc. (http://www.zend.com)
 * @license   http://framework.zend.com/license/new-bsd New BSD License
 */

namespace Application;

use Application\Adapter\CoreAuthenticationAdapter;
use Application\Alert\Email;
use Application\Alert\Schedule\ConductorSms;
use Application\Alert\Schedule\PassengerSms;
use Application\Alert\Sms;
use Application\Manager\UserManager;
use Application\Model\Db\BusMobileLocation;
use Application\Model\Db\Schedule;
use Application\Payment\Gateway\DialogEzCash;
use Application\Payment\Gateway\HNBipg;
use Application\Payment\Gateway\MobitelMCash;
use Application\Payment\Gateway\SampathCcV1;
use Application\Payment\Gateway\SampathCcV2;
use Application\Payment\Gateway\PayPalExpressCheckout;
use Application\Sms\Client;
use Application\Util\Alert;
use Application\Util\Language;
use Application\Util\Log;
use Zend\Config\Config;
use Zend\ModuleManager\ModuleEvent;
use Zend\ModuleManager\ModuleManager;
use Zend\Mvc\ModuleRouteListener;
use Zend\Mvc\MvcEvent;
use Zend\Authentication\Adapter\DbTable\CredentialTreatmentAdapter;
use Zend\Authentication\AuthenticationService;
use Zend\Mail\Transport\Smtp as SmtpTransport;
use Zend\Mail\Transport\SmtpOptions;
use Zend\Http\Request as HttpRequest ;
use Zend\Console\Request as ConsoleRequest ;
use Zend\Validator\AbstractValidator;
use Zend\View\Model\JsonModel;
use Zend\View\Model\ModelInterface;

class Module
{
    public function init(ModuleManager $moduleManager)
    {
        $events = $moduleManager->getEventManager();

        // Registering a listener at default priority, 1, which will trigger
        // after the ConfigListener merges config.
        $events->attach(ModuleEvent::EVENT_MERGE_CONFIG, array($this, 'onMergeConfig'));
    }

    public function onBootstrap(MvcEvent $e)
    {
        //set log initialization
        Log::init();

        $eventManager        = $e->getApplication()->getEventManager();
        $moduleRouteListener = new ModuleRouteListener();
        $moduleRouteListener->attach($eventManager);

        $eventManager->attach(MvcEvent::EVENT_ROUTE, array($this, 'loadConfiguration'), 2);
        //set translations
        $eventManager->attach(MvcEvent::EVENT_ROUTE, array($this, 'loadTranslation'));
        //set module layouts
        $e->getApplication()->getEventManager()->getSharedManager()->attach('Zend\Mvc\Controller\AbstractController', 'dispatch', function($e) {
            $controller = $e->getTarget();
            $controllerClass = get_class($controller);
            $moduleNamespace = substr($controllerClass, 0, strpos($controllerClass, '\\'));
            $config = $e->getApplication()->getServiceManager()->get('config');
            //change module layout if exists
            if (isset($config['module_layouts'][$moduleNamespace])) {
                $controller->layout($config['module_layouts'][$moduleNamespace]);
            }
            //set site specific layout, if exists
            $currentDomainName = Domain::getDomain();
            if(isset($config['site_layouts']) && isset($config['site_layouts'][$currentDomainName])){
                $controller->layout($config['site_layouts'][$currentDomainName]);
            }
        }, 100);

        /**
         * Return json formatted string on error/404 etc.. instead of plain html
         */
        // attach the JSON view strategy
        $app      = $e->getTarget();
        $locator  = $app->getServiceManager();
        $view     = $locator->get('ZendViewView');
        $strategy = $locator->get('ViewJsonStrategy');
        $view->getEventManager()->attach($strategy, 100);
        // attach a listener to check for errors
        $eventManager->attach(MvcEvent::EVENT_RENDER, array($this, 'onRenderError'));

        //global settings
        $config = $e->getApplication()->getServiceManager()->get('config');
        $e->getViewModel()->setVariable('display_exceptions', $config['system']['debug']);
        //set configs into view model
        $e->getViewModel()->setVariable('SYSCONFIG', $config);
    }

    public function onMergeConfig(ModuleEvent $e)
    {
        $configListener = $e->getConfigListener();
        /** @var \Zend\Config\Config $config */
        $config         = $configListener->getMergedConfig();

        // Modify the configuration; here, we'll remove a specific key:
        if (PHP_SAPI != 'cli') {
            //site is not called from command line
            $domainName = $_SERVER['HTTP_HOST'];
            //load domain specific configs
            if(isset($config->domain->{$domainName})){
                //get domain's configs
                $domainConfigsMeta = $config->domain->{$domainName};
                if(isset($domainConfigsMeta->site) && isset($domainConfigsMeta->siteConfigName)){
                    //set domain
                    Domain::setDomain($domainConfigsMeta->site);
                    //get all the overriding configs
                    $domainConfigs = include __DIR__ . '/../../config/autoload/domain/'. $domainConfigsMeta->siteConfigName;
                    //merge config into main config
                    $config->merge(new Config($domainConfigs));
                }else{
                    throw new \Exception('Domain configurations are invalid');
                }
            }else{
                //redirect to busbooking

            }
        }else{
            //called from commandline
        }

        // Pass the changed configuration back to the listener:
        $configListener->setMergedConfig($config->toArray());
    }
    /**
     * Basic Configurations
     * @param MvcEvent $e
     */
    public function loadConfiguration(MvcEvent $e)
    {
        $application = $e->getApplication();
        $sm = $application->getServiceManager();
        $sharedManager = $application->getEventManager()->getSharedManager();

        $router = $sm->get('router');
        $request = $sm->get('request');

        //chek if the request is from browser
        if (!($e->getRequest() instanceof ConsoleRequest)) {
            //pass to ACL
            $matchedRoute = $router->match($request);
            if (null !== $matchedRoute) {
                //Configure ACL classes
                /**
                 * @todo this skips on exceptions. Findout why and put a fix
                 */
                $sharedManager->attach('Zend\Mvc\Controller\AbstractActionController', 'dispatch',
                    function ($e) use ($sm) {
                        $sm->get('ControllerPluginManager')->get('Application\Event\Authentication')
                            ->preDispatch($e); //pass to the plugin...
                    }, 2
                );
            }

            //if the response is an options request.
            if ( $e->getRequest()->isOptions() ) {
                // respond to OPTIONS request
                echo json_encode(array());
                die;
            }
        } else {
            //If the request is from console
        }


    }

    /**
     * Set application translations
     * @param MvcEvent $e
     */
    public function loadTranslation(MvcEvent $e)
    {
        /**
         * Set up translatorg
         */
        Language::setLanguage($e);

        $translator = $e->getApplication()->getServiceManager()->get('translator');
        //set translator for forms
        AbstractValidator::setDefaultTranslator(new \Zend\Mvc\I18n\Translator($translator));
    }

    /**
     * Event handler for error events
     * @param MvcEvent $e
     */
    public function onRenderError(MvcEvent $e)
    {
        //var_dump($e->getError());die;
        // must be an error
        if (!$e->isError()) {
            return;
        }
        //die('asdas');

        // Check the accept headers for application/json
        $request = $e->getRequest();
        if (!$request instanceof HttpRequest) {
            return;
        }

        $headers = $request->getHeaders();
        if (!$headers->has('Accept')) {
            return;
        }

        $accept = $headers->get('Accept');
        $match  = $accept->match('application/json');
        if (!$match || $match->getTypeString() == '*/*') {
            // not application/json
            return;
        }

        // make debugging easier if we're using xdebug!
        //ini_set('html_errors', 0);

        // if we have a JsonModel in the result, then do nothing
        $currentModel = $e->getResult();
        if ($currentModel instanceof JsonModel) {
            return;
        }

        // create a new JsonModel - use application/api-problem+json fields.
        $response = $e->getResponse();
        $model = new JsonModel(array(
            "status" => $response->getStatusCode(),
            "title" => $response->getReasonPhrase(),
        ));

        // Find out what the error is
        $exception  = $currentModel->getVariable('exception');

        if ($currentModel instanceof ModelInterface && $currentModel->reason) {
            switch ($currentModel->reason) {
                case 'error-controller-cannot-dispatch':
                    $model->detail = 'The requested controller was unable to dispatch the request.';
                    break;
                case 'error-controller-not-found':
                    $model->detail = 'The requested controller could not be mapped to an existing controller class.';
                    break;
                case 'error-controller-invalid':
                    $model->detail = 'The requested controller was not dispatchable.';
                    break;
                case 'error-router-no-match':
                    $model->detail = 'The requested URL could not be matched by routing.';
                    break;
                default:
                    $model->detail = $currentModel->message;
                    break;
            }
        }

        if ($exception) {
            if ($exception->getCode()) {
                $e->getResponse()->setStatusCode($exception->getCode());
            }
            $e->getResponse()->setReasonPhrase($exception->getMessage());

            $model->error = $exception->getMessage();

            // find the previous exceptions
            $messages = array();
            while ($exception = $exception->getPrevious()) {
                $messages[] = "* " . $exception->getMessage();
            };
            if (count($messages)) {
                $exceptionString = implode("n", $messages);
                $model->messages = $exceptionString;
            }
        }

        // set our new view model
        $model->setTerminal(true);
        $e->setResult($model);
        $e->setViewModel($model);
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
                    __NAMESPACE__ => __DIR__ . '/src/' . __NAMESPACE__,
                ),
            ),
        );
    }

    public function getServiceConfig()
    {

        return array(
            'factories' => array(
                //mails section
                'Mail\Transport\Smtp' => function ($sm) {
                    $config = $sm->get('Config');
                    $transport = new SmtpTransport();
                    $transport->setOptions(new SmtpOptions($config['mail']['transport']['options']));

                    return $transport;
                },
                'Email' => function ($sm) {
                    $mail = new Model\Email($sm);
                    return $mail;
                },
                'Sms' => function ($sm) {
                    $sms = new Client($sm);
                    return $sms;
                },
                //alerts
                'Alert\Sms' => function ($sm) {
                    $sms = new Sms($sm);
                    return $sms;
                },
                'Alert\Email' => function ($sm) {
                    $email = new Email($sm);
                    return $email;
                },
                'Alert\Schedule\PassengerSms' => function ($sm) {
                    $sms = new PassengerSms($sm);
                    return $sms;
                },
                'Alert\Schedule\ConductorSms' => function ($sm) {
                    $sms = new ConductorSms($sm);
                    return $sms;
                },

                //Dialog ez cash payment gateway helpwe class
                'Payment\DialogEzCash' => function ($sm) {
                    $dialog = new DialogEzCash($sm);
                    return $dialog;
                },
                //Mobitel mcash payment gateway helper class
                'Payment\MobitelMCash' => function ($sm) {
                    return new MobitelMCash($sm);
                },
                //Sampath IPG 1 payment gateway helper class
                'Payment\SampathCcV1' => function ($sm) {
                    return new SampathCcV1($sm);
                },
                //Sampath IPG 2 payment gateway helper class
                'Payment\SampathCcV2' => function ($sm) {
                    return new SampathCcV2($sm);
                },
				// People IPG payment gateway helper class
				 'Payment\PeopleIPG' => function ($sm) {
                    return new PeopleIPG($sm);
                },
                //PayPal helper class
                'Payment\PayPalExpressCheckout' => function ($sm) {
                	return new PayPalExpressCheckout($sm);
                },
                //hnb
                'Payment\Hnb' => function ($sm) {
                    return new HNBipg($sm);
                },

                'AuthStorage' => function ($sm) {
                    return new \Zend\Authentication\Storage\Session('Express');
                },
                //authentication service
                'AuthService' => function ($sm) {
                    $authService = new AuthenticationService();
                    $authService->setAdapter(new CoreAuthenticationAdapter($sm));
                    $authService->setStorage($sm->get('AuthStorage'));

                    return $authService;
                },

                //manager classes
                'Manager\UserManager' => function ($sm) {
                    return new UserManager($sm);
                },

                //DB Class
                'Db\Schedule' => function ($sm) {
                    $oSchedule = new Schedule($sm);

                    return $oSchedule;
                },
                'Db\BusMobileLocation' => function ($sm) {
                    return new BusMobileLocation($sm);
                },
                //Send system alerts
                'Application\Util\Alert' => function ($sm) {
                    $oAlert = new Alert($sm);
                    return $oAlert;
                },
                //offer code
                'Application\Discount' => function ($sm) {
                    return new Discount($sm);
                }
            ),
            //mark the dependancy object as shared or create instance every time it gets called
            'shared' => array(
                'Email' => false
            ),
        );
    }
}
