<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 7/22/14
 * Time: 3:40 PM
 */

namespace Application\Controller;


use Application\Acl\Acl;
use Application\Form\Login;
use Application\Form\PasswordReset;
use Application\Form\Signup;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\Mvc\MvcEvent;
use Zend\View\Model\ViewModel;

class AuthController extends AbstractActionController {

    protected $authservice;

    protected function getRedirectUrl()
    {
        $url = '/';
        if($this->getAuthService()->hasIdentity()){
            $identity = $this->getAuthService()->getIdentity();
            $role = $identity->getRole()->getRoles();
            switch($role[count($role)-1]){
                case Acl::ROLE_CUSTOMER :
                    $url = '/';
                    break;
                case Acl::ROLE_AGENT :
                    $url = '/';
                    break;
                default:
                    $url = '/admin-panel/';
                    break;
            }
        }
        return $url;
    }

    public function loginAction()
    {
        //check if user is already logged in
        if($this->getAuthService()->hasIdentity()){
            //redirect
            $this->redirect()->toUrl($this->getRedirectUrl());
        }

        $form = new Login();
        $message = '';

        $request = $this->getRequest();
        $redirectTo = $this->params()->fromQuery('r');

        if ($request->isPost()) {
            $form->setData($request->getPost());
            if ($form->isValid()) {
                $formData = $form->getData();

                //check authentication and log in
                /**
                 * @var $authService \Zend\Authentication\AuthenticationService
                 */
                $authService = $this->getServiceLocator()->get('AuthService');
                /**
                 * @var $adapter \Application\Adapter\CoreAuthenticationAdapter
                 */
                $adapter = $authService->getAdapter();

                $adapter->setIdentity($formData['username'])
                    ->setCredential($formData['password']);
                //$rememberMe = $formData['remember'];

                //authenticate
                $result = $authService->authenticate();
                //check for result validation
                if ($result->isValid()) {
                    //get redirect url
                    $redirectTo = $redirectTo? $redirectTo : $this->getRedirectUrl();
                    $this->redirect()->toUrl($redirectTo);
                } else {
                    //return $result->getMessages();
                    //show error messages
                    $message = 'Login failed. Please try again.';
                }
            }else{
                $message = 'Login failed. Please try again.';
            }
        }

        return array('loginForm' => $form, 'message'=> $message);
    }

    public function staffloginAction()
    {
        //call login method
        $loginArray = $this->loginAction();
        $oView = new ViewModel();
        $oView->setVariables($loginArray);
        $this->layout('/layout/layout_basic');

        return $oView;
    }

    /**
     * Logout Action
     */
    public function logoutAction()
    {
        $redirectTo = $this->params()->fromQuery('r', '/app/auth/stafflogin');

        $this->getAuthService()->clearIdentity();
        //redirect to home
        $this->redirect()->toUrl($redirectTo);
    }

