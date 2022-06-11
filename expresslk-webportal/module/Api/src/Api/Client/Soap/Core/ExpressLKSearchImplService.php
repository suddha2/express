<?php

namespace Api\Client\Soap\Core;

include_once('Filter.php');
include_once('AmenityFilter.php');
include_once('AvailabilityFilter.php');
include_once('BusFilter.php');
include_once('ScheduleFilter.php');
include_once('TimeFilter.php');
include_once('TransitFilter.php');
include_once('TravelClassFilter.php');
include_once('LegFilterData.php');
include_once('FilterData.php');
include_once('FilterValue.php');
include_once('UserLight.php');
include_once('Person.php');
include_once('Division.php');
include_once('Company.php');
include_once('User.php');
include_once('UserGroup.php');
include_once('Permission.php');
include_once('Module.php');
include_once('PermissionGroup.php');
include_once('PermissionSingle.php');
include_once('AgentAllocation.php');
include_once('BookingItemCancellation.php');
include_once('CancellationCriteria.php');
include_once('ItemCancellationCriteria.php');
include_once('CancellationChargeResponse.php');
include_once('CancellationResponse.php');
include_once('CancellationCause.php');
include_once('BusScheduleLight.php');
include_once('BusSchedule.php');
include_once('BusScheduleBusStop.php');
include_once('City.php');
include_once('Entity.php');
include_once('LightEntity.php');
include_once('BusLight.php');
include_once('Bus.php');
include_once('Amenity.php');
include_once('BusBusRoute.php');
include_once('BusRoute.php');
include_once('BusRouteBusStop.php');
include_once('BusStop.php');
include_once('BusType.php');
include_once('SeatingProfile.php');
include_once('TravelClass.php');
include_once('BusImage.php');
include_once('OperationalStage.php');
include_once('Coupon.php');
include_once('Booking.php');
include_once('Agent.php');
include_once('BookingItem.php');
include_once('Change.php');
include_once('ChangeType.php');
include_once('BookingItemCharge.php');
include_once('BookingItemDiscount.php');
include_once('BookingItemMarkup.php');
include_once('BookingItemPassenger.php');
include_once('BookingStatus.php');
include_once('BookingItemTax.php');
include_once('Client.php');
include_once('AccountStatus.php');
include_once('Passenger.php');
include_once('Payment.php');
include_once('PaymentRefund.php');
include_once('Refund.php');
include_once('AdvisoryNote.php');
include_once('BusSeat.php');
include_once('Gender.php');
include_once('NotificationMethod.php');
include_once('PaymentRefundMode.php');
include_once('BusImageType.php');
include_once('PassengerType.php');
include_once('PaymentRefundType.php');
include_once('VendorPaymentRefundMode.php');
include_once('EntityArray.php');
include_once('ExpResponse.php');
include_once('SearchCriteria.php');
include_once('BaseCriteria.php');
include_once('LegCriteria.php');
include_once('SearchResponse.php');
include_once('SearchResult.php');
include_once('LegResult.php');
include_once('ResultLeg.php');
include_once('Result.php');
include_once('BaseCostPrice.php');
include_once('ResultSector.php');
include_once('CostPrice.php');
include_once('charges.php');
include_once('entry.php');
include_once('discounts.php');
include_once('markups.php');
include_once('taxes.php');
include_once('ConfirmationCriteria.php');
include_once('ClientDetail.php');
include_once('PersonDetail.php');
include_once('SeatAllocations.php');
include_once('Allocation.php');
include_once('PassengerDetail.php');
include_once('PaymentCriteria.php');
include_once('PaymentRefundCriteria.php');
include_once('RefundCriteria.php');
include_once('PaymentMethodCriteria.php');
include_once('ConfirmResponse.php');
include_once('PaymentRefundsResponse.php');
include_once('JourneyPerformedCriteria.php');
include_once('ReleaseResponse.php');
include_once('HoldCriteria.php');
include_once('SeatCriteria.php');
include_once('HoldResponse.php');
include_once('HoldResult.php');
include_once('SessionCriteria.php');
include_once('AvailabilityCriteria.php');
include_once('AvailabilityResponse.php');
include_once('AvailabilityResult.php');
include_once('Reservation.php');
include_once('RepriceCriteria.php');
include_once('PaymentMethod.php');
include_once('BookingLight.php');
include_once('BusRouteLight.php');
include_once('ApplicationType.php');
include_once('Rule.php');
include_once('RuleCondition.php');
include_once('bookingItemCancellationData.php');
include_once('heldItem.php');
include_once('Conductor.php');
include_once('Driver.php');
include_once('Supplier.php');
include_once('SupplierAccount.php');
include_once('SupplierContactPerson.php');
include_once('SupplierGroup.php');
include_once('AccountType.php');
include_once('intArray.php');

