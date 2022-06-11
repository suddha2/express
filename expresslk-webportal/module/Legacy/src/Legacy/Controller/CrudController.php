<?php

namespace Legacy\Controller;

use Api\Operation\Request\QueryCriteria;
use Zend\Json\Json;
use Zend\ServiceManager\ServiceManager;

class CrudController extends \Admin\Controller\CrudController
{
    private $entity;

    private $count = -1;

    public function __construct($entity)
    {
        $this->entity = $entity;
    }

    public function getEntity(ServiceManager $sm, $id)
    {
        if (is_array($id)) {
            return $this->getEntityList($sm, array('id', $id));
        }

        $entity = $this->getHeavyEntity();
        $oGateway = $this->getGatewayObject($entity, $sm);
        $response = $oGateway->getOne($id);
        return $response->body;
    }

    public function createEntity(ServiceManager $sm, $data, $headers = array())
    {
        $entity = $this->getHeavyEntity();
        $oGateway = $this->getGatewayObject($entity, $sm);
        $oModel = $this->getModelObject($entity, $data, $sm);
        $response = $oGateway->createOne($oModel, $headers);

        return $response;
    }

    public function updateEntity(ServiceManager $sm, $id, $data)
    {
        $entity = $this->getHeavyEntity();
        $oGateway = $this->getGatewayObject($entity, $sm);
        $oModel = $this->getModelObject($entity, $data, $sm);
        $response = $oGateway->updateOne($id, $oModel);

        return $response;
    }

    public function getEntityList(ServiceManager $sm, $queryParams)
    {
        $criteria = new QueryCriteria();
        $criteria->setParams($queryParams);
        $oGateway = $this->getGatewayObject($this->entity, $sm);
        $response = $oGateway->getList($criteria);
        $this->count = (int) $response->headers->offsetGet('X-Total-Count');
        return $response->body;
    }

    public function count()
    {
        return $this->count;
    }

    protected function getHeavyEntity()
    {
        if ($this->isLightEntity()) {
            return substr($this->entity, 0, -5);
        } else {
            return $this->entity;
        }
    }

    protected function isLightEntity()
    {
        $strlen = strlen($this->entity);
        if ($strlen < 5) {
            return false;
        }
        return substr_compare($this->entity, 'Light', $strlen - 5, 5) === 0;
    }
}