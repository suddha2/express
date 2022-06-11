<?php
namespace Api;

use Api\Client\Mobitel\CGWService;
use Api\Client\Soap\Core\ExpressLKSearchImplService;
use Api\Client\Soap\Core\PaymentRefund;
use Api\Client\Soap\ReservationService;
use Api\Client\Soap\ReportService;
use Api\Manager\Authentication;
use Api\Manager\Booking;
use Api\Manager\Booking\Cancellation;
use Api\Manager\Booking\ConfirmBooking;
use Api\Manager\Company;
use Api\Manager\Reports\ReportManager;
use Api\Manager\Booking\Payment;
use Api\Manager\Bus;
use Api\Manager\BusLight;
use Api\Manager\Schedule;
use Api\Manager\Schedule\Availability;
use Api\Manager\Schedule\Hold;
use Api\Manager\Schedule\Search;
use Api\Manager\Session;
use Api\Manager\UserLight;
use Api\Manager\Supplier;

class Module
{
    public function getConfig()
    {
        return include __DIR__ . '/config/module.config.php';
    }

    public function getAutoloaderConfig()
    {
        return array(
            'Zend\Loader\StandardAutoloader' => array(
                'namespaces' => array(
                    __NAMESPACE__ => __DIR__ . '/src/' . __NAMESPACE__,
                ),
            ),
        );
    }

    public function getServiceConfig()
    {

        return array(
            'factories' => array(
                //Search service
                'Api\SearchService' => function ($sm) {
                    //create wsdl reader service - array('trace' => 1, 'exceptions'=> false)
                    $soapService = new ReservationService($sm);

                    return $soapService;
                },
                //Report service
                'Api\ReportService' => function ($sm) {
                    //create wsdl reader service - array('trace' => 1, 'exceptions'=> false)
                    $reportService = new ReportService($sm);
                    return $reportService;
                },
                //Mobitel Gateway service
                'Api\Mobitel\CGW' => function ($sm) {
                    return new CGWService($sm);
                },

                //session object
                'Api\Manager\Session' => function ($sm) {
                    $session = new Session($sm);
                    return $session;
                },
                //Authentication object
                'Api\Manager\Authentication' => function ($sm) {
                    $auth = new Authentication($sm);
                    return $auth;
                },

                // schedule section
                'Api\Manager\Schedule' => function ($sm) {
                    return new Schedule($sm);
                },
                'Api\Manager\OperationalSchedule' => function ($sm) {
                    return new Schedule\OperationalSchedule($sm);
                },
                //search obejct
                'Api\Manager\Schedule\Search' => function ($sm) {
                    $search = new Search($sm);
                    return $search;
                },
                //availability check object
                'Api\Manager\Schedule\Availability' => function ($sm) {
                    $searchAvailability = new Availability($sm);
                    return $searchAvailability;
                },
                //availability check object
                'Api\Manager\Schedule\Hold' => function ($sm) {
                    $hold = new Hold($sm);
                    return $hold;
                },
                //booking confirmation object
                'Api\Manager\Booking\ConfirmBooking' => function ($sm) {
                    $hold = new ConfirmBooking($sm);
                    return $hold;
                },
                //booking confirmation object
                'Api\Manager\Booking\Payment' => function ($sm) {
                    $pay = new Payment($sm);
                    return $pay;
                },

                //Bus manager object
                'Api\Manager\Bus' => function ($sm) {
                    $pay = new Bus($sm);
                    return $pay;
                },
                'Api\Manager\BusLight' => function ($sm) {
                    $bl = new BusLight($sm);
                    return $bl;
                },
				
				'Api\Manager\Supplier' => function ($sm) {
                    $sup = new Supplier($sm);
                    return $sup;
                },
                'Api\Manager\UserLight' => function ($sm) {
                    $u = new UserLight($sm);
                    return $u;
                },

                //booking cancellation object
                'Api\Manager\Booking\Cancellation' => function ($sm) {
                    $cancel = new Cancellation($sm);
                    return $cancel;
                },
                //booking confirmation object
                'Api\Manager\Booking' => function ($sm) {
                    $booking = new Booking($sm);
                    return $booking;
                },
                //reports
                'Api\Manager\Reports\ReportManager' => function ($sm) {
                    $refund = new ReportManager($sm);
                    return $refund;
                },

                'Api\Manager\Company' => function ($sm) {
                    return new Company($sm);
                },
            )
        );
    }
}
