<?php

namespace Application\Payment\Gateway\PayPal;

class DoExpressCheckoutPayment extends \SpeckPaypal\Request\DoExpressCheckoutPayment {

    protected $paymentRequest_n_allowedPaymentMethod;

    public function setPaymentRequest_n_allowedPaymentMethod($method)
    {
        $this->paymentRequest_n_allowedPaymentMethod = $method;
    }

    public function getPaymentRequest_n_allowedPaymentMethod()
    {
        return $this->paymentRequest_n_allowedPaymentMethod;
    }
}
