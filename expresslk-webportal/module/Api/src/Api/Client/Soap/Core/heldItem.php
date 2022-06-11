<?php

namespace Api\Client\Soap\Core;

class heldItem
{

    /**
     * @var int $heldItemId
     * @access public
     */
    public $heldItemId = null;

    /**
     * @var string $resultIndex
     * @access public
     */
    public $resultIndex = null;

    /**
     * @var int $sectorIndex
     * @access public
     */
    public $sectorIndex = null;

    /**
     * @param int $heldItemId
     * @param string $resultIndex
     * @param int $sectorIndex
     * @access public
     */
    public function __construct($heldItemId, $resultIndex, $sectorIndex)
    {
      $this->heldItemId = $heldItemId;
      $this->resultIndex = $resultIndex;
      $this->sectorIndex = $sectorIndex;
    }

}
