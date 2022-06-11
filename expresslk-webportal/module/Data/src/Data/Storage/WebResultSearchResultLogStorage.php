<?php


namespace Data\Storage;

use Zend\Db\Adapter\Adapter;

class WebResultSearchResultLogStorage
{
    const TABLE_NAME = 'web_search_result_log';
	
    protected $adapter;
    protected $tblGW;

    public function __construct(Adapter $adapter)
    {
        $this->adapter = $adapter;
        $this->tblGW = new \Zend\Db\TableGateway\TableGateway(self::TABLE_NAME, $this->adapter);
    }

	public function add($searchDate,$searchFromCity,$searchToCity,$searchFromCityId,$searchToCityId,$searchResultCount,$domain)
    {
        
		// $myfile = fopen("C:\wamp64\logs\WebResultSearchResultLogStorage.txt", "w") or die("Unable to open file!");
		// fwrite($myfile,"\n ==================================".date('Y-m-d H:i:s'));
		// fwrite($myfile,"\n DATA  \n");
		$data= ['search_date' => $searchDate,
            'search_from_city' => $searchFromCity,
            'search_from_city_id' => $searchFromCityId,
            'search_to_city_id' => $searchToCityId,
            'search_to_city' => $searchToCity,
            'search_result_count' => $searchResultCount,
			'created_date'=>date('Y-m-d H:i:s'),
			'domain'=> $domain
			];
		
		// fwrite($myfile,"\n  ".print_r($data,true));
		
		return $this->tblGW->insert(
           $data 
        );
    }

    
	
}