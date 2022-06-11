<?php

namespace Api\Client\Rest\Model;

class SupplierContactPerson extends SpecializedPerson {

    public function __construct($serviceManager)
    {
        parent::__construct($serviceManager, 'supplierContactPerson');
    }
}