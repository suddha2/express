<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 9/14/14
 * Time: 11:13 AM
 */

namespace Admin\Controller;

use Api\Client\Rest\Model\City;
use Api\Client\Soap\Core\PaymentRefundCriteria;
use Api\Client\Soap\Core\PaymentRefundMode;
use Api\Client\Soap\Core\PaymentRefundType;
use Api\Manager\Base;
use Api\Manager\Booking;
use Api\Manager\Booking\Cancellation;
use Application\Util\Language;
use DOMPDFModule\View\Model\PdfModel;
use DOMPDFModule\View\Renderer\PdfRenderer;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\JsonModel;
use Zend\View\Model\ViewModel;
use Zend\View\Renderer\PhpRenderer;

class TicketboxController extends AbstractActionController
{

    public function indexAction()
    {
//        $renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
//        $headScript = $this->getServiceLocator()->get('viewhelpermanager')->get('HeadScript');
//        $headLink = $this->getServiceLocator()->get('viewhelpermanager')->get('headLink');
//
//        $renderer->headTitle('TicketBox - Enter ticket info');
//        //init script
//        $headScript->appendFile('/min/ticketbox.min.js');

        //get city list from service
//        $oCities = new City($this->getServiceLocator());
//        $aCityList = $oCities->getTerminusCityList();
//        //create html view
//        $oView = new ViewModel();
//        $oView->setTerminal(true);
//        $oView->setVariable(
//            'allCities',
//            $aCityList
//        );
//
//        return $oView;
    }

    public function printticketAction()
    {
        $data = $this->params()->fromQuery('data', '');
        $vars = json_decode(base64_decode($data));

        $view = new ViewModel();
        $view->setTerminal(true);
        $view->setVariable('pages', $vars);

        return $view;
    }

    public function cancellationAction()
    {
        $view = new ViewModel();
        $view->setTerminal(true);

        return $view;
    }

    public function cancelbookingAction()
    {
        $result = array();
        $reqestData = $this->params()->fromJson();
        $mode = $reqestData['mode'];
        $referenceNo = $reqestData['ref'];

        try {
            if ($mode == 'getBooking') {
                /** @var $booking \Api\Manager\Booking */
                $booking = $this->getServiceLocator()->get('Api\Manager\Booking');

                $bookingResult = $booking->getBookingRefById($referenceNo);

                if ($bookingResult->status->code == Booking::STATUS_CODE_CANCELLED) {
                    throw new \Exception('Booking is already cancelled');
                }
				if ($bookingResult->status->code == Booking::STATUS_CODE_TENTATIVE) {
                    throw new \Exception('Booking cannot be cancelled at the moment.');
                }
                /** @var \Api\Manager\Booking\Cancellation $cancel */
                $cancel = $this->getServiceLocator()->get('Api\Manager\Booking\Cancellation');
                $charge = $cancel->cancellationCharge($bookingResult->id);

                $result['booking'] = $bookingResult;
                $result['cancellationCharge'] = $charge;
                $result['refundAmount'] = Cancellation::getRefundAmount($bookingResult, $charge);
            } elseif ($mode == 'cancelBooking') {
                $data = $reqestData['data'];
                $cancellationCause = $data['cause'];
                $cancellationremark = $data['remark'];
                $hasRefund = ($data['hasRefund'] == true);
				$cashCard = ($data['cashCard'] == true);
                if (empty($cancellationCause)) {
                    throw new \Exception('Invalid form');
                }

                /** @var $booking \Api\Manager\Booking */
                $booking = $this->getServiceLocator()->get('Api\Manager\Booking');

                $bookingResult = $booking->getBookingRefById($referenceNo);
                /** @var \Api\Manager\Booking\Cancellation $cancel */
                $cancel = $this->getServiceLocator()->get('Api\Manager\Booking\Cancellation');
                //calculate charges
                //$charge = $cancel->cancellationCharge($bookingResult->id);
                //cancel the ticket
                $return = $cancel->cancelTicket($bookingResult->id, true, $cancellationCause, $cancellationremark,$cashCard);

                //if has refund
                if ($hasRefund) {
                    $cancellationrefundMode = $data['refundMode'];
                    $cancellationrefundReference = $data['refundReference'];
                    $amount = $data['refundamount'];

                    if (empty($cancellationrefundMode)) {
                        throw new \Exception('Add a refund mode');
                    }

                    /** @var \Api\Manager\Booking\Payment $refund */
                    $refund = $this->getServiceLocator()->get('Api\Manager\Booking\Payment');
                    /** @var PaymentRefundCriteria $criteria */
                    $criteria = Base::getEntityObject('PaymentRefundCriteria');
                    $criteria->amount = $amount;
                    $criteria->actualAmount = $amount;
                    $criteria->actualCurrency = 'LKR';
                    $criteria->bookingId = $bookingResult->id;
                    $criteria->mode = $cancellationrefundMode;
                    $criteria->reference = $cancellationrefundReference;
                    $criteria->type = PaymentRefundType::Refund;

                    $refund->addPaymentRefund($criteria);
                }

            }
        } catch (\Exception $e) {
            $result['error'] = $e->getMessage();
        }
        return new JsonModel($result);
    }
}
