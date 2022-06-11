<?php
/**
 * Created by PhpStorm.
 * User: Kavinda
 * Date: 8/24/14
 * Time: 11:50 AM
 */

namespace Api\Client\Rest;


use Api\Operation\Request\QueryCriteria;
use Application\Acl\Acl;
use Application\Exception\HttpStatusException;
use Application\Exception\SessionTimeoutException;
use Httpful\Mime;
use Httpful\Request;
use Zend\Authentication\AuthenticationService;
use Zend\Debug\Debug;
use Zend\ServiceManager\ServiceManager;

class Connector {

    const HTTP_SUCCESS = 200;
    const HTTP_SESSION_EXPIRED = 403;
    const HTTP_METHOD_BLOCKED = 405;
    const HTTP_SERVER_ERROR = 500;

    private $_sm, $_config, $_baseUrl;
    private $_AuthHeadersEnabled = true; //add auth headers to each request

    public function __construct(ServiceManager $sm)
    {
        $this->_sm = $sm;
        $this->_config = $sm->get('Config');

        //save base url for rest requests
        $this->_baseUrl = $this->_config['rest']['v1']['admin'];
    }

    /**
     * @param boolean $AuthHeadersEnabled
     * @return Connector
     */
    public function setAuthHeadersEnabled($AuthHeadersEnabled)
    {
        $this->_AuthHeadersEnabled = $AuthHeadersEnabled;
        return $this;
    }

    /**
     * Get REST base url
     * @return mixed
     */
    protected function getBaseUrl()
    {
        return $this->_baseUrl;
    }

    /**
     * @return ServiceManager
     */
    protected function getServiceManager()
    {
        return $this->_sm;
    }

    /**
     * sends get request to the server
     * @param $url
     * @param QueryCriteria|null $criteria
     * @return \Httpful\Response
     */
    public function get($url, QueryCriteria $criteria = null)
    {
        //set query criteria
        if(!is_null($criteria)){
            $url = $url . '?' . $criteria->createQuery();
        }

        $self = clone $this;
        $response = Request::get($url)
            ->expects(Mime::JSON)
            ->parseResponsesWith(function($response) use ($self){
                //parse response
                return $self->_parseRESTResponse($response);
            })
            //send user/token
            ->addHeaders($this->_getRequestHeaders())
            ->send();

        //validate headers
        $this->_validateHeaders($response);

        return $response;
    }

    /**
     * sends a post request to the server
     * @param $url
     * @param $data
     * @return \Httpful\Response
     */
    public function post($url, $data)
    {
        $self = clone $this;
        $response = Request::post($url)
            ->expects(Mime::JSON)
            ->parseResponsesWith(function($response) use ($self){
                //parse response
                return $self->_parseRESTResponse($response);
            })
            //send user/token
            ->addHeaders($this->_getRequestHeaders())
            ->sendsType(Mime::FORM)
            ->body($data)
            ->send();

        //validate headers
        $this->_validateHeaders($response);

        return $response;
    }

    /**
     * Sends a put request to the server
     * @param $url
     * @param $data
     * @param $headers
     * @return \Httpful\Response
     */
    public function put($url, $data, $headers = array())
    {
        $self = clone $this;
        $response = Request::put($url)
            ->expects(Mime::JSON)
            ->parseResponsesWith(function($response) use ($self){
                //parse response
                return $self->_parseRESTResponse($response);
            })
            //send user/token
            ->addHeaders($this->_getRequestHeaders($headers))
            ->sendsType(Mime::JSON)
            ->body($data)
            ->send();

        //validate headers
        $this->_validateHeaders($response);

        return $response;
    }

    /**
     * @param $url
     * @return \Httpful\Response
     */
    public function delete($url)
    {
        $self = clone $this;
        $response = Request::delete($url)
            //send user/token
            ->addHeaders($this->_getRequestHeaders())
            ->sendsType(Mime::JSON)
            ->send();

        //validate headers
        $this->_validateHeaders($response);

        return $response;
    }

    /**
     * @param \Httpful\Response $oResponse
     * @throws HttpStatusException
     * @throws SessionTimeoutException
     */
    protected function _validateHeaders($oResponse)
    {
        //check for http status code
        $iStatusCode = $oResponse->code;
        $aBody = $oResponse->body;
        $sErrorMessage = isset($aBody->error)? strval($aBody->error) : 'HTTP Error: '. $iStatusCode .'. Error from services';

        //throw exception if not success status
        switch ($iStatusCode){
            //skip if success
            case self::HTTP_SUCCESS:
                break;
            //throw session expired
            case self::HTTP_SESSION_EXPIRED:
                throw new SessionTimeoutException('Session expired. Please login again.');
                break;
            default:
        }
        //if there's still an error, throw appropriate exception
        if($oResponse->hasErrors()){
            throw new HttpStatusException($sErrorMessage, $iStatusCode);
        }
    }

    /**
     * Get request headers for each request
     * @param array $pHeaders
     * @return array
     */
    private function _getRequestHeaders($pHeaders = array())
    {
        $aHeaders = array();

        if($this->_AuthHeadersEnabled){
            //Inject user headers
            $oUser = Acl::getAuthUser($this->getServiceManager());
            $aHeaders['username'] = $oUser->username;
            $aHeaders['token'] = $oUser->token;
        }

        //merge headers if passed in is an array
        if(!empty($pHeaders) && is_array($pHeaders)){
            $aHeaders = array_merge($aHeaders, $pHeaders);
        }

        return $aHeaders;
    }

    /**
     * Parse REST response
     * @param $response
     * @return mixed
     * @throws \Exception
     */
    protected function _parseRESTResponse($response)
    {
        $parsed = json_decode($response, false);
        //check if response is okay
        if (is_null($parsed) && 'null' !== strtolower($response)){
            //return response as an error
            throw new \Exception($response);
        }
        return $parsed;
    }

    /**
     * Create a flat array from object
     * @param $object
     * @return array
     */
    protected function flattenObject($object)
    {
        $result = array();
        if(is_array($object)){
            foreach ($object as $k => $v) {
                $result[$k] = $this->merger('', $v);
                //merge array and original object
                $result[$k] = $this->combineArrayObject($result[$k], $v);
            }
        }
        else{
            $result = $this->merger('', $object);
            //merge array and original object
            $result = $this->combineArrayObject($result, $object);
        }
        return $result;
    }

    private function merger($prefix, $object)
    {
        $result = array();
        if (is_array($object)){
            foreach ($object as $k => $v) {
                $result = array_merge($result, $this->merger($prefix . $k, $v));
            }
        }
        elseif(is_object($object)){
            $props = get_object_vars($object);
            $result = array_merge($result, $this->merger($prefix, $props));
        }else{
            $result[$prefix] = $object;
        }

        return $result;
    }

    /**
     * @param $array
     * @param $object
     * @return mixed
     */
    private function combineArrayObject($array, $object)
    {
        $vars = get_object_vars($object);
        foreach ($vars as $k => $v) {
            $array[$k] = $v;
        }
        return $array;
    }

}