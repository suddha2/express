<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 4/3/14
 * Time: 12:48 PM
 */
return array(
    'mail' => array(
        'transport' => array(
            'options' => array(
                'host'              => 'email-smtp.eu-west-1.amazonaws.com',
                'connection_class'  => 'plain',
                'port' => 587,
                'connection_config' => array(
                    'username' => 'AKIAJOVLA7CSNBJIRQTQ',
                    'password' => 'AuECW+3cGiHmZjk3i9ZquZSA6EMuAcclB/Xkr9Jc9ukO',
                    'ssl' => 'tls'
                ),
            ),
        ),
        'defaults' => array(
            'from' => array(
                'email' => 'info@busbooking.lk',
                'name'  => 'BusBooking.lk'
            ),
            'replyto' => array(
                'email' => 'info@busbooking.lk',
                'name'  => 'BusBooking.lk'
            ),
        ),
    ),
);