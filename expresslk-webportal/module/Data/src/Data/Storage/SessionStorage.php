<?php


namespace Data\Storage;

use Zend\Session\SaveHandler\DbTableGateway;
use Zend\Session\SaveHandler\DbTableGatewayOptions;
use Zend\Db\Adapter\Adapter;
use Zend\Session\SessionManager;
use Zend\Session\Container;

/**
 * Class SessionStorage
 * @package Data\Storage
 */
class SessionStorage
{
    const TABLE_NAME = 'session';

    protected $adapter;
    protected $tblGW;
    protected $sessionConfig;

    public function __construct(Adapter $adapter, $session_config)
    {
        $this->adapter = $adapter;
        $this->sessionConfig = $session_config;
        $this->tblGW = new \Zend\Db\TableGateway\TableGateway(self::TABLE_NAME, $this->adapter);
    }

    /**
     * Set session storage to Database
     */
    public function setSessionStorage()
    {
        $gwOpts = new DbTableGatewayOptions();
        $gwOpts->setDataColumn('data');
        $gwOpts->setIdColumn('id');
        $gwOpts->setLifetimeColumn('lifetime');
        $gwOpts->setModifiedColumn('modified');
        $gwOpts->setNameColumn('name');

        $saveHandler = new DbTableGateway($this->tblGW, $gwOpts);
        $sessionManager = new SessionManager();
        if ($this->sessionConfig) {
            $sessionConfig = new \Zend\Session\Config\SessionConfig();
            $sessionConfig->setOptions($this->sessionConfig);
            $sessionManager->setConfig($sessionConfig);
        }
        $sessionManager->setSaveHandler($saveHandler);
        Container::setDefaultManager($sessionManager);
        $sessionManager->start();
    }
}