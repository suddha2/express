<?php

namespace Api\Client\Mobitel\CGW;

class chargeFromMSISDN
{

    /**
     * @var chgRequest $chgRequest
     * @access public
     */
    public $chgRequest = null;

    /**
     * @var string $userName
     * @access public
     */
    public $userName = null;

    /**
     * @var string $passWord
     * @access public
     */
    public $passWord = null;

    /**
     * @param chgRequest $chgRequest
     * @param string $userName
     * @param string $passWord
     * @access public
     */
    public function __construct($chgRequest, $userName, $passWord)
    {
      $this->chgRequest = $chgRequest;
      $this->userName = $userName;
      $this->passWord = $passWord;
    }

}
