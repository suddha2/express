<?php

namespace Legacy\Adapter;

use Zend\Paginator\Adapter\AdapterInterface;
use Legacy\Controller\CrudController;
use Zend\ServiceManager\ServiceManager;

class RestSelect implements AdapterInterface
{
    private $sm;
    private $crud;
    private $queryParams;

    public function __construct(ServiceManager $sm, CrudController $crud, $queryParams)
    {
        $this->sm = $sm;
        $this->crud = $crud;
        $this->queryParams = $queryParams;
    }

    /**
     * Returns a collection of items for a page.
     *
     * @param  int $offset Page offset
     * @param  int $itemCountPerPage Number of items per page
     * @return array
     */
    public function getItems($offset, $itemCountPerPage)
    {
        $this->queryParams['pageStart'] = $offset;
        $this->queryParams['pageRows'] = $itemCountPerPage;

        if (!isset($this->queryParams['sortField'])) {
            $this->queryParams['sortField'] = 'id';
            $this->queryParams['sortDir'] = 'DESC';
        } elseif (!isset($this->queryParams['sortDir'])) {
            $this->queryParams['sortDir'] = 'ASC';
        }
        return $this->crud->getEntityList($this->sm, $this->queryParams);
    }

    /**
     * Returns the total number of rows in the result set.
     *
     * @return int
     */
    public function count()
    {
        $count = $this->crud->count();
        if ($count < 0) {
            $this->getItems(0, 30);
            $count = $this->crud->count();
        }
        return $count;
    }
}