<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class RuleCondition extends Entity
{

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @param int $id
     * @access public
     */
    public function __construct($id)
    {
      $this->id = $id;
    }

}
