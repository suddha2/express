<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 4/24/15
 * Time: 11:40 AM
 */

namespace Admin\Form;


use Application\Form\Base;

class User extends Base{

    public function __construct()
    {
        parent::__construct('user-form');

        $this->setAttributes(array(
            'method'=> 'post',
            'class' => 'form',
            'ng-submit' => 'save()',
        ));

        $fName = new \Zend\Form\Element\Text('firstName');
        $fName->setLabel('First Name')
            ->setAttributes(array(
                'ng-model' => 'form.firstName',
                'class' => 'form-control',
                'required' => 'required',
                'placeholder' => 'First Name',
            ));
        $this->add($fName);

        $submit = new \Zend\Form\Element\Submit('submit');
        $submit->setValue('Submit')
            ->setAttributes(array(
                'class' => 'btn btn-primary'
            ));
        $this->add($submit);

    }
}