    /**
     * Signup action
     *
     * @return array
     */
    public function inscriptionAction()
    {
        /**
         * Facebook signup/signin response
         */
        $session = null;
        $conf = $this->getServiceLocator()->get('Config');
//        FacebookSession::setDefaultApplication($conf['vd']['FB']['APP_ID'], $conf['vd']['FB']['APP_SECRET']);
//        $helper = new FacebookRedirectLoginHelper($this->getServerName(). '/inscription');
//        try {
//            $session = $helper->getSessionFromRedirect();
//        } catch(FacebookRequestException $ex) {
//            // When Facebook returns an error
//        } catch(\Exception $ex) {
//            // When validation fails or other local issues
//        }

        $form = new Signup();
        $message = '';
        /**
         * @var $customers \Application\Model\Customers
         */
        $customers = $this->getServiceLocator()->get('Application\Model\Customers');
        $request = $this->getRequest ();

        if ($request->isPost ()) {

            $form->setInputFilters($this->getServiceLocator());
            $form->setData( $request->getPost () );

            if ($form->isValid()) {
                $formData = $form->getData();
                try {
                    /**
                     * @todo inplement signup logic
                     *
                     */
                    $customers->createCustomer(array(
                        'gender' => $formData['gender'],
                        'email' => strtolower($formData['email']),
                        'country_id' => $formData['country'],
                        'zodiac' => $formData['zodiac'],
                        'password' => (empty($formData['password']) ? null : $formData['password']),
                        'registrationdate' => date('Y-m-d H:i:s'),
                        'credit' => 0,
                        'status' => 1,
                        'group_id' => 1,
                        'code' => null,
                        'bindings_id' => null,
                        'language_id' => 2
                    ));

                    try {
                        /**
                         * @var $oEmail \Application\Model\Email
                         */
                        $oEmail = $this->getServiceLocator()->get('Email');
                        $oEmail->addTo(strtolower($formData['email']))
                            ->addFrom('email@newdimension.lu', 'Voyance Destin')
                            ->setSubject('Your Voyance Destin Account created.')
                            ->setBody('Hi,
                                Thank you for signing up with VoyanceDestin. Your account has been created.
                                ')
                            ->send();
                    } catch (\Exception $e) {
                    }

                    //log the user in
                    $this->loginUsingEmail($formData['email']);
                    //redirect to moncompte
                    $this->redirect()->toUrl('/moncompte');

                } catch (\Exception $e) {
                    $message = $e->getMessage();
                }
            }
        }else{
            //populate from FB data
            if ($session) {
                // Logged in
//                try {
//                    $user_profile = (new FacebookRequest(
//                        $session, 'GET', '/me'
//                    ))->execute()->getGraphObject(GraphUser::className());
//
//                    /**
//                     * @todo check if email already exists in the database, and log user in
//                     */
//
//                    $userData = $user_profile->asArray();
//                    $form->get('email')->setValue( !empty($userData['email']) ? $userData['email'] : '' );
//                    $form->get('gender')->setValue( !empty($userData['gender']) ? $userData['gender'] : '' );
//                    //$form->get('email')->setValue( $user_profile->getBirthday() ? $user_profile->getBirthday() : '' );
//
//                } catch(FacebookRequestException $e) {
//
//                }
            }
        }

        return array('form' => $form, 'message' => $message);
    }


    /**
     * @param $email
     * @return array|bool
     */
    private function loginUsingEmail($email)
    {
        $response = false;
        /**
         * @var $customers \Application\Model\Customers
         */
        $customers = $this->getServiceLocator()->get('Application\Model\Customers');
        $customerDetails = $customers->getCustomerByEmail($email);
        if($customerDetails){
            $response = $customers->loginAuthenticate($customerDetails['email'], $customerDetails['password']);
        }
        return $response;
    }

    public function fbauthAction()
    {
//        $conf = $this->getServiceLocator()->get('Config');
//
//        FacebookSession::setDefaultApplication($conf['vd']['FB']['APP_ID'], $conf['vd']['FB']['APP_SECRET']);
//        //get login url
//        $helper = new FacebookRedirectLoginHelper($this->getServerName(). '/inscription');
//        $url = $helper->getLoginUrl(array(
//            'email',
//            'public_profile'
//        ));
//
//        $this->redirect()->toUrl($url);
    }

    public function resetAction()
    {
        $form = new PasswordReset();

        $request = $this->getRequest ();
        $emailSent = null;

        if ($request->isPost ()) {

            $form->setInputFilters($this->getServiceLocator());
            $form->setData( $request->getPost () );

            if ($form->isValid()) {
                $formData = $form->getData();

                try {
                    $oUser = $this->getServiceLocator()->get('Application\Model\Customers');
                    $aUser = $oUser->getCustomerByEmail($formData['email']);
                    /**
                     * @var $oEmail \Application\Model\Email
                     */
                    $oEmail = $this->getServiceLocator()->get('Email');
                    $oEmail->addTo($aUser['email'])
                        ->addFrom('email@newdimension.lu', 'Voyance Destin')
                        ->setSubject('Your Voyance Destin password.')
                        ->setBody('Hi,
                        Your password is '. $aUser['password'] .'
                        ')
                        ->send();

                    $emailSent = true;
                } catch (\Exception $e) {
                    $emailSent = false;
                    $form->get('email')->setMessages(array('Sending email failed. Plese try again'));
                }
            }
        }

        return array(
            'form' => $form,
            'emailSent' => $emailSent
        );
    }

    public function getAuthService()
    {
        if (! $this->authservice) {
            $this->authservice = $this->getServiceLocator()->get('AuthService');
        }

        return $this->authservice;
    }

    private function getServerName()
    {
        $uri = $this->getRequest()->getUri();
        $base = sprintf('%s://%s', $uri->getScheme(), $uri->getHost());
        return $base;
    }

} 