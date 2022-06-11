<?php

namespace Legacy\Controller;

use Zend\View\Model\ViewModel;
use Zend\Mvc\Controller\AbstractActionController;
use Legacy\Adapter\RestSelect;
use Zend\Paginator\Paginator;

abstract class EntityController extends AbstractActionController
{
    protected $crud;
    protected $controller;

    public function __construct($entity)
    {
        $this->crud = new CrudController($entity);
    }

    public function indexAction()
    {
        $queryParams = $this->params()->fromQuery();
        unset($queryParams['page']);
        foreach($queryParams as $param => $value) {
            if (!$value) {
                unset($queryParams[$param]);
            }
        }
        $paginator = new Paginator(new RestSelect($this->getServiceLocator(), $this->crud, $queryParams));
        $paginator->setCurrentPageNumber((int) $this->params()->fromQuery('page', 1));
        $paginator->setItemCountPerPage(30);

        return new ViewModel(array(
            'paginator' => $paginator,
            'params' => $queryParams,
            'filters' => $this->getFilters(),
            'headers' => $this->getHeaders(),
            'controller' => $this->controller,
        ));
    }

    public function viewAction()
    {
        $id = $this->params()->fromRoute('id');
        if ($id === null) {
            $this->redirect()->toRoute();
        }
        $model = $this->crud->getEntity($this->getServiceLocator(), $id);

        return new ViewModel(array(
            'model' => $model,
            'controller' => $this->controller,
        ));
    }

    public function editAction()
    {
        $id = $this->params()->fromRoute('id');
        if ($id !== null) {
            $model = $this->crud->getEntity($this->getServiceLocator(), $id);
            $error = null;

            if (!empty($this->params()->fromPost('update'))) {
                $this->loadFromPost($model);
                $response = $this->crud->updateEntity($this->getServiceLocator(), $id, $model);
                if ($response->code == 200) {
                    $this->redirect()->toRoute('legacy/default', array('controller'=> $this->controller, 'action'=> 'view', 'id' => $model->id));
                } else {
                    if (is_string($response->body)) {
                        $error = $response->body;
                    }
                }
            }

            $data = $this->getDataForEditOrCreate();
            return new ViewModel(
                array_merge($data, array(
                    'model' => $model,
                    'error' => $error,
                    'controller' => $this->controller,
                )
            ));
        } else {
            $this->redirect()->toRoute();
        }
    }

    protected function getDataForEditOrCreate()
    {
        return array();
    }

    protected abstract function loadFromPost(&$model);

    protected abstract function getFilters();

    protected abstract function getHeaders();
}
