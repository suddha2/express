<?php

namespace Api\Manager;

use Api\Operation\Request\QueryCriteria;
use Api\Client\Rest\Factory;

class UserLight extends Base {

    /**
     * Get user list for a division
     * @return mixed
     */
    public function getUsersByDivision($divisionId)
    {
        $criteria = new QueryCriteria();
        $criteria->addParam('divisionId', $divisionId);
        $criteria->setSortField('firstName');
        $criteria->setLoadAll();
        $fac = new Factory($this->getServiceManager(), 'userLight');
        return $fac->getList($criteria)->body;
    }
}