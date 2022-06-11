<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 10/22/14
 * Time: 8:24 PM
 */

namespace Admin\Controller;


use Api\Client\Rest\Factory as RestFactory;
use Api\Operation\Request\QueryCriteria;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\JsonModel;
use Zend\View\Model\ViewModel;

class BusScheduleController extends AbstractActionController{

    public function viewAction()
    {
        $oView = new ViewModel();
        $oView->setTerminal(true);

        return $oView;
    }

    public function viewajaxAction()
    {
        $response = array();
        $response['success'] = array();
        $response['total'] = 0;

        return new JsonModel($response);
    }

    public function addAction()
    {
        $oView = new ViewModel();
        $oView->setTerminal(true);

        /**
         * @var $oBus \Api\Manager\Bus get bus object
         */
        //$oBus = $this->getServiceLocator()->get('Api\Manager\Bus');
        //$criteria = new QueryCriteria();
		//$criteria->setLoadAll() ->setSortField('name');
		//$oView->buses = $oBus->getBusList($criteria);

        return $oView;
    }
	public function getBusByPlateAction()
    {
		$plateNumber = $this->params()->fromQuery('plateNumber');
		$oBus = $this->getServiceLocator()->get('Api\Manager\BusLight');
		
		//$criteria->setSortField('plateNumber');
		$myArrayofData = $oBus->getBusListByPlateNumber($plateNumber);
		return new JsonModel($myArrayofData);
    }
	
	
    public function addjaxAction()
    {
        $response = array();
        $response['error'] = array();

        return new JsonModel($response);
    }
}