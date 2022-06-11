<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BusImage extends Entity
{

    /**
     * @var int $busId
     * @access public
     */
    public $busId = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var base64Binary $image
     * @access public
     */
    public $image = null;

    /**
     * @var BusImageType $type
     * @access public
     */
    public $type = null;

    /**
     * @param int $busId
     * @param int $id
     * @param base64Binary $image
     * @param BusImageType $type
     * @access public
     */
    public function __construct($busId, $id, $image, $type)
    {
      $this->busId = $busId;
      $this->id = $id;
      $this->image = $image;
      $this->type = $type;
    }

}
