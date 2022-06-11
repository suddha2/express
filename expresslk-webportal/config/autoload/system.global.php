<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 4/3/14
 * Time: 12:48 PM
 */
return array(
    //configs for domains
    'domain' => array(
        'www.busbooking.lk' => array(
            'site'         => \Application\Domain::NAME_BUSBOOKING,
            'siteConfigName'   => 'busbooking.lk.php',
        ),
        'www.superline.lk' => array(
            'site'         => \Application\Domain::NAME_SUPERLINE,
            'siteConfigName'   => 'superline.lk.php',
        ),
    ),
    'system' => array(
        'serverName' => 'express.lk',
        'siteName' => 'express.lk', //name used in branding
        'hotlinePhone' => '0115-116-117', //phone number used in branding
        'debug' => false,
        'webBookingExpireTime' => (60*60*12),
        'TBBookingExpireTime' => (60*60*2),
        'ticketingActiveTime' => (60*60*1), //seconds before ticketing app activates
    ),
    'wsdl'  => array(
        'search' => 'http://167.114.96.6:7575/ExpressLK-search/search?wsdl',
        'reports' => 'http://167.114.96.6:7575/ExpressLK-reports/reports?wsdl'
    ),
    'rest'  => array(
        'v1'    => array(
            'admin' => 'http://167.114.96.6:7575/ExpressLK-admin/v1/internal/admin'
        )
    ),
    'api' => array(
        'auth' => array(
            'username' => 'bbk',
            'password' => 'vS0bgsikVKSLnQ0JtcuHqie791wOVImF7SFAGpMGgn8T8vKWcZSjhGGzCtrM',
        ),
    ),
    'sms'   => array(
        'src'       => 'BusBooking',
        'endPoints' => array(
            //primary sms endpoint
            'primary' => array(
                'endPoint'  => 'http://sms.textware.lk:5000/sms/send_sms.php',  //www.textware.lk
                'userID'    => 'express418',
                'password'  => 'Expr48LK',
            ),
            //secondary sms endpoint
            'secondary' => array(
                'endPoint'  => 'http://123.231.38.75:5000/sms/send_sms.php',
                'userID'    => 'express418',
                'password'  => 'H6738yJHBagL0987JYTQ', //'Express123',
            ),
            //ternary sms endpoint
            'ternary' => array(
                'endPoint'  => 'http://www.textware.lk:5000/sms/send_sms.php',  //www.textware.lk
                'userID'    => 'express418',
                'password'  => 'H6738yJHBagL0987JYTQ',
            ),
        )
    ),
    'client-api' => array(
        'mobitel' => array(
            'cgw' => array(
                'wsdl'  => '',
                'username'  => '',
                'password'  => '',
                'serviceId'  => '',
            )
        )
    ),
    'db-session' => array(
        'sessionConfig' => array(
//            'cache_expire' => 86400,
//            'cookie_lifetime' => 1800,
//            'cookie_path' => '/',
//            'cookie_secure' => false,
//            'remember_me_seconds' => 3600,
            'use_cookies' => true,
            'gc_maxlifetime' => 1800,
        )
    )
);