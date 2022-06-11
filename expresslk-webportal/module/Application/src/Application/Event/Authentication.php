<?php
/**
 * File for Event Class
 *
 * @category  User
 * @package   User_Event
 * @author    Udantha Pathirana <info@udantha.com>
 */

/**
 * @namespace
 */
namespace Application\Event;

/**
 * @uses Zend\Mvc\MvcEvent
 * @uses User\Controller\Plugin\UserAuthentication
 * @uses User\Acl\Acl
 */
use Application\Acl\Acl;
use Application\Exception\SessionTimeoutException;
use Application\Model\User;
use Application\Util\Log;
use Zend\Mvc\Controller\Plugin\AbstractPlugin;
use Zend\Mvc\MvcEvent as MvcEvent,
    Zend\Authentication\AuthenticationService,
    Zend\View\Helper\Navigation\AbstractHelper,
    Application\Acl\Acl as AclClass;
use Zend\View\Model\ViewModel;

/**
 * Authentication Event Handler Class
 *
 * This Event Handles Authentication
 *
 * @category  User
 * @package   User_Event
 */
class Authentication extends AbstractPlugin
{
    /**
     * @var AuthPlugin
     */
    protected $_userAuth = null;

    /**
     * @var AclClass
     */
    protected $_aclClass = null;

    /**
     * preDispatch Event Handler
     *
     * @param \Zend\Mvc\MvcEvent $event
     * @throws \Exception
     */
    public function preDispatch(MvcEvent $event)
    {
        /**
         * @var $userAuth AuthenticationService
         */
        $userAuth = $event->getApplication()->getServiceManager()->get('AuthService');
        $acl = $this->getAclClass($event);

        if ($userAuth->hasIdentity()) {
            //logged in user, redirect to permission page
            $redirectUrl = '/app/auth/stafflogin';
        }else{
            //not logged in
            $redirectUrl = '/app/auth/stafflogin';
        }
        $oUser = Acl::getAuthUser($event->getApplication()->getServiceManager());
        $roles = $oUser->getRole();

        //set acl and role to be used in menus
        AbstractHelper::setDefaultAcl($acl);
        AbstractHelper::setDefaultRole($roles);

        $routeMatch = $event->getRouteMatch();
        $controller = $routeMatch->getParam('controller');
        $action     = $routeMatch->getParam('action');

        //check if resource doesnt exists or if resource exists, check if allowed
        if (!$acl->hasResource($controller) ||
            (!$acl->isAllowed($roles, $controller, $action))) {

            //Skip auth controller
            if($controller=='Application\Controller\Auth'){
                return;
            }

            //check headers for json request. If so throw an error so error handler performs proper formatting
            $headers = $event->getRequest()->getHeaders();
            $accept = $headers->get('Accept');
            $match  = is_object($accept) ? $accept->match('application/json') : false;
            if (!$match || $match->getTypeString() == '*/*') {
                //request is not json
                //if already logged in, show error page
                if ($userAuth->hasIdentity()) {
                    //set error view
                    throw new \Exception('You don\'t have permission', 401);
                }else{
                    //set redirect to
                    $to = '?r=' . urlencode($_SERVER['REQUEST_URI']);
                    $response = $this->getController()->plugin('redirect')->toUrl( $redirectUrl . $to );
                    $response->sendHeaders();
                    exit;
                }
            }else{
                //request is json
                throw new SessionTimeoutException('Session expired. Please login again.');
            }

        }
    }

    /**
     * Sets ACL Class
     *
     * @param \Application\Acl\Acl $aclClass
     * @return Authentication
     */
    public function setAclClass(AclClass $aclClass)
    {
        $this->_aclClass = $aclClass;

        return $this;
    }

    /**
     * Gets ACL Class
     *
     * @return \Application\Acl\Acl
     */
    public function getAclClass(MvcEvent $event)
    {
        if ($this->_aclClass === null) {
            $this->_aclClass = new AclClass( $event->getApplication()->getServiceManager() );
        }

        return $this->_aclClass;
    }

}
