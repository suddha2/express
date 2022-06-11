<?php

namespace Api\Client\Soap\Core;

class CancellationCriteria
{

    /**
     * @var int $bookingId
     * @access public
     */
    public $bookingId = null;

    /**
     * @var CancellationCause $cause
     * @access public
     */
    public $cause = null;

    /**
     * @var boolean $chargeCancellationFee
     * @access public
     */
    public $chargeCancellationFee = null;

    /**
     * @var ItemCancellationCriteria[] $itemCriteria
     * @access public
     */
    public $itemCriteria = null;

    /**
     * @var string $remark
     * @access public
     */
    public $remark = null;

    /**
     * @param int $bookingId
     * @param CancellationCause $cause
     * @param boolean $chargeCancellationFee
     * @param string $remark
     * @access public
     */
    public function __construct($bookingId, $cause, $chargeCancellationFee, $remark)
    {
      $this->bookingId = $bookingId;
      $this->cause = $cause;
      $this->chargeCancellationFee = $chargeCancellationFee;
      $this->remark = $remark;
    }

}
