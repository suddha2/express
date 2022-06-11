<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 3/19/15
 * Time: 3:17 PM
 */

namespace Api\Client\Rest\Model;


use Api\Client\Rest\Connector;
use Api\Operation\Request\QueryCriteria;

class Supplier extends Connector{

    /**
     * @return mixed
     */
    public function getSupplierList(QueryCriteria $criteria = null,$bitmask)
    {
        $url = $this->getBaseUrl() . '/supplier/list/'.$bitmask;
        $response = $this->get($url, $criteria);

        return $response->body;
    }
}