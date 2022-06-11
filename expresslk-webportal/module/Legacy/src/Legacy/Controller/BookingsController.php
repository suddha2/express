<?php

namespace Legacy\Controller;

use Zend\View\Model\ViewModel;

class BookingsController extends EntityController
{
    public function __construct()
    {
        parent::__construct('bookingLight');
        $this->controller = 'bookings';
    }

    protected function getFilters()
    {
        return array(
            array('name' => 'reference~', 'label' => 'Reference', 'placeholder' => 'Filter by reference'),
            array('name' => 'clientNic~', 'label' => 'Client\'s NIC', 'placeholder' => 'Filter by client\'s NIC'),
            array('name' => 'clientMobileTelephone', 'label' => 'Client\'s mobile', 'placeholder' => 'Filter by client\'s mobile'),
            array('name' => 'clientName*', 'label' => 'Client\'s name', 'placeholder' => 'Filter by client\'s name'),
        );
    }

    protected function getHeaders()
    {
        return array(
            'bookingTime' => 'Booking time',
            'reference' => 'Reference',
            'status' => 'Status',
            'clientMobileTelephone' => 'Mobile',
            'fromCity' => 'From',
            'toCity' => 'To',
        );
    }

    protected function loadFromPost(&$model)
    {
        $model->remarks = $this->params()->fromPost('remarks');
    }
}

