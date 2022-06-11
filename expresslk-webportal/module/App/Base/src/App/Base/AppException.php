<?php


namespace App\Base;


class AppException extends \Exception{

    private $expCode = '';

    public function __construct($code, \Exception $previous = null)
    {
        //set code
        $this->expCode = $code;
        //set code as 0 and call parent exception
        parent::__construct('', 0, $previous);
    }

    /**
     * Get application error code
     * @return string
     */
    public function getAppCode()
    {
        return $this->expCode;
    }
}