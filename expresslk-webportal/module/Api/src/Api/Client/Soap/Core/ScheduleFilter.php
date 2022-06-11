<?php

namespace Api\Client\Soap\Core;

include_once('Filter.php');

class ScheduleFilter extends Filter
{

    /**
     * @var int $scheduleId
     * @access public
     */
    public $scheduleId = null;

    /**
     * @param int $scheduleId
     * @access public
     */
    public function __construct($scheduleId)
    {
      $this->scheduleId = $scheduleId;
    }

}
