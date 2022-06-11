<?php
/**
 * Created by PhpStorm.
 * User: win 8.1
 * Date: 6/1/2015
 * Time: 1:10 PM
 */

namespace Api\Manager;


class Booking extends Base{

    const STATUS_CODE_CONFIRM = 'CONF';
    const STATUS_CODE_CANCELLED = 'CANC';
    const STATUS_CODE_TENTATIVE = 'TENT';

    /**
     * @param $reference
     * @return \Api\Client\Soap\Core\Booking
     * @throws \Exception
     */
    public function getBookingRefById($reference)
    {
        $bus = new \Api\Client\Rest\Model\Booking($this->getServiceManager());
        return $bus->getBookingRefById($reference);
    }

}