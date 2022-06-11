<?php

namespace Api\Client\Soap\Core;

class ConfirmationCriteria
{

    /**
     * @var int $agentId
     * @access public
     */
    public $agentId = null;

    /**
     * @var ClientDetail $client
     * @access public
     */
    public $client = null;

    /**
     * @var SeatAllocations[] $itemSeatAllocations
     * @access public
     */
    public $itemSeatAllocations = null;

    /**
     * @var PassengerDetail[] $passengers
     * @access public
     */
    public $passengers = null;

    /**
     * @var PaymentCriteria $paymentCriteria
     * @access public
     */
    public $paymentCriteria = null;

    /**
     * @var PaymentMethodCriteria $paymentMethodCriteria
     * @access public
     */
    public $paymentMethodCriteria = null;

    /**
     * @var string $remarks
     * @access public
     */
    public $remarks = null;

    /**
     * @param int $agentId
     * @param ClientDetail $client
     * @access public
     */
    public function __construct($agentId, $client)
    {
      $this->agentId = $agentId;
      $this->client = $client;
    }

}
