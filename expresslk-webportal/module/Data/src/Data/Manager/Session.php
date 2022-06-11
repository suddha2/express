<?php


namespace Data\Manager;


use Data\InjectorBase;
use Data\Storage\SessionStorage;

/**
 * Class Session
 * @package Data\Manager
 */
class Session extends InjectorBase
{
    /**
     * Plug the session handlers application wide
     * @return SessionStorage
     */
    public function hookDbSession()
    {
        $conf = $this->getServiceManager()->get('Config');
        $config = null;
        $serviceConfig = null;

        if (isset($conf['db-session']) && isset($conf['db-session']['sessionConfig'])) {
            $config = $conf['db-session']['sessionConfig'];
        }

        $oDb = $this->getServiceManager()->get('Zend\Db\Adapter\Adapter');

        return new SessionStorage($oDb, $config);
    }
}