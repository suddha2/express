<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 10/22/14
 * Time: 8:39 PM
 */

namespace Admin\Controller;


use Admin\Form\User;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\JsonModel;
use Zend\View\Model\ViewModel;

class UserController extends AbstractActionController{

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

        $oForm = new User();

        $oView->setVariables(array(
            'form' => $oForm
        ));

        return $oView;
    }

    public function addjaxAction()
    {
        $response = array();
        $response['error'] = array();

        return new JsonModel($response);
    }

} 