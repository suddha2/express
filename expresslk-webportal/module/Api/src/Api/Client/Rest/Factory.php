<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 6/28/15
 * Time: 1:29 PM
 */

namespace Api\Client\Rest;


use Api\Operation\CrudInterface;
use Api\Operation\Request\QueryCriteria;

/**
 * Factory class to manage all the generic rest API calls to Backend
 * Class Factory
 * @package Api\Client\Rest
 */
class Factory extends Connector implements CrudInterface{

    protected $entity;

    public function __construct($serviceManager, $entity)
    {
        parent::__construct($serviceManager);
        //save entity
        $this->entity = $entity;
    }

    public function getOne($id)
    {
        $url = $this->getBaseUrl() . '/'. $this->entity .'/'. $id;
        $response = $this->get($url);
        return $response;
    }

    public function getList(QueryCriteria $criteria)
    {
        $url = $this->getBaseUrl() . '/'. $this->entity;
        $response = $this->get($url, $criteria);
        return $response;
    }

    public function createOne($data, $headers = array())
    {
        $url = $this->getBaseUrl() . '/'. $this->entity;
        $response = $this->put($url, $data, $headers);
        return $response;
    }

    public function updateOne($id, $data)
    {
        $url = $this->getBaseUrl() . '/'. $this->entity;
        $data->id = $id;
        $response = $this->put($url, $data);
        return $response;
    }

    public function deleteOne($id)
    {
        $url = $this->getBaseUrl() . '/'. $this->entity .'/'. $id;
        $response = $this->delete($url);
        return $response;
    }
}