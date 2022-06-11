<?php


namespace Api\Client\Rest\Model;

use Api\Operation\CrudInterface;
use Api\Operation\Request\QueryCriteria;
use Application\Model\User;
use Zend\ServiceManager\ServiceManager;

class AllowedDivisions implements CrudInterface{

    /**
     * @var ServiceManager
     */
    private $sm;

    private $divisions = array();

    public function __construct($serviceManager)
    {
        $this->sm = $serviceManager;
        $this->divisions = $this->getUserObject()->getDivisions();
    }

    public function getOne($id)
    {
        //search key for bitmask
        $response = new \stdClass();
        $response->body = isset($this->divisions[$id])? $this->divisions[$id] : null;
        return $response;
    }

    public function getList(QueryCriteria $criteria)
    {
        $aDivs = is_array($this->divisions)? array_values($this->divisions): array();
        //search key for bitmask
        $response = new \stdClass();
        $response->body = $aDivs;
        $response->headers =  new \ArrayObject(array('X-Total-Count' => count($aDivs)));
        return $response;
    }

    public function getDivisionById($id)
    {

    }

    public function createOne($data, $headers = array())
    {
        throw new \Exception('Creating is not possible for AllowedDivisionss');
    }

    public function updateOne($id, $data)
    {
        throw new \Exception('Updating is not possible for AllowedDivisionss');
    }

    public function deleteOne($id)
    {
        throw new \Exception('Deletion is not possible for AllowedDivisionss');
    }

    /**
     * @return \Application\Model\User
     */
    private function getUserObject()
    {
        $oUser = $this->sm->get('AuthService')->getIdentity();
        return $oUser ? $oUser : new User();
    }
}