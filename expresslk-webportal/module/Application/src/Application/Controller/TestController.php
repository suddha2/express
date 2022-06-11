<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 7/28/14
 * Time: 12:07 AM
 */

namespace Application\Controller;


use Api\Client\Rest\Model\City;
use Api\Manager\Reports\ReportManager;
use Api\Manager\Session;
use Api\Operation\Request\QueryCriteria;
use Application\Domain;
use Application\Model\Email;
use Application\Sms\Template;
use Zend\Db\Adapter\Adapter;
use Zend\Debug\Debug;
use Zend\Mime\Mime;
use Zend\Mime\Part;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\JsonModel;
use Zend\View\Model\ViewModel;
use Api\Client\Rest\Factory as RestFactory;

class TestController extends AbstractActionController{

    /**
     * This action is listened from the load balancer to check the service status
     * If this returns 404, LB considers this as a failed instance
     */
    public function healthAction()
    {
        try {
            //check front end database
            /** @var Adapter $oDb */
            $oDb = $this->getServiceLocator()->get('Zend\Db\Adapter\Adapter');
            $oDb->getDriver()->getConnection();

            //check REST service
            $oHeart = new RestFactory($this->getServiceLocator(), 'heartBeat');
            $oCityResult = $oHeart->getList(new QueryCriteria());

        } catch (\Exception $e) {
            //return 404 error if any of the services are not working
            $this->getResponse()->setStatusCode(404);
            return;
        }
        return new JsonModel(array());
    }

    public function emailAction()
    {
        /* @var $email \Application\Model\Email */
        try {
            $email = $this->getServiceLocator()->get('Email');
            $email->addTo('udanthaya@gmail.com')
                ->setLayout(Email::TEMPLATE_BOOKING_SUCCESS)
                ->setSubject('Congratulations! Testing Email')
                ->setBodyHtml(array(
                    '::bookingNo::' => 'no',
                    '::departCity::' => 'no',
                    '::arrivalCity::' => 'no',
                    '::date::' => 'no',
                    '::departureTime::' => 'no',
                    '::boardingLocation::' => 'no',
                    '::beThereAt::' => 'no',
                    '::travels::' => 'no',
                    '::busNumber::' => 'no',
                    '::seatNumber::' => 'no',
                    '::totalAmount::' => 'no',
                    '::cost::' => 'no',
                    '::bookingCharges::' => 'no',
                    '::tax::' => 'no',
                ));
            //parts
            $cal = 'BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//hacksw/handcal//NONSGML v1.0//EN
CALSCALE:GREGORIAN
BEGIN:VEVENT
DTEND:'. date('Y-m-d') .'
UID:'. uniqid() .'
DTSTAMP:'. date('Ymd\THis\Z') .'
LOCATION:'. 'Basthiyan Mawatha' .'
DESCRIPTION:'. 'Description of the event' .'
URL;VALUE=URI:http://www.busbooking.lk
SUMMARY:'. 'Summary description' .'
DTSTART:'. date('Ymd\THis\Z') .'
END:VEVENT
END:VCALENDAR';
            $att = new Part($cal);
            $att->filename = 'BookingCal.ics';
            $att->type = 'text/calendar';
            $email->addPart($att);

            $oView = new ViewModel();
            $oView->setVariables(array(
                'beThereAt'         => '21:00',
                'travels'           => 'Super Luxury Service' ,
                'busNumber'         => 'NC-3764',
                'departCity'        => 'Soysapura',
                'boardingLocation'  => 'Technical Junction - Colombo 11',
                'seatNumber'        => '44',
                'seatType'          => 'Adjustable/Sleeparate',
                'departureTime'     => '20:00',
                'bookingNo'         => '80134',
                'routeName'         => 'SOYSAPURA-JAFFNA',
                'date'              => '31st March, 2016',
                'arrivalCity'       => 'Jaffna',
                'sequenceNo'        => 'LKR 1360',
                'busType'           => 'Super Luxury Service',
            ));
            $oView->setTerminal(true);
            $oView->setTemplate('application/index/partial/ticket');

            $viewRender = $this->getServiceLocator()->get('ViewRenderer');
            $oDomPdf = new \DOMPDF();
            $oDomPdf->load_html($viewRender->render($oView));
            $oDomPdf->set_paper('A5', 'landscape');
            $oDomPdf->render();
            $oPdfString = $oDomPdf->output();
            //add attachement as pdf
            $attachment = new Part($oPdfString);
            $attachment->filename = "BBK-eTicket-";
            $attachment->type = 'application/pdf';
            $attachment->encoding = Mime::ENCODING_BASE64;
            $attachment->disposition = Mime::DISPOSITION_ATTACHMENT;
            $email->addPart($attachment);

            $email->send();
            echo 'Sent email';
        } catch (\Exception $e) {
            echo $e->getMessage();
        }
        exit;
    }
    public function testAction()
    {
//        $oData = $this->getServiceLocator()->get('DataStore');
//        $oData->setItem('data', 'data');
//        var_dump($oData->getItem('data'));
//        die;

        try {
            /* @var $oSms \Application\Sms\Client */
            $oSms = $this->getServiceLocator()->get('Sms');
            $aSmsData = array(
                '::SITE_NAME::' => Domain::getDomain(),
                '::PLATE_NO::' => 'NE-3865',
                '::DATE::' => '2016-02-05',
                '::FROM_NAME::' => 'Colombo (Technical)',
                '::DEPARTURE_TIME::' => '09.00P.M.',
                '::TO_NAME::' => 'Kalmunei',
                '::SEAT_NO::' => '29,30',
                '::TICKET_NO::' => 'B3B667',
                '::FEE::' => '2000',
            );
            $oSms->sendSMS('0773641464', Template::BOOKING_SUCCESS, $aSmsData);
        } catch (\Exception $e) {
            var_dump($e->getMessage());
        }
        die('Sent');
    }

