<?php

namespace Api\Client\Rest\Model;

use Api\Client\Rest\Factory;
use Api\Operation\Request\QueryCriteria;

abstract class SpecializedPerson extends Factory {

    /**
     * @param QueryCriteria $criteria
     * @return mixed
     */
    public function getList(QueryCriteria $criteria)
    {
        $response = parent::getList($criteria);
        $response->body = $this->removePersonId($response->body);
        return $response;
    }

    private function removePersonId($object)
    {
        if (is_array($object)) {
            foreach ($object as $v) {
                unset($v->person->id);
            }
        } else {
            unset($object->person->id);
        }
        return $object;
    }
}