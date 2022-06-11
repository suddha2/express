<?php


namespace Application;


class Domain {

    const NAME_BUSBOOKING = 'www.busbooking.lk';
    const NAME_SUPERLINE = 'superline.express.lk';
    const NAME_SURREXI = 'surrexi.express.lk';
    const NAME_SLTB = 'sltb.express.lk';
    const NAME_EBERTSILVA = 'ebertsilva.express.lk';
    const NAME_NTC = 'ntc.express.lk';
    const NAME_MOBITEL = '52.220.116.44';
	

    private static $_currentSiteName;

    public static function setDomain($siteName)
    {
        //validate site name with constants
        $oClass = new \ReflectionClass(__CLASS__);
        $aConstants = $oClass->getConstants();
        //check if site name exits, otherwise throw an error
        if (!in_array($siteName, $aConstants)) {
            throw new \Exception('Invalid site name.');
        }

        self::$_currentSiteName = $siteName;
    }

    public static function getDomain()
    {
        return self::$_currentSiteName;
    }
}