    public function ipgAction()
    {
        /** @var \Application\Payment\Gateway\HNBipg $oHnb */
        $oHnb = $this->getServiceLocator()->get('Payment\Hnb');
        $oHnb->setReturnUrl('/app/test/ipgreturn');
        $oIpgRequest = $oHnb->getEncryptedRequest('EEEEEEE', 10, 'Test transaction from BBK');
        $form = $oIpgRequest->getRequestPayload();
        echo '<html><head></head><body>'. $form .'</body><script>setTimeout(function(){ document.getElementById("__form__").submit() }, 2000)</script></html>';
        die;
    }

    public function ipgreturnAction()
    {
        Debug::dump($_POST, 'Post response');
        Debug::dump($_GET, 'Get response');
        /** @var \Application\Payment\Gateway\HNBipg $oHnb */
        $oHnb = $this->getServiceLocator()->get('Payment\Hnb');
        //var_dump($oHnb->getDecryptedResponse($_REQUEST));
        die;
    }

    public function teststorageAction()
    {
        /* @var $oDataStore \Zend\Cache\Storage\StorageInterface */
        $oDataStore = $this->getServiceLocator()->get('DataStore');
        $oDataStore->setItem('data', 'This is the actual data');
        die;
    }

    public function testticketAction()
    {
        $oView = new ViewModel();
        $oView->setVariables(array(
            'beThereAt'         => '21:00',
            'travels'           => 'Super Luxury Service' ,
            'busNumber'         => 'NC-3764',
            'departCity'        => 'Soysapura',
            'boardingLocation'  => 'Technical Junction - Colombo 11',
            'seatNumber'        => '44',
            'seatType'          => 'Adjustable/Sleeparate',
            'departureTime'     => '20:00',
            'bookingNo'         => '80134',
            'routeName'         => 'SOYSAPURA-JAFFNA',
            'date'              => '31st March, 2016',
            'arrivalCity'       => 'Jaffna',
            'sequenceNo'        => 'LKR 1360',
            'busType'           => 'Super Luxury Service',
        ));
        $oView->setTerminal(true);
        $oView->setTemplate('application/index/partial/ticket');

        //return $oView;

        $viewRender = $this->getServiceLocator()->get('ViewRenderer');
        $oDomPdf = new \DOMPDF();
        $oDomPdf->load_html($viewRender->render($oView));
        $oDomPdf->set_paper('A5', 'landscape');
        $oDomPdf->render();
        $oPdfString = $oDomPdf->output();
        header("Content-Type: application/pdf");
        echo $oPdfString;
        die;
    }
} 