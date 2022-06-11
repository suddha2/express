<?php


namespace Legacy\Ticketing;


use Application\Helper\ExprDateTime;
use Application\InjectorBase;
use Legacy\Extend\ProviderBase;
use Zend\Session\Container;

class Session extends InjectorBase
{
    const SESSION_NAME = 'LegacySearchSession';
    const SESS_SEARCH_NAME = 'SearchSession';
    const SESS_SEARCH_DATA = 'SearchSessionData';
    const SESS_CUSTOMER_PHONE = 'SearchSessionCustomerPhone';
    const SESS_ID = 'SearchSessionID';
    const SESS_EXPIRY = 'SearchSessionExpiry';

    private $_sessionObject;

    public function __construct($serviceManager)
    {
        parent::__construct($serviceManager);
        //initialize session
        $this->_sessionObject = new Container(self::SESSION_NAME);
    }

    public function initSession()
    {
        //set expiry time to 5 minutes from now
        $iExpireMinutes = 5;
        $oExpireTime = new ExprDateTime();
        $oExpireTime->add(new \DateInterval('PT'.$iExpireMinutes.'M'));

        //expire all previous sessions
        //clear out only this session container
        $this->_sessionObject->getManager()->getStorage()->clear(self::SESSION_NAME);
        $this->_sessionObject = new Container(self::SESSION_NAME);
        //intialize session parameters
        $this->_sessionObject->offsetSet(self::SESS_ID, uniqid('m', true));
        //set expire time
        $this->_sessionObject->offsetSet(self::SESS_EXPIRY, $oExpireTime);
        //
    }

    /**
     * @return bool
     * @throws \Exception
     */
    private function validateSession()
    {
        /** @var ExprDateTime $oExpireTime */
        $oExpireTime = $this->_sessionObject->offsetGet(self::SESS_EXPIRY);
        if($oExpireTime && ($oExpireTime>(new ExprDateTime()))){
            return true;
        }
        throw new \Exception('Session timed out. Please try again.');
    }

    public function getSessionId()
    {
        return $this->_sessionObject->offsetGet(self::SESS_ID);
    }

    /**
     * @param $sessionId
     * @return bool
     */
    public function sessionIdIsValid($sessionId)
    {
        $saved = $this->_sessionObject->offsetGet(self::SESS_ID);
        if($saved && !is_null($saved) && $saved==$sessionId){
            return true;
        }
        return false;
    }

    /**
     * @return mixed
     */
    public function getSearchSession()
    {
        //validate current session
        $this->validateSession();

        return $this->_sessionObject->offsetGet(self::SESS_SEARCH_NAME);
    }

    /**
     * @param mixed $searchSession
     * @return Session
     */
    public function setSearchSession($searchSession)
    {
        //validate current session
        $this->validateSession();

        $this->_sessionObject->offsetSet(self::SESS_SEARCH_NAME, $searchSession);
        return $this;
    }

    /**
     * @param $key
     * @return mixed
     * @throws \Exception
     */
    public function getSearchData($key)
    {
        //validate current session
        $this->validateSession();

        $index = self::SESS_SEARCH_DATA . $key;

        if(!$this->_sessionObject->offsetExists($index)){
            throw new \Exception('Session expired. Please try again.');
        }

        return $this->_sessionObject->offsetGet($index);
    }

    /**
     * @param $key
     * @param $data
     * @return $this
     */
    public function setSearchData($key, $data)
    {
        //validate current session
        $this->validateSession();

        $index = self::SESS_SEARCH_DATA . $key;

        $this->_sessionObject->offsetSet($index, $data);
        return $this;
    }

    /**
     * @return ProviderBase
     * @throws \Exception
     */
    public function getProvider()
    {
        //validate current session
        $this->validateSession();

        $oProvider = $this->_sessionObject->offsetGet(self::SESS_CUSTOMER_PHONE);
        if(!($oProvider instanceof ProviderBase)){
            throw new \Exception('Phone number provider is not set properly. Please re-load the process.');
        }
        return $oProvider;
    }

    /**
     * @param ProviderBase $oProvider
     * @return $this
     */
    public function setProvider(ProviderBase $oProvider)
    {
        //validate current session
        $this->validateSession();

        $this->_sessionObject->offsetSet(self::SESS_CUSTOMER_PHONE, $oProvider);
        return $this;
    }

    /**
     * Destroy session
     */
    public function destroy()
    {
        //clear out only this session container
        $this->_sessionObject->getManager()->getStorage()->clear(self::SESSION_NAME);
        //log the user out
        $this->getServiceLocator()->get('AuthService')->clearIdentity();
    }

}