<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 3/25/14
 * Time: 6:01 PM
 */
use Application\Acl\Acl;

return array(
    'acl' => array(
        'roles' => array(
            Acl::ROLE_GUEST     => null,
            Acl::ROLE_CUSTOMER  => Acl::ROLE_GUEST,
            Acl::ROLE_AGENT => null,
            Acl::ROLE_TICKETBOX => null,
            Acl::ROLE_ADMIN     => Acl::ROLE_TICKETBOX,
        ),
        'resources' => array(
            'allow' => array(
                /**
                 * Search module
                 */
                'Application\Controller\Index' => array(
                    'all'   => array(Acl::ROLE_GUEST, Acl::ROLE_AGENT)
                ),
                'Application\Controller\AgentRegistration' => array(
                    'all'   => array(Acl::ROLE_GUEST, Acl::ROLE_AGENT)
                ),
                'Application\Controller\Search' => array(
                    'all'   => array(Acl::ROLE_GUEST, Acl::ROLE_AGENT)
                ),
                'Application\Controller\Payment' => array(
                    'all'   => array(Acl::ROLE_GUEST, Acl::ROLE_AGENT)
                ),
                'Application\Controller\Auth' => array(
                    'all'   => array(Acl::ROLE_GUEST, Acl::ROLE_AGENT, Acl::ROLE_TICKETBOX)
                ),
                'Application\Controller\Test' => array(
                    'all'   => array(Acl::ROLE_GUEST)
                ),

                /**
                 * Admin module
                 */
                'Admin\Controller\Index' => array(
                    //'index'   => Acl::ROLE_TICKETBOX,
                    'all'   => Acl::ROLE_ADMIN,
                ),
                'Admin\Controller\Ticketbox' => array(
                    'all'   => Acl::ROLE_TICKETBOX
                ),
                'Admin\Controller\TicketboxAjax' => array(
                    'all'   => Acl::ROLE_TICKETBOX
                ),
                'Admin\Controller\Bus' => array(
                    'all'   => Acl::ROLE_ADMIN
                ),
                'Admin\Controller\User' => array(
                    'all'   => Acl::ROLE_ADMIN
                ),
                'Admin\Controller\BusSchedule' => array(
                    'all'   => Acl::ROLE_ADMIN
                ),
                'Admin\Controller\BusService' => array(
                    'all'   => Acl::ROLE_ADMIN
                ),
                'Admin\Controller\Crud' => array(
                    'all'   => Acl::ROLE_ADMIN
                ),
                'Admin\Controller\Report' => array(
                    'all'   => array(Acl::ROLE_TICKETBOX, Acl::ROLE_ADMIN)
                ),
            )
        )
    )
);