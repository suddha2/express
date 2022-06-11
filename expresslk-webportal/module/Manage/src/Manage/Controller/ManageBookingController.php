<?php
/**
 * Zend Framework (http://framework.zend.com/)
 *
 * @link      http://github.com/zendframework/ZendSkeletonApplication for the canonical source repository
 * @copyright Copyright (c) 2005-2014 Zend Technologies USA Inc. (http://www.zend.com)
 * @license   http://framework.zend.com/license/new-bsd New BSD License
 */

namespace Application\Controller;

use Api\Client\Rest\Model\City;
use Api\Operation\Request\QueryCriteria;
use Application\Discount;
use Application\Util\Language;
use DOMPDFModule\View\Model\PdfModel;
use DOMPDFModule\View\Renderer\PdfRenderer;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\ViewModel;
use Zend\View\Renderer\PhpRenderer;

class ManageBookingController extends AbstractActionController
{
    public function viewBookingAction()
    {
        $oView = new ViewModel();

        /* @var $renderer \Zend\View\Renderer\PhpRenderer */
		$renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
		$headScript = $this->getServiceLocator()->get('viewhelpermanager')->get('HeadScript');
		$headLink = $this->getServiceLocator()->get('viewhelpermanager')->get('headLink');
        //init script
		$headScript->appendFile('/min/search.min.js?'. filemtime('public/min/search.min.js'));

        $title = "Online Bus Ticket Booking Sri Lanka";
        $metaDesc = 'Among the platforms for online bus ticket booking Sri Lanka has to offer, Bus Booking provides maximum convenience, attending to all your bus seat reservation needs.';
        $metaKeywords = ' Online Bus Ticket Booking Sri Lanka, online bus booking Sri Lanka, online bus seat reservation';
        //customize titles based on loaded city
        if(!is_null($fromName) && !is_null($toName)){
            $title = "$fromName to $toName bus seat booking";
            $metaDesc = "Book bus tickets / seats from $fromName to $toName luxury and super luxury buses in Sri Lanka";
            $metaKeywords = "from $fromName to $toName, online bus booking Sri Lanka, online bus seat reservation";
        }


        //set meta and titile
        $renderer->headTitle($title);
        $renderer->headMeta($metaDesc, 'description');
        $renderer->headMeta($metaKeywords, 'keywords');

        return $oView;
    }
}
