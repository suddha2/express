<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class ReportParameter extends Entity
{

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var int $reportId
     * @access public
     */
    public $reportId = null;

    /**
     * @var ValueType $type
     * @access public
     */
    public $type = null;

    /**
     * @var string[] $values
     * @access public
     */
    public $values = null;

    /**
     * @var boolean $visible
     * @access public
     */
    public $visible = null;

    /**
     * @param int $id
     * @param string $name
     * @param int $reportId
     * @param ValueType $type
     * @param boolean $visible
     * @access public
     */
    public function __construct($id, $name, $reportId, $type, $visible)
    {
      $this->id = $id;
      $this->name = $name;
      $this->reportId = $reportId;
      $this->type = $type;
      $this->visible = $visible;
    }

}
