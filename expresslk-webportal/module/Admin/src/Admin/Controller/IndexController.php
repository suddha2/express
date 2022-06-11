<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 7/27/14
 * Time: 4:17 AM
 */

namespace Admin\Controller;

use Application\Util\Language;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\ViewModel;

class IndexController extends AbstractActionController
{	
	public function indexAction()
    {
        $renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
        $headScript = $this->getServiceLocator()->get('viewhelpermanager')->get('HeadScript');
        $headLink = $this->getServiceLocator()->get('viewhelpermanager')->get('headLink');

        $renderer->headTitle('Your one stop Sri Lankan online travel booking engine!');

        $headLink->appendStylesheet('/min/ngadmin.min.css?'. filemtime('public/min/ngadmin.min.css'));

        $headScript->appendFile('/min/ngadmin.min.js?'. filemtime('public/min/ngadmin.min.js'));
        $headScript->appendFile('/min/crud.min.js?'. filemtime('public/min/crud.min.js'));
        $headScript->appendFile('/min/config.min.js?'. filemtime('public/min/config.min.js'));
        $headScript->appendFile('/min/ticketbox.min.js?'. filemtime('public/min/ticketbox.min.js'));
        $headScript->appendFile('/min/report.min.js?'. filemtime('public/min/report.min.js'));
        $headScript->appendFile('/min/bus-location.min.js?'. filemtime('public/min/bus-location.min.js'));

        //create html view
        $oView = new ViewModel();
        //set ng app
        $this->layout()->ngApp = 'expressAdminCrud';
        $this->layout()->setTemplate('layout/crud');

        $type = $this->params()->fromQuery('type');
        $oView->minimal = $type=='min'? true : false;

        return $oView;
    }

    public function configsAction()
    {
        $renderer = $this->getServiceLocator()->get('Zend\View\Renderer\PhpRenderer');
        $headScript = $this->getServiceLocator()->get('viewhelpermanager')->get('HeadScript');

        $renderer->headTitle('Your one stop Sri Lankan online travel booking engine!');

        //init script
        $headScript->appendFile('/min/config.min.js');

        //create html view
        $oView = new ViewModel();

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

    public function adminAction()
    {

    }

} 