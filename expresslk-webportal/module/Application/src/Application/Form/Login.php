<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 3/26/14
 * Time: 11:00 AM
 */


/**
 * @namespace
 */
namespace Application\Form;

/**
 * @uses Zend\Form\Form
 */
use Zend\Form\Form,
    Zend\Form\Element,
    Zend\Validator\EmailAddress;

/**
 * Login Form Class
 *
 * Login Form
 *
 * @category  User
 * @package   User_Form
 */
class Login extends Form
{
    /**
     * Initialize Form
     */
    public function __construct($name = null)
    {
        // we want to ignore the name passed
        parent::__construct('login');
        $this->setAttributes(array(
            'method'=> 'post',
            'class' => 'form'
        ));

        $this->add(array(
            'name' => 'username',
            'attributes' => array(
                'type'  => 'text',
                'class' => 'form-control',
                'required' => 'required'
            ),
            'options' => array(
                'label' => 'Username/Email',
            ),
            'filters' => array(
                array('name' => 'StringTrim'),
            ),
            'validators' => array(
                array(
                    'name' => 'EmailAddress',
                    'options' => array(
                        'messages' => array(
                            EmailAddress::INVALID_FORMAT => 'Email address format is invalid'
                        )
                    )
                )
            )
        ));

        $this->add(array(
            'name' => 'password',
            'attributes' => array(
                'type'  => 'password',
                'class' => 'form-control',
                'required' => 'required'
            ),
            'options' => array(
                'label' => 'Password',
            ),
        ));

        //$this->add(new Element\Csrf('csrf'));

        $this->add(array(
            'name' => 'login',
            'attributes' => array(
                'type'  => 'submit',
                'value' => 'Login',
                'id' => 'submit',
                'class' => 'btn btn-default',
            ),
        ));

        $this->setInputFilters();

    }

    /**
     *
     */
    public function setInputFilters(){
        $filter = $this->getInputFilter();
        //email
        $filter->add(array(
            'name' => 'username',
            'required' => true,
            'filters'  => array(
                array('name' => 'StripTags'),
                array('name' => 'StringTrim'),
            ),
//            'validators' => array (
//                'EmailAddress' => array (
//                    'name' => 'EmailAddress'
//                ),
//
//            ),
        ));
        //first name
        $filter->add(array(
            'name' => 'password',
            'required' => true,
            'filters'  => array(
                array('name' => 'StringTrim'),
            ),
            'validators' => array (
                'stringLength' => array (
                    'name' => 'StringLength',
                    'options' => array (
                        'max' => '200',
                    ),
                ),
            ),
        ));

        $this->setInputFilter($filter);
    }
}