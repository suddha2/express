<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 8/12/14
 * Time: 1:16 PM
 */

namespace Application\Form;

use Zend\ServiceManager\ServiceManager;

class EditProfile extends Signup{

    public function __construct($name = null)
    {
        // we want to ignore the name passed
        parent::__construct();

        //customer id
        $this->add(array(
            'name' => 'customer_id',
            'attributes' => array(
                'type' => 'hidden',
            ),
        ));

        //actual password
//        $this->add(array(
//            'name' => 'actpassword',
//            'attributes' => array(
//                'type' => 'password',
//                'class' => 'form-control',
//                'required' => 'required',
//                'maxlength' => 150
//            ),
//            'options' => array(
//                'label' => 'Password',
//            ),
//        ));
        //password
        $this->add(array(
            'name' => 'password',
            'attributes' => array(
                'type' => 'password',
                'class' => 'form-control',
                'maxlength' => 150
            ),
            'options' => array(
                'label' => 'Password',
            ),
        ));

        //retype password
        $this->add(array(
            'name' => 'retypepassword',
            'attributes' => array(
                'type' => 'password',
                'class' => 'form-control',
                'maxlength' => 150
            ),
            'options' => array(
                'label' => 'Re type Password',
            ),
        ));

        //telephone
        $this->add(array(
            'name' => 'telephone',
            'attributes' => array(
                'type'  => 'text',
                'class' => 'form-control',
                'required' => 'required',
                'maxlength' => 150
            ),
            'filters' => array(
                array('name' => 'StringTrim'),
            ),
        ));

    }

    public function setInputFilters(ServiceManager $sl){
        //set parent filters
        parent::setInputFilters($sl);

        $userId = $sl->get('AuthService')->getIdentity()->customer_id;

        $filter = $this->getInputFilter();

        //email
        $filter->remove('email');
        $filter->add(array(
            'name' => 'email',
            'required' => true,
            'filters'  => array(
                array('name' => 'StripTags'),
                array('name' => 'StringTrim'),
            ),
            'validators' => array (
                'EmailAddress' => array (
                    'name' => 'EmailAddress',
                    'options' => array(
                        'message'   => "Adresse e-mail n'est pas valide. Vérifiez s'il vous plaît.",
                    )
                ),
                array(
                    'name'    => 'Db\NoRecordExists',
                    'options' => array(
                        'table'     => 'credits_customers',
                        'field'     => 'email',
                        'adapter'   => $sl->get('Zend\Db\Adapter\Adapter'),
                        'message'   => 'Email already registered.',
                        'exclude'   => array(
                            'field' => 'customer_id',
                            'value' => $userId
                        )
                    ),
                ),
            ),
        ));

        //actual password
//        $filter->add(array(
//            'name' => 'actpassword',
//            'required' => true,
//            'filters'  => array(
//                array('name' => 'StringTrim'),
//            ),
//            'validators' => array (
//                array(
//                    'name'    => 'Db\RecordExists',
//                    'options' => array(
//                        'table'     => 'credits_customers',
//                        'field'     => 'password',
//                        'adapter'   => $sl->get('Zend\Db\Adapter\Adapter'),
//                        //'message'   => 'Password is n.',
//                        'exclude' => 'customer_id = '.$userId
//                    ),
//                ),
//            ),
//        ));

        //password
        $filter->remove('password');
        $filter->add(array(
            'name' => 'password',
            'required' => false,
            'filters'  => array(
                array('name' => 'StringTrim'),
            ),
            'validators' => array (
                'stringLength' => array (
                    'name' => 'StringLength',
                    'options' => array (
                        'min' => '4',
                    ),
                ),
            ),
        ));
        //retypepassword
        $filter->remove('retypepassword');
        $filter->add(array(
            'name' => 'retypepassword',
            'required' => false,
            'filters'  => array(
                array('name' => 'StringTrim'),
            ),
            'validators' => array(
                array(
                    'name' => 'Identical',
                    'options' => array(
                        'token' => 'password', // name of first password field
                    ),
                ),
            ),
        ));

        $this->setInputFilter($filter);
    }
} 