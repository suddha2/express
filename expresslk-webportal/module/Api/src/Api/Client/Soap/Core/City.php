<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class City extends Entity
{

    /**
     * @var boolean $active
     * @access public
     */
    public $active = null;

    /**
     * @var string $code
     * @access public
     */
    public $code = null;

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
     * @var string $nameSi
     * @access public
     */
    public $nameSi = null;

    /**
     * @var string $nameTa
     * @access public
     */
    public $nameTa = null;
	
	 /**
     * @var string $district
     * @access public
     */
    public $district = null;
	
	

    /**
     * @param boolean $active
     * @param string $code
     * @param int $id
     * @param string $name
     * @param string $nameSi
     * @param string $nameTa
     * @access public
     */
    public function __construct($active, $code, $id, $name, $nameSi, $nameTa,$district)
    {
      $this->active = $active;
      $this->code = $code;
      $this->id = $id;
      $this->name = $name;
      $this->nameSi = $nameSi;
      $this->nameTa = $nameTa;
	  $this->district=$district;
    }

}
