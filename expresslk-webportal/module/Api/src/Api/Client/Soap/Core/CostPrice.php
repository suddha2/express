<?php

namespace Api\Client\Soap\Core;

include_once('BaseCostPrice.php');

class CostPrice extends BaseCostPrice
{

    /**
     * @var charges $charges
     * @access public
     */
    public $charges = null;

    /**
     * @var discounts $discounts
     * @access public
     */
    public $discounts = null;

    /**
     * @var float $grossPrice
     * @access public
     */
    public $grossPrice = null;

    /**
     * @var markups $markups
     * @access public
     */
    public $markups = null;

    /**
     * @var float $priceBeforeCharges
     * @access public
     */
    public $priceBeforeCharges = null;

    /**
     * @var float $priceBeforeTax
     * @access public
     */
    public $priceBeforeTax = null;

    /**
     * @var taxes $taxes
     * @access public
     */
    public $taxes = null;

    /**
     * @param charges $charges
     * @param discounts $discounts
     * @param float $grossPrice
     * @param markups $markups
     * @param float $priceBeforeCharges
     * @param float $priceBeforeTax
     * @param taxes $taxes
     * @access public
     */
    public function __construct($charges, $discounts, $grossPrice, $markups, $priceBeforeCharges, $priceBeforeTax, $taxes)
    {
      parent::__construct();
      $this->charges = $charges;
      $this->discounts = $discounts;
      $this->grossPrice = $grossPrice;
      $this->markups = $markups;
      $this->priceBeforeCharges = $priceBeforeCharges;
      $this->priceBeforeTax = $priceBeforeTax;
      $this->taxes = $taxes;
    }

}
