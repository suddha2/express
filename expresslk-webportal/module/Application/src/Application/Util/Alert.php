<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 1/14/15
 * Time: 10:15 AM
 */

namespace Application\Util;


use Zend\ServiceManager\ServiceManager;

class Alert {

    private $_sm, $_config;

    public static $skipNICs = array(
        '892480826V', //ruwan
        '851730591V', //udantha
        '871451320V', //madhura
        '861271625V', //dinusha
    );

    public function __construct(ServiceManager $serviceManager)
    {
        $this->_sm = $serviceManager;
        $this->_config = $this->_sm->get('Config');
    }

    /**
     * @param $body
     * @param string $subject
     */
    public function sendEmailToAdmin($body, $subject = 'Notification from the server')
    {
        $adminEmails = array('strategy@express.lk');

        //append sitename
        $subject = $subject . ' : ' . $this->_config['system']['serverName'];

        try {
            /* @var $email \Application\Model\Email */
            $email = $this->_sm->get('Email');
            $email->addTo($adminEmails)
                ->setSubject($subject)
                ->setBodyHtml($body);
            //send email
            $email->send();
        } catch (\Exception $e) {

        }
    }
}