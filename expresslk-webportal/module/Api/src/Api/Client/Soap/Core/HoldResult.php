<?php

namespace Api\Client\Soap\Core;

class HoldResult
{

    /**
     * @var AdvisoryNote[] $advisoryNotes
     * @access public
     */
    public $advisoryNotes = null;

    /**
     * @var int[] $heldItemIds
     * @access public
     */
    public $heldItemIds = null;

    /**
     * @var heldItem[] $heldItems
     * @access public
     */
    public $heldItems = null;

    /**
     * @access public
     */
    public function __construct()
    {
    
    }

}
