<?php
/**
 * Created by PhpStorm.
 * User: win 8.1
 * Date: 6/1/2015
 * Time: 1:11 PM
 */

namespace Api\Client\Rest\Model;

use Api\Client\Rest\Factory;
use Api\Operation\Request\QueryCriteria;
use Api\Client\Soap\Core\Entity;

class Booking extends Factory {

    public function __construct($serviceManager)
    {
        parent::__construct($serviceManager, 'booking');
    }

    /**
     * @param $reference
     * @return \Api\Client\Soap\Core\Booking
     * @throws \Exception
     */
    public function getBookingRefById($reference)
    {
        $url = $this->getBaseUrl() . '/booking?reference=' . $reference;
        $response = $this->get($url);

        $result = $response->body;
        if(is_array($result) && count($result)>0){
            return array_pop($result);
        }else{
            throw new \Exception('Malformed booking reference result.');
        }
    }

    public function getList(QueryCriteria $criteria)
    {
        $response = parent::getList($criteria);
        $this->handleResponse($response->body);
        return $response;
    }

    public function getOne($id)
    {
        $response = parent::getOne($id);
        $this->handleResponse($response->body);
        return $response;
    }

    private function handleResponse(&$objects)
    {
        return $objects;

        // we are no longer using this response handling
        if (is_array($objects)) {
            foreach ($objects as $booking) {
                $this->handleBooking($booking);
            }
        } else {
            $this->handleBooking($objects);
        }
        return $objects;
    }

    private function handleBooking(&$booking)
    {
        foreach ($booking as $value) {
            $this->organize($value);
        }
        return $booking;
    }

    private function organize(&$value)
    {
        if (is_object($value)) {
            if (isset($value->id)) {
                unset($value->id);
            }
            foreach ($value as $v) {
                $this->organize($v);
            }
        } elseif (is_array($value)) {
            foreach ($value as $v) {
                $this->organize($v);
            }
        }
    }
}