class ExpressLKSearchImplService extends \SoapClient
{

    /**
     * @var array $classmap The defined classes
     * @access private
     */
    private static $classmap = array(
      'Filter' => 'Api\Client\Soap\Core\Filter',
      'AmenityFilter' => 'Api\Client\Soap\Core\AmenityFilter',
      'AvailabilityFilter' => 'Api\Client\Soap\Core\AvailabilityFilter',
      'BusFilter' => 'Api\Client\Soap\Core\BusFilter',
      'ScheduleFilter' => 'Api\Client\Soap\Core\ScheduleFilter',
      'TimeFilter' => 'Api\Client\Soap\Core\TimeFilter',
      'TransitFilter' => 'Api\Client\Soap\Core\TransitFilter',
      'TravelClassFilter' => 'Api\Client\Soap\Core\TravelClassFilter',
      'LegFilterData' => 'Api\Client\Soap\Core\LegFilterData',
      'FilterData' => 'Api\Client\Soap\Core\FilterData',
      'FilterValue' => 'Api\Client\Soap\Core\FilterValue',
      'UserLight' => 'Api\Client\Soap\Core\UserLight',
      'Person' => 'Api\Client\Soap\Core\Person',
      'Division' => 'Api\Client\Soap\Core\Division',
      'Company' => 'Api\Client\Soap\Core\Company',
      'User' => 'Api\Client\Soap\Core\User',
      'UserGroup' => 'Api\Client\Soap\Core\UserGroup',
      'Permission' => 'Api\Client\Soap\Core\Permission',
      'Module' => 'Api\Client\Soap\Core\Module',
      'PermissionGroup' => 'Api\Client\Soap\Core\PermissionGroup',
      'PermissionSingle' => 'Api\Client\Soap\Core\PermissionSingle',
      'AgentAllocation' => 'Api\Client\Soap\Core\AgentAllocation',
      'BookingItemCancellation' => 'Api\Client\Soap\Core\BookingItemCancellation',
      'CancellationCriteria' => 'Api\Client\Soap\Core\CancellationCriteria',
      'ItemCancellationCriteria' => 'Api\Client\Soap\Core\ItemCancellationCriteria',
      'CancellationChargeResponse' => 'Api\Client\Soap\Core\CancellationChargeResponse',
      'CancellationResponse' => 'Api\Client\Soap\Core\CancellationResponse',
      'BusScheduleLight' => 'Api\Client\Soap\Core\BusScheduleLight',
      'BusSchedule' => 'Api\Client\Soap\Core\BusSchedule',
      'BusScheduleBusStop' => 'Api\Client\Soap\Core\BusScheduleBusStop',
      'City' => 'Api\Client\Soap\Core\City',
      'Entity' => 'Api\Client\Soap\Core\Entity',
      'LightEntity' => 'Api\Client\Soap\Core\LightEntity',
      'BusLight' => 'Api\Client\Soap\Core\BusLight',
      'Bus' => 'Api\Client\Soap\Core\Bus',
      'Amenity' => 'Api\Client\Soap\Core\Amenity',
      'BusBusRoute' => 'Api\Client\Soap\Core\BusBusRoute',
      'BusRoute' => 'Api\Client\Soap\Core\BusRoute',
      'BusRouteBusStop' => 'Api\Client\Soap\Core\BusRouteBusStop',
      'BusStop' => 'Api\Client\Soap\Core\BusStop',
      'BusType' => 'Api\Client\Soap\Core\BusType',
      'SeatingProfile' => 'Api\Client\Soap\Core\SeatingProfile',
      'TravelClass' => 'Api\Client\Soap\Core\TravelClass',
      'BusImage' => 'Api\Client\Soap\Core\BusImage',
      'OperationalStage' => 'Api\Client\Soap\Core\OperationalStage',
      'Coupon' => 'Api\Client\Soap\Core\Coupon',
      'Booking' => 'Api\Client\Soap\Core\Booking',
      'Agent' => 'Api\Client\Soap\Core\Agent',
      'BookingItem' => 'Api\Client\Soap\Core\BookingItem',
      'Change' => 'Api\Client\Soap\Core\Change',
      'ChangeType' => 'Api\Client\Soap\Core\ChangeType',
      'BookingItemCharge' => 'Api\Client\Soap\Core\BookingItemCharge',
      'BookingItemDiscount' => 'Api\Client\Soap\Core\BookingItemDiscount',
      'BookingItemMarkup' => 'Api\Client\Soap\Core\BookingItemMarkup',
      'BookingItemPassenger' => 'Api\Client\Soap\Core\BookingItemPassenger',
      'BookingStatus' => 'Api\Client\Soap\Core\BookingStatus',
      'BookingItemTax' => 'Api\Client\Soap\Core\BookingItemTax',
      'Client' => 'Api\Client\Soap\Core\Client',
      'AccountStatus' => 'Api\Client\Soap\Core\AccountStatus',
      'Passenger' => 'Api\Client\Soap\Core\Passenger',
      'Payment' => 'Api\Client\Soap\Core\Payment',
      'PaymentRefund' => 'Api\Client\Soap\Core\PaymentRefund',
      'Refund' => 'Api\Client\Soap\Core\Refund',
      'AdvisoryNote' => 'Api\Client\Soap\Core\AdvisoryNote',
      'BusSeat' => 'Api\Client\Soap\Core\BusSeat',
      'EntityArray' => 'Api\Client\Soap\Core\EntityArray',
      'ExpResponse' => 'Api\Client\Soap\Core\ExpResponse',
      'SearchCriteria' => 'Api\Client\Soap\Core\SearchCriteria',
      'BaseCriteria' => 'Api\Client\Soap\Core\BaseCriteria',
      'LegCriteria' => 'Api\Client\Soap\Core\LegCriteria',
      'SearchResponse' => 'Api\Client\Soap\Core\SearchResponse',
      'SearchResult' => 'Api\Client\Soap\Core\SearchResult',
      'LegResult' => 'Api\Client\Soap\Core\LegResult',
      'ResultLeg' => 'Api\Client\Soap\Core\ResultLeg',
      'Result' => 'Api\Client\Soap\Core\Result',
      'BaseCostPrice' => 'Api\Client\Soap\Core\BaseCostPrice',
      'ResultSector' => 'Api\Client\Soap\Core\ResultSector',
      'CostPrice' => 'Api\Client\Soap\Core\CostPrice',
      'charges' => 'Api\Client\Soap\Core\charges',
      'entry' => 'Api\Client\Soap\Core\entry',
      'discounts' => 'Api\Client\Soap\Core\discounts',
      'markups' => 'Api\Client\Soap\Core\markups',
      'taxes' => 'Api\Client\Soap\Core\taxes',
      'ConfirmationCriteria' => 'Api\Client\Soap\Core\ConfirmationCriteria',
      'ClientDetail' => 'Api\Client\Soap\Core\ClientDetail',
      'PersonDetail' => 'Api\Client\Soap\Core\PersonDetail',
      'SeatAllocations' => 'Api\Client\Soap\Core\SeatAllocations',
      'Allocation' => 'Api\Client\Soap\Core\Allocation',
      'PassengerDetail' => 'Api\Client\Soap\Core\PassengerDetail',
      'PaymentCriteria' => 'Api\Client\Soap\Core\PaymentCriteria',
      'PaymentRefundCriteria' => 'Api\Client\Soap\Core\PaymentRefundCriteria',
      'RefundCriteria' => 'Api\Client\Soap\Core\RefundCriteria',
      'PaymentMethodCriteria' => 'Api\Client\Soap\Core\PaymentMethodCriteria',
      'ConfirmResponse' => 'Api\Client\Soap\Core\ConfirmResponse',
      'PaymentRefundsResponse' => 'Api\Client\Soap\Core\PaymentRefundsResponse',
      'JourneyPerformedCriteria' => 'Api\Client\Soap\Core\JourneyPerformedCriteria',
      'ReleaseResponse' => 'Api\Client\Soap\Core\ReleaseResponse',
      'HoldCriteria' => 'Api\Client\Soap\Core\HoldCriteria',
      'SeatCriteria' => 'Api\Client\Soap\Core\SeatCriteria',
      'HoldResponse' => 'Api\Client\Soap\Core\HoldResponse',
      'HoldResult' => 'Api\Client\Soap\Core\HoldResult',
      'SessionCriteria' => 'Api\Client\Soap\Core\SessionCriteria',
      'AvailabilityCriteria' => 'Api\Client\Soap\Core\AvailabilityCriteria',
      'AvailabilityResponse' => 'Api\Client\Soap\Core\AvailabilityResponse',
      'AvailabilityResult' => 'Api\Client\Soap\Core\AvailabilityResult',
      'Reservation' => 'Api\Client\Soap\Core\Reservation',
      'RepriceCriteria' => 'Api\Client\Soap\Core\RepriceCriteria',
      'BookingLight' => 'Api\Client\Soap\Core\BookingLight',
      'BusRouteLight' => 'Api\Client\Soap\Core\BusRouteLight',
      'Rule' => 'Api\Client\Soap\Core\Rule',
      'RuleCondition' => 'Api\Client\Soap\Core\RuleCondition',
      'bookingItemCancellationData' => 'Api\Client\Soap\Core\bookingItemCancellationData',
      'heldItem' => 'Api\Client\Soap\Core\heldItem',
      'Conductor' => 'Api\Client\Soap\Core\Conductor',
      'Driver' => 'Api\Client\Soap\Core\Driver',
      'Supplier' => 'Api\Client\Soap\Core\Supplier',
      'SupplierAccount' => 'Api\Client\Soap\Core\SupplierAccount',
      'SupplierContactPerson' => 'Api\Client\Soap\Core\SupplierContactPerson',
      'SupplierGroup' => 'Api\Client\Soap\Core\SupplierGroup',
      'intArray' => 'Api\Client\Soap\Core\intArray');

