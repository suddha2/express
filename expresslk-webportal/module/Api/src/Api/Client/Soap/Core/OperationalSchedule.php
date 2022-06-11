<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class OperationalSchedule extends Entity
{
    // schedule
    public $id;
    public $arrivalTime;
    public $departureTime;
    public $stage;

    // route
    public $routeId;
    public $fromCityId;
    public $routeName;
    public $routeNumber;
    public $routeNumberName;

    // bus
    public $plateNumber;

    // driver, conductor
    public $conductorName;
    public $conductorMobile;
    public $driverName;
    public $driverMobile;

    // derived
    public $seatReservations;
    public $totalCost;

    // seating profile
    public $seatingProfileId;

    public $divisionId;
    public $allowedDivisions;
}