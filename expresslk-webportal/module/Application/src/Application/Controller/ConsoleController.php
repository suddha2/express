<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 12/31/14
 * Time: 12:56 PM
 */

namespace Application\Controller;

use Api\Manager\Schedule\OperationalSchedule;
use Api\Operation\Request\QueryCriteria;
use Application\Helper\ExprDateTime;
use Zend\Debug\Debug;
use Zend\Mvc\Controller\AbstractActionController;
use Api\Client\Rest\Factory as RestFactory;

class ConsoleController extends AbstractActionController{

    public function testAction()
    {

    }

    public function conductorscheduleAction()
    {
        $result = array();
        try {

            $oSchedule = new RestFactory($this->getServiceLocator(), 'operationSchedule');
            $oQuery = new QueryCriteria();
            //get operation schedules that are; bus departs in an hour, stage is OFB
            $oDepartTime = (new ExprDateTime())->add(new \DateInterval('PT1H'));
            $oDepartEnd = clone $oDepartTime;
            $oDepartEnd->sub(new \DateInterval('PT24H'));

            $oQuery->setParams(array(
                'departureStart' => $oDepartTime,
                'departureEnd' => $oDepartEnd,
                'stage.code' => OperationalSchedule::STAGE_CODE_OFB,
            ));
            Debug::dump([
                $oDepartTime->format('Y-m-d H:i:s'),
                $oDepartEnd->format('Y-m-d H:i:s'),
            ]);

            //$schedules = $oSchedule->getList($oQuery);
        } catch (\Exception $e) {
            echo $e->getMessage();
            $result['error'] = $e->getMessage();
        }
    }
}