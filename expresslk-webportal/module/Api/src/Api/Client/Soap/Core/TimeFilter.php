<?php

namespace Api\Client\Soap\Core;

include_once('Filter.php');

class TimeFilter extends Filter
{

    /**
     * @var dateTime $leaveAfter
     * @access public
     */
    public $leaveAfter = null;

    /**
     * @var dateTime $leaveBefore
     * @access public
     */
    public $leaveBefore = null;

    /**
     * @var dateTime $reachAfter
     * @access public
     */
    public $reachAfter = null;

    /**
     * @var dateTime $reachBefore
     * @access public
     */
    public $reachBefore = null;

    /**
     * @param dateTime $leaveAfter
     * @param dateTime $leaveBefore
     * @param dateTime $reachAfter
     * @param dateTime $reachBefore
     * @access public
     */
    public function __construct($leaveAfter, $leaveBefore, $reachAfter, $reachBefore)
    {
      $this->leaveAfter = $leaveAfter;
      $this->leaveBefore = $leaveBefore;
      $this->reachAfter = $reachAfter;
      $this->reachBefore = $reachBefore;
    }

}
