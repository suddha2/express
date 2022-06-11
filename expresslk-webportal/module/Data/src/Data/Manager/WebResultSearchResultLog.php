<?php


namespace Data\Manager;


use Data\InjectorBase;
use Data\Storage\WebResultSearchResultLogStorage;

class WebResultSearchResultLog extends InjectorBase
{
    private $searchResultLogStorage = null;

    
    public function add ($searchDate,$searchFromCity,$searchToCity,$searchFromCityId,$searchToCityId,$searchResultCount,$domain)
    {
		return $this->getSearchResultLogStorage()->add ($searchDate,$searchFromCity,$searchToCity,$searchFromCityId,$searchToCityId,$searchResultCount,$domain);
    }

    

    private function getSearchResultLogStorage()
    {
        if(is_null($this->searchResultLogStorage)){
            $this->searchResultLogStorage = new WebResultSearchResultLogStorage($this->getServiceManager()->get('Zend\Db\Adapter\Adapter'));
        }
        return $this->searchResultLogStorage;
    }
	
	
}