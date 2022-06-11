<?php
/**
 * Zend Framework (http://framework.zend.com/)
 *
 * @link      http://github.com/zendframework/ZendSkeletonApplication for the canonical source repository
 * @copyright Copyright (c) 2005-2014 Zend Technologies USA Inc. (http://www.zend.com)
 * @license   http://framework.zend.com/license/new-bsd New BSD License
 */

return array(
    'controllers' => array(
        'invokables' => array(
            'Cron\Controller\SmsResend' => 'Cron\Controller\SmsResendController',
            'Cron\Controller\EmailResend' => 'Cron\Controller\EmailResendController',
            'Cron\Controller\AddSchedule' => 'Cron\Controller\AddScheduleController',
			
        ),
    ),
	'console' => array(
        'router' => array(
            'routes' => array(
                'smsresend' => array(
					'options' => array(
							'route'    => 'resendsms',
							'defaults' => array(
												'controller' => 'Cron\Controller\SmsResend',
												'action'     => 'resendSMS'
										)
							)
					),
				'emailresend' => array(
					'options' => array(
							'route'    => 'resendemail',
							'defaults' => array(
												'controller' => 'Cron\Controller\EmailResend',
												'action'     => 'resendEmail'
										)
							)
					),
				'processScheduleQueue' => array(
					'options' => array(
							'route'    => 'addschedule',
							'defaults' => array(
												'controller' => 'Cron\Controller\AddSchedule',
												'action'     => 'addSchedule'
										)
							)
					)
            ),
        ),
    ),

);
