<?php
namespace Api\Manager;

use Api\Operation\Request\QueryCriteria;

class Supplier extends Base{
    public function getSupplier(QueryCriteria $criteria = null,$bitMaskSum)
    {
        $supplier = new \Api\Client\Rest\Model\Supplier($this->getServiceManager());
        return $supplier->getSupplierList($criteria, $bitMaskSum );
    }

}