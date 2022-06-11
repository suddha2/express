<?php


namespace Application;


use Zend\Session\Container;

class Discount extends InjectorBase
{
    const SESSION_NAME = 'DicountCodeSession';

    const DISCOUNT_CODE = 'DISCOUNT_CODE';

    private $_sessionObject;

    public function __construct($serviceManager)
    {
        parent::__construct($serviceManager);
        //initialize session
        $this->_sessionObject = new Container(self::SESSION_NAME);
    }

    /**
     * Save current discount code in session
     * @param string $discountCode
     * @return $this
     */
    public function setCurrentDiscountCode($discountCode)
    {
        $this->_sessionObject->offsetSet(self::DISCOUNT_CODE, $discountCode);
        return $this;
    }

    /**
     * Get the discount code if one exists
     * @return bool|mixed
     */
    public function getCurrentDiscountCode()
    {
        if(!$this->_sessionObject->offsetExists(self::DISCOUNT_CODE)){
            //no discount exists
            return false;
        }

        return $this->_sessionObject->offsetGet(self::DISCOUNT_CODE);
    }

    /**
     * Mark a discount code as already used.
     */
    public function discountCodeUsed()
    {
        //clear discount code from session
        $this->_sessionObject->offsetUnset(self::DISCOUNT_CODE);
    }
}