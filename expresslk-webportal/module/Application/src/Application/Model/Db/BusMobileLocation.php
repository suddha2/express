<?php


namespace Application\Model\Db;


use Zend\ServiceManager\ServiceManager;

class BusMobileLocation
{
    private $sm;
    /**
     * @var \Zend\Db\Adapter\Adapter
     */
    private $db;

    public function __construct(ServiceManager $sm)
    {
        $this->sm = $sm;
        $this->db = $this->sm->get('Zend\Db\Adapter\Adapter');
    }

    /**
     * Add location data
     * @param $locationData
     * @return int
     */
    public function addLocation($locationData)
    {
        $statement = $this->db->createStatement("INSERT INTO bus_mobile_location 
                  (bus_id, lattitude, longitude, speed, bearing) VALUES (:bus, :lat, :lon, :speed, :bearing)");

        $schResult = $statement->execute(array(
            'bus' => $locationData['bus'],
            'lat' => $locationData['lat'],
            'lon' => $locationData['lon'],
            'speed' => $locationData['speed'],
            'bearing' => $locationData['bearing'],
        ));
        return $schResult->getAffectedRows();
    }

    /**
     * Get Bus Ids
     * @return \Zend\Db\Adapter\Driver\ResultInterface
     */
    public function getBusList()
    {
        $statement = $this->db->query("SELECT DISTINCT bus_id FROM bus_mobile_location");
        return $statement->execute();
    }

    /**
     * @param $busId
     * @param $startDate
     * @param $endDate
     * @return \Zend\Db\Adapter\Driver\ResultInterface
     */
    public function getBusLastLocations($busId, $startDate, $endDate)
    {
        //if no end date, load current date
        if($endDate==false){
            $endDate = date('Y-m-d H:i:s');
        }

        $statement = $this->db->createStatement("SELECT * FROM bus_mobile_location WHERE 
                        bus_id = :bus AND (created_time BETWEEN :startDate AND :endDate)
                        ORDER BY created_time DESC ");

        $schResult = $statement->execute(array(
            'bus' => $busId,
            'startDate' => $startDate,
            'endDate' => $endDate,
        ));
        return $schResult;
    }
}