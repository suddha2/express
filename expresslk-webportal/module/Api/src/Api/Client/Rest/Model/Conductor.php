<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 3/19/15
 * Time: 3:17 PM
 */

namespace Api\Client\Rest\Model;

class Conductor extends SpecializedPerson {

    public function __construct($serviceManager)
    {
        parent::__construct($serviceManager, 'conductor');
    }
}