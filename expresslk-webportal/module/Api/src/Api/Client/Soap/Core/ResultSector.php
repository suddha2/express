<?php

namespace Api\Client\Soap\Core;

include_once('CostPrice.php');

class ResultSector extends CostPrice
{

    /**
     * @var dateTime $arrivalTime
     * @access public
     */
    public $arrivalTime = null;

    /**
     * @var dateTime $departureTime
     * @access public
     */
    public $departureTime = null;

    /**
     * @var City $fromCity
     * @access public
     */
    public $fromCity = null;

    /**
     * @var BusSchedule $schedule
     * @access public
     */
    public $schedule = null;

    /**
     * @var City $toCity
     * @access public
     */
    public $toCity = null;

    /**
     * @param charges $charges
     * @param discounts $discounts
     * @param float $grossPrice
     * @param markups $markups
     * @param float $priceBeforeCharges
     * @param float $priceBeforeTax
     * @param taxes $taxes
     * @param dateTime $arrivalTime
     * @param dateTime $departureTime
     * @param City $fromCity
     * @param BusSchedule $schedule
     * @param City $toCity
     * @access public
     */
    public function __construct($charges, $discounts, $grossPrice, $markups, $priceBeforeCharges, $priceBeforeTax, $taxes, $arrivalTime, $departureTime, $fromCity, $schedule, $toCity)
    {
      parent::__construct($charges, $discounts, $grossPrice, $markups, $priceBeforeCharges, $priceBeforeTax, $taxes);
      $this->arrivalTime = $arrivalTime;
      $this->departureTime = $departureTime;
      $this->fromCity = $fromCity;
      $this->schedule = $schedule;
      $this->toCity = $toCity;
    }

}