    /**
     * @param array $options A array of config values
     * @param string $wsdl The wsdl file to use
     * @access public
     */
    public function __construct(array $options = array(), $wsdl = 'http://167.114.96.6:7575/ExpressLK-search/search?wsdl')
    {
      foreach (self::$classmap as $key => $value) {
        if (!isset($options['classmap'][$key])) {
          $options['classmap'][$key] = $value;
        }
      }
      
      parent::__construct($wsdl, $options);
    }

    /**
     * @param EntityArray $list
     * @access public
     * @return EntityArray
     */
    public function saveList(EntityArray $list)
    {
      return $this->__soapCall('saveList', array($list));
    }

    /**
     * @access public
     * @return boolean
     */
    public function heartBeat()
    {
      return $this->__soapCall('heartBeat', array());
    }

    /**
     * @param string $sessionId
     * @access public
     * @return ExpResponse
     */
    public function refreshSession($sessionId)
    {
      return $this->__soapCall('refreshSession', array($sessionId));
    }

    /**
     * @param string $sessionId
     * @param AvailabilityCriteria $criteria
     * @access public
     * @return AvailabilityResponse
     */
    public function checkAvailability($sessionId, AvailabilityCriteria $criteria)
    {
      return $this->__soapCall('checkAvailability', array($sessionId, $criteria));
    }

