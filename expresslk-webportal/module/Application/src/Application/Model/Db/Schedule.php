<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 12/31/14
 * Time: 2:01 PM
 */

namespace Application\Model\Db;

use Application\Sms\Template;
use Zend\ServiceManager\ServiceManager;

class Schedule {

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

    public function getConductorSchedule()
    {
        
    }
}
/*
 * process runs every hour (9 am)
 * selects schedules after two hours (11 am to 12 am)
 */