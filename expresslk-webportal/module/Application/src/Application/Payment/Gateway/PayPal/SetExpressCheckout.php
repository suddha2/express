<?php

namespace Application\Payment\Gateway\PayPal;

class SetExpressCheckout extends \SpeckPaypal\Request\SetExpressCheckout {

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
