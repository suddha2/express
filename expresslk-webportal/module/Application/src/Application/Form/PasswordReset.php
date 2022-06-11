<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 8/6/14
 * Time: 11:39 AM
 */

namespace Application\Form;

/**
 * @uses Zend\Form\Form
 */
use Zend\Di\ServiceLocator;
use Zend\Form\Form,
    Zend\Form\Element,
    Zend\Validator\EmailAddress;
use Zend\ServiceManager\ServiceManager;

class PasswordReset extends Form{

    /**
     * Initialize Form
     */
    public function __construct($name = null)
    {
        // we want to ignore the name passed
        parent::__construct();
        $this->setAttributes(array(
            'method'=> 'post',
            'class' => 'form'
        ));

        $this->add(array(
            'name' => 'email',
            'attributes' => array(
                'type'  => 'email',
                'class' => '',
                'required' => 'required'
            ),
            'options' => array(
                'label' => 'Email',
            ),
            'filters' => array(
                array('name' => 'StringTrim'),
            ),

        ));

    }

    public function setInputFilters(ServiceManager $sl){
        $filter = $this->getInputFilter();
        //email
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
                    'name'    => 'Db\RecordExists',
                    'options' => array(
                        'table'     => 'credits_customers',
                        'field'     => 'email',
                        'adapter'   => $sl->get('Zend\Db\Adapter\Adapter'),
                        'message'   => 'Email is not found.',
                    ),
                ),
            ),
        ));

        $this->setInputFilter($filter);
    }
} 