<?php


namespace Legacy\Extend\Mobitel;


use Legacy\Extend\ProviderBase;

class MobitelCustomerProvider extends ProviderBase
{
    private $_phoneNumber;

    /**
     * @return mixed
     */
    public function getPhoneNumber()
    {
        return $this->_phoneNumber;
    }

    /**
     * @param mixed $phoneNumber
     * @return MobitelCustomerProvider
     */
    public function setPhoneNumber($phoneNumber)
    {
        $this->_phoneNumber = $phoneNumber;
        return $this;
    }


}