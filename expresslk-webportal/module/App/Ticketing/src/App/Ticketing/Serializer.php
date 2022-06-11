<?php


namespace App\Ticketing;


use App\Base\Injector;
use Application\Acl\Acl;
use Application\Util;

class Serializer extends Injector
{
    const CACHE_KEY = 'APP_SERIALIZER';

    /**
     * Generate hashing
     * @param $oObject
     * @return string
     */
    public function getHash($oObject)
    {
        return (md5(json_encode( Util::ksortDeep($oObject) )));
    }

    /**
     * @param $key
     * @return array
     */
    public function getSavedObject($key)
    {
        $oUser = Acl::getAuthUser($this->getServiceManager());
        //set up cache
        $oCache = $this->getServiceManager()->get('cache');
        $skey = sha1(self::CACHE_KEY . $oUser->username . strval($key));

        $success = false;
        //get from cache
        $result = $oCache->getItem($skey, $success);
        if(!$success){
            $result = array();
        }

        return $result;
    }

    /**
     * @param $key
     * @param $object
     */
    public function saveObject($key, $object)
    {
        $oUser = Acl::getAuthUser($this->getServiceManager());
        //set up cache
        $oCache = $this->getServiceManager()->get('cache');
        $skey = sha1(self::CACHE_KEY . $oUser->username . strval($key));
        //save
        $oCache->setItem($skey, $object);
    }

    /**
     * @param $key
     */
    public function clearSaved($key)
    {
        $oUser = Acl::getAuthUser($this->getServiceManager());
        //set up cache
        $oCache = $this->getServiceManager()->get('cache');
        $skey = sha1(self::CACHE_KEY . $oUser->username . strval($key));

        $oCache->removeItem($skey);
    }
}