<?php

namespace Web\Surrexi\Controller;

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
        //get city list from service
        $oCities = new City($this->getServiceLocator());
        $aCityList = $oCities->getTerminusCityList();
        //create html view
        $oView->setVariable(
            'allCities',
            $aCityList
        );
        //set as home layout
        $this->layout()->isHome = true;

        return $oView;
    }


}

