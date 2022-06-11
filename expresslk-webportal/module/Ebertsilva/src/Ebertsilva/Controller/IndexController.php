<?php

namespace Ebertsilva\Controller;

use Api\Client\Rest\Model\City;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\ViewModel;

class IndexController extends AbstractActionController
{

    public function indexAction()
    {
        /* @var $renderer \Zend\View\Renderer\PhpRenderer */
        $renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
        $headScript = $this->getServiceLocator()->get('viewhelpermanager')->get('HeadScript');
        $headLink = $this->getServiceLocator()->get('viewhelpermanager')->get('headLink');
        //init script
        $headScript->appendFile('/min/search.min.js?'. filemtime('public/min/search.min.js'));


        $oView = new ViewModel();
        //create html view
        $oView->setVariable(
            'allCities',
            array()
        );
        //set as home layout
        $this->layout()->isHome = true;

        return $oView;
    }
	public function termsAction()
	{
		$renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
		$renderer->headTitle('Terms & Conditions - One stop online market for bus seat booking & ticket reservations for long distances buses & super luxury coaches, within Sri Lanka.');

		$oView = new ViewModel();

		return $oView;
	}

}

