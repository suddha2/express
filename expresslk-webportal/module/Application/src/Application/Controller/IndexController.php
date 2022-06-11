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

class IndexController extends AbstractActionController
{
    public function indexAction()
    {
        $oView = new ViewModel();

        /* @var $renderer \Zend\View\Renderer\PhpRenderer */
		$renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
		$headScript = $this->getServiceLocator()->get('viewhelpermanager')->get('HeadScript');
		$headLink = $this->getServiceLocator()->get('viewhelpermanager')->get('headLink');
        //init script
		$headScript->appendFile('/min/search.min.js?'. filemtime('public/min/search.min.js'));

        $endPoint = $this->params()->fromRoute("endpoint");
        $fromName = $this->params()->fromRoute("fromName");
        $toName = $this->params()->fromRoute("toName");

		//get city list from service
//		$oCities = new City($this->getServiceLocator());
//        $oCities->setShouldTranslate(false);
//		$aCityList = $oCities->getTerminusCityList();
		//create html view
		$oView->setVariable(
			'allCities',
            array()
		);
        //set as home layout
        $this->layout()->isHome = true;

        //set current screen is home page or listing page
        $oView->setVariable('isHomeSearch', is_null($endPoint));

        $title = "Online Bus Ticket Booking Sri Lanka";
        $metaDesc = 'Among the platforms for online bus ticket booking Sri Lanka has to offer, Bus Booking provides maximum convenience, attending to all your bus seat reservation needs.';
        $metaKeywords = ' Online Bus Ticket Booking Sri Lanka, online bus booking Sri Lanka, online bus seat reservation';
        //customize titles based on loaded city
        if(!is_null($fromName) && !is_null($toName)){
            $title = "$fromName to $toName bus seat booking";
            $metaDesc = "Book bus tickets / seats from $fromName to $toName luxury and super luxury buses in Sri Lanka";
            $metaKeywords = "from $fromName to $toName, online bus booking Sri Lanka, online bus seat reservation";
        }

        $oView->setVariable('fromCityName', $fromName);
        $oView->setVariable('toCityName', $toName);

        //set meta and titile
        $renderer->headTitle($title);
        $renderer->headMeta($metaDesc, 'description');
        $renderer->headMeta($metaKeywords, 'keywords');

        return $oView;
    }

    public function loadpartialAction()
    {
        $partial = $this->params()->fromQuery('p');
        //sanitize
        $partial = preg_replace("/[^a-zA-Z0-9_-]+/", "", $partial);

        $oView = new ViewModel();
        $oView->setTemplate('application/index/partial/'. $partial);
        $oView->setTerminal(true);

        return $oView;
    }

	public function langAction()
	{
		$lang = $this->params()->fromQuery('lang');
		$destination = urldecode($this->params()->fromQuery('dest', '/'));

		$curLocale = Language::EN_LOCALE;

		switch ($lang){
			case 'si':
				$curLocale = Language::SI_LOCALE;
				break;
			case 'ta':
				$curLocale = Language::TA_LOCALE;
				break;
		}

		Language::setCurrentLocale($curLocale);

		return $this->redirect()->toUrl($destination)
            ->setStatusCode(301);
	}

	public function offercodeAction()
    {
        $offerCode = $this->params()->fromRoute('offerCode');

        //set offer code in session
        if(!empty($offerCode)){
            /** @var Discount $oDiscount */
            $oDiscount = $this->getServiceLocator()->get('Application\Discount');
            $oDiscount->setCurrentDiscountCode($offerCode);
        }

        //301 redirect to home page
        return $this->redirect()->toUrl($this->_getDomain())
            ->setStatusCode(301);
    }

	public function faqAction()
    {
    	$renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
		$renderer->headTitle('Bus fares sri lanka | Sri Lanka bus | bus routes');
        $renderer->headMeta('Curious about bus fares Sri Lanka? Or maybe it is bus routes you are interested in. Whatever it is, our FAQ section may be able to assist you in all your inquiries. ', 'description');
        $renderer->headMeta('Bus routes, bus fares sri lanka, sri lank bus, public transport, bus schedule, bus seat reservations, travel, reserve seats, seat booking, luxury travel, transport fare', 'keywords');

		$oView = new ViewModel();

		return $oView;
    }

	public function aboutusAction()
	{
		$renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
		$renderer->headTitle('About Us | Busbooking.lk');
        $renderer->headMeta('Bus Booking has a fully automated multi-platform booking engine complete with bus schedule. Sri Lanka relies heavily on public transport and our services enhance passenger experience.', 'description');
        $renderer->headMeta(' online bus seat reservation, online bus booking Sri Lanka, online bus ticket booking', 'keywords');

		$oView = new ViewModel();

		return $oView;
	}

