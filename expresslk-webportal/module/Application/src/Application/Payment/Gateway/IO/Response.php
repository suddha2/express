<?php


namespace Application\Payment\Gateway\IO;


use Api\Client\Soap\Core\PaymentRefundMode;

class Response
{
    private $isSuccess = false;
    private $bookingReference;
    private $ipgReference;
    private $error = array();
    private $paymentMode;
    private $paidAmount = 0;
    private $actualAmount = null;
    private $actualCurrency = null;
    private $extraData = null;
	

    /**
     * @return boolean
     */
    public function isSuccess()
    {
        return $this->isSuccess;
    }

    /**
     * @param boolean $isSuccess
     * @return Response
     */
    public function setSuccess($isSuccess)
    {
        $this->isSuccess = $isSuccess;
        return $this;
    }

    /**
     * @return mixed
     */
    public function getBookingReference()
    {
        return $this->bookingReference;
    }

    /**
     * @param mixed $bookingReference
     * @return Response
     */
    public function setBookingReference($bookingReference)
    {
        $this->bookingReference = $bookingReference;
        return $this;
    }

    /**
     * @return mixed
     */
    public function getIpgReference()
    {
        return $this->ipgReference;
    }

    /**
     * @param mixed $ipgReference
     * @return Response
     */
    public function setIpgReference($ipgReference)
    {
        $this->ipgReference = $ipgReference;
        return $this;
    }

    /**
     * @return array
     */
    public function getErrors()
    {
        return $this->error;
    }

    /**
     * @param mixed $error
     * @return Response
     */
    public function setError($error)
    {
        $this->error[] = $error;
        return $this;
    }

    /**
     * @return PaymentRefundMode
     */
    public function getPaymentMode()
    {
        return $this->paymentMode;
    }

    /**
     * @param PaymentRefundMode $paymentMode
     * @return Response
     */
    public function setPaymentMode($paymentMode)
    {
        $this->paymentMode = $paymentMode;
        return $this;
    }

    /**
     * @return int
     */
    public function getPaidAmount()
    {
        return $this->paidAmount;
    }

    /**
     * @param int $paidAmount
     * @return Response
     */
    public function setPaidAmount($paidAmount)
    {
        $this->paidAmount = $paidAmount;
        if ($this->actualAmount === null) {
            $this->actualAmount = $this->paidAmount;
        }
        if ($this->actualCurrency === null) {
            $this->actualCurrency = 'LKR';
        }
        return $this;
    }

    /**
     * @return null|double
     */
    public function getActualAmount()
    {
        return $this->actualAmount;
    }

    /**
     * @param double $actualAmount
     * @return Response
     */
    public function setActualAmount($actualAmount)
    {
        $this->actualAmount = $actualAmount;
        return $this;
    }

    /**
     * @return string
     */
    public function getActualCurrency()
    {
        return $this->actualCurrency;
    }

    /**
     * @param string $actualCurrency
     * @return Response
     */
    public function setActualCurrency($actualCurrency)
    {
        $this->actualCurrency = $actualCurrency;
        return $this;
    }
	
	public function setExtraData($extraData){
		$this->extraData=$extraData;
	}
	public function getExtraData(){
		return $this->extraData;
	}
}