    /**
     * @param string $sessionId
     * @param RepriceCriteria $repriceCriteria
     * @access public
     * @return ExpResponse
     */
    public function reprice($sessionId, RepriceCriteria $repriceCriteria)
    {
      return $this->__soapCall('reprice', array($sessionId, $repriceCriteria));
    }

    /**
     * @param string $sessionId
     * @param HoldCriteria $criteria
     * @access public
     * @return HoldResponse
     */
    public function hold($sessionId, HoldCriteria $criteria)
    {
      return $this->__soapCall('hold', array($sessionId, $criteria));
    }

    /**
     * @param string $sessionId
     * @param ConfirmationCriteria $criteria
     * @access public
     * @return ConfirmResponse
     */
    public function confirm($sessionId, ConfirmationCriteria $criteria)
    {
      return $this->__soapCall('confirm', array($sessionId, $criteria));
    }

    /**
     * @param CancellationCriteria $criteria
     * @access public
     * @return CancellationChargeResponse
     */
    public function calculateCancellationCharge(CancellationCriteria $criteria)
    {
      return $this->__soapCall('calculateCancellationCharge', array($criteria));
    }

    /**
     * @param PaymentRefundCriteria $criteria
     * @access public
     * @return PaymentRefundsResponse
     */
    public function addPaymentRefund(PaymentRefundCriteria $criteria)
    {
      return $this->__soapCall('addPaymentRefund', array($criteria));
    }

    /**
     * @param JourneyPerformedCriteria $criteria
     * @access public
     * @return ExpResponse
     */
    public function markJourneyPerformed(JourneyPerformedCriteria $criteria)
    {
      return $this->__soapCall('markJourneyPerformed', array($criteria));
    }

    /**
     * @param CancellationCriteria $criteria
     * @access public
     * @return CancellationResponse
     */
    public function cancel(CancellationCriteria $criteria)
    {
      return $this->__soapCall('cancel', array($criteria));
    }

    /**
     * @param SessionCriteria $sessionCriteria
     * @access public
     * @return string
     */
    public function createSession(SessionCriteria $sessionCriteria)
    {
      return $this->__soapCall('createSession', array($sessionCriteria));
    }

    /**
     * @param Entity $t
     * @access public
     * @return Entity
     */
    public function save(Entity $t)
    {
      return $this->__soapCall('save', array($t));
    }

    /**
     * @param string $sessionId
     * @param SearchCriteria $criteria
     * @access public
     * @return SearchResponse
     */
    public function search($sessionId, SearchCriteria $criteria)
    {
      return $this->__soapCall('search', array($sessionId, $criteria));
    }

    /**
     * @param string $sessionId
     * @param SearchCriteria $criteria
     * @access public
     * @return SearchResponse
     */
    public function filter($sessionId, SearchCriteria $criteria)
    {
      return $this->__soapCall('filter', array($sessionId, $criteria));
    }

    /**
     * @param string $sessionId
     * @param intArray $itemIds
     * @access public
     * @return ReleaseResponse
     */
    public function release($sessionId, intArray $itemIds)
    {
      return $this->__soapCall('release', array($sessionId, $itemIds));
    }

}
