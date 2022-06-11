<?php


namespace Api\Manager;


use Application\Domain;

class Company extends Base{

    const CODE_BBK = 'BBK'; //code for busbooking
    const CODE_NCG = 'NCG'; //code for ncg
    const CODE_NTC = 'NTC'; //code for ntc
    const CODE_SPL = 'SPL'; //code for superline

    /**
     * get relavant domain from code
     * @param $code
     * @return null|string
     */
    public static function getDomainFromCode($code)
    {
        $domain = null;
        switch ($code){
            case self::CODE_BBK:
                $domain = Domain::NAME_BUSBOOKING;
                break;
            case self::CODE_SPL:
                $domain = Domain::NAME_SUPERLINE;
                break;
            default:
                $domain = Domain::NAME_BUSBOOKING;
                break;
        }

        return $domain;
    }
}