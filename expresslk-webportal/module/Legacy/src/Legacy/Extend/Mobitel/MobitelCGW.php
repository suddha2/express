<?php


namespace Legacy\Extend\Mobitel;


use Legacy\Ticketing\Session;
use Zend\Mvc\Controller\Plugin\AbstractPlugin;
use Zend\Mvc\ModuleRouteListener;
use Zend\Mvc\MvcEvent;

class MobitelCGW extends AbstractPlugin
{
    const PARAM_MSISDN = '_MCGWT_MSISDN';
    const PARAM_AGENT_ID = '_MCGWT_ID';
    const PARAM_AGENT_TOKEN = '_MCGWT_TOKEN';
    const PARAM_SERVICE = '_LEG_SERVICE';
    const PARAM_SERVICE_VALUE = '_MCGWT_';

    /**
     * Initialize and set up procedures for Mobitel CGW
     * @param MvcEvent $e
     */
    public function init(MvcEvent $e)
    {
        $oApplication = $e->getApplication();
        $routeMatch = $e->getRouteMatch();
        $aParams = $routeMatch->getParams();
        $sNamespace = $aParams[ModuleRouteListener::MODULE_NAMESPACE];
        $moduleNamespace = substr($sNamespace, 0, strpos($sNamespace, '\\'));

        //break if this is not loaded in Legacy module
        if(strtolower($moduleNamespace)!=='legacy'){
            return;
        }

        //validate the IP if debug is false. If not from mobitel, throw exception

        //check the URL params for key parameter. If it doesn't exist generate one.
//        $mKey = $routeMatch->getParam('key', false);
//        if(!$mKey || strlen($mKey)<4){
//            //generate key parameter
//            $aParams['key'] = uniqid();
//            //redirect to the same url
//
//            $router = $e->getRouter();
//            $url    = $router->assemble($aParams, array('name' => 'legacy/ticketbox'));
//            header('Location: '. $url, true, 302);
//            exit;
//        }

        //extract post parameters from mobitel. Save them in session
        $aPost = $_GET; //using $_POST as request object is not yet resolved at this point
        if(isset($aPost[self::PARAM_SERVICE]) && $aPost[self::PARAM_SERVICE]==self::PARAM_SERVICE_VALUE){
            $sPhoneNumber = isset($aPost[self::PARAM_MSISDN])? $aPost[self::PARAM_MSISDN] : false;

            //validate phone number
            if($sPhoneNumber==false){
                throw new \Exception('Customer phone number is incorrect');
            }

            //save MSISDN (phone number) in session
            $oProvider = new MobitelCustomerProvider();
            $oProvider->setPhoneNumber($sPhoneNumber);
            /** @var Session $oSession */
            $oSession = $oApplication->getServiceManager()->get('Legacy\Ticketing\Session');
            //initialize session
            $oSession->initSession();
            $oSession->setProvider($oProvider);

            //redirect to clear out the parameters
            $router = $e->getRouter();
            $url    = $router->assemble(array('controller'=> 'Ticketbox'), array('name' => 'legacy/default'));
            header('Location: '. $url, true, 302);
            exit;
        }
    }

    private function validateUser($sUsername, $sToken)
    {
        //check for existing token

        //if the token is valid, login
    }

    private function validateMobitelNumber($sPhoneNumber)
    {

    }
}