	public function termsAction()
	{
		$renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
		$renderer->headTitle('Terms & Conditions - One stop online market for bus seat booking & ticket reservations for long distances buses & super luxury coaches, within Sri Lanka.');

		$oView = new ViewModel();

		return $oView;
	}

	public function privacyAction()
	{
		$renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
		$renderer->headTitle('Privacy Policy - Most popular online web portal for online bus seat booking and super luxury coach tickets reservations within Sri Lanka.');

		$oView = new ViewModel();

		return $oView;
	}

	public function contactAction()
	{
		$renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
        $headScript = $this->getServiceLocator()->get('viewhelpermanager')->get('HeadScript');

        $headScript->appendFile('https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false');

        $renderer->headTitle('Bus ticket reservation | contact us at busbooking');
        $renderer->headMeta('For all questions regarding bus ticket reservation, bus routes, schedules and other additional information, please feel free to contact us at busbooking via phone or email. ', 'description');
        $renderer->headMeta('Contact us at busbooking, bus ticket reservation, bus routes, bus fares sri lanka, sri lank bus, public transport, bus schedule, bus seat reservations, travel, reserve seats, seat booking', 'keywords');

		$oView = new ViewModel();
        //disable container
        $this->layout()->noContainer = true;
		return $oView;
	}

	public function experienceAction()
	{
		$renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
		$renderer->headTitle('Bus Reservation and Public Transportation in Sri Lanka');
        $renderer->headMeta('Taking the concept of bus reservation and public transportation in Sri Lanka to the next level, Bus Booking enhances the passenger experience, ensuring a comfortable journey.', 'description');
        $renderer->headMeta('public transportation in sri lanka, bus reservation', 'keywords');

		$oView = new ViewModel();

		return $oView;
	}

    public function busroutesAction()
    {
        $renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
        $renderer->headTitle('Bus Routes and cities in Sri Lanka');
        $renderer->headMeta('Bus reservation cities in Sri Lanka.', 'description');
        $renderer->headMeta('public transportation in sri lanka, bus reservation', 'keywords');

        $oView = new ViewModel();


        $oView->cityList = $this->getCityDestinationList();

        return $oView;
    }

    public function sitemapAction()
    {
        $sitemapType = $this->params()->fromRoute('type');

        $response = $this->getResponse();
        $response->getHeaders()->addHeaders(array('Content-type' => 'text/xml'));

        $oView = new ViewModel();
        $oView->setTerminal(true);

        $oView->type = $sitemapType;

        $citySitemaps = array(
            'sitemap_cities' => array(
                'start' => 0,
            ),
            'sitemap_cities_1' => array(
                'start' => 40,
            ),
            'sitemap_cities_2' => array(
                'start' => 80,
            ),
        );

        //if type is city list
        if(isset($citySitemaps[$sitemapType])){
            $oView->citylist = $this->getCityDestinationList($citySitemaps[$sitemapType]['start']);
        }

        $oView->citySiteMpas = array_keys($citySitemaps);

        return $oView;
    }

    private function getCityDestinationList($start = 0, $rows = 40)
    {
        ini_set('memory_limit', '256M');
        $cityResult = array();
        $oCache = $this->getServiceLocator()->get('DataStore');
        $key = 'citydestinationlist_'. Language::getCurrentLocale() . '-' . strval($start) . strval($rows);
        $key = sha1($key);

        $success = false;
        $cityResult = $oCache->getItem($key, $success);
        if(!$success){
            //generate result again
            try {
                $oCities = new City($this->getServiceLocator());
                $queryCriteria = new QueryCriteria();
                $queryCriteria->setParams(array(
                    'pageStart' => strval($start),
                    'pageRows' => strval($rows),
                    'active' => 'true',
                ));
                $aCityList = $oCities->getCityList($queryCriteria);
                //go through each city and get to city
                foreach ($aCityList as $fromCity) {
                    //get to city
                    $toCity = $oCities->getDestinationsOfCity($fromCity->id);
                    foreach ($toCity as $toC) {
                        //build array
                        $cityResult[] = array(
                            'from' => $fromCity->name,
                            'to' => $toC->name,
                            'link' => '/bus/search/' . $fromCity->name . '/' . $toC->name
                                . '/' . $fromCity->id . '/' . $toC->id . '/',
                        );
                    }
                }
                //cache result only if the response is proper
                if(is_array($cityResult) && count($cityResult)>0){
                    $oCache->setItem($key, $cityResult);
                }
            } catch (\Exception $e) {

            }
        }

        return $cityResult;
    }

    /**
     * @return string
     */
    private function _getDomain()
    {
        $uri = $this->getRequest()->getUri();
        $base = sprintf('%s://%s', $uri->getScheme(), $uri->getHost());
        return $base;
    }
}
