<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 7/25/14
 * Time: 4:17 PM
 */

namespace Application\Form;


use Zend\Di\ServiceLocator;
use Zend\Form\Form;
use Zend\ServiceManager\ServiceManager;
use Zend\Validator\EmailAddress;
use Zend\Validator\Identical;

class Signup extends Form{

    public function __construct($name = null)
    {

        // we want to ignore the name passed
        parent::__construct('login');

        $this->setAttributes(array(
            'method'=> 'post',
            'class' => 'form'
        ));

        $this->add(array(
            'type' => 'Zend\Form\Element\Radio',
            'name' => 'gender',
            'options' => array(
                'label' => 'Gender',
                'label_attributes' => array('class'=>'form-choice-label form-choice-gender gender-::VALUE::'),
                'value_options' => array(
                    'female' => 'Femme',
                    'male' => 'Homme',
                ),
            )
        ));

        //email
        $this->add(array(
            'name' => 'email',
            'attributes' => array(
                'type'  => 'email',
                'class' => 'form-control',
                'required' => 'required',
                'maxlength' => 150
            ),
            'filters' => array(
                array('name' => 'StringTrim'),
            ),

        ));

        //password
        $this->add(array(
            'name' => 'password',
            'attributes' => array(
                'type' => 'password',
                'class' => 'form-control',
                'required' => 'required',
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
                'required' => 'required',
                'maxlength' => 150
            ),
            'options' => array(
                'label' => 'Re type Password',
            ),
        ));

        //countries
        $this->add(array(
            'type' => 'Zend\Form\Element\Radio',
            'name' => 'country',
            'options' => array(
                'label' => 'Countries',
                'label_attributes' => array('class'=>'form-choice-label form-choice-country country-::VALUE::'),
                'value_options' => array(
                    '3' => 'BE',
                    '1' => 'LU',
                    '7' => 'CH',
                    '2' => 'FR',
                ),
            )
        ));

        //zodiacs
        $this->add(array(
            'type' => 'Zend\Form\Element\Radio',
            'name' => 'zodiac',
            'options' => array(
                'label' => 'Zodiac',
                'label_attributes' => array('class'=>'form-choice-label form-choice-zodiac zodiac-::VALUE::'),
                'value_options' => array(
                    'aries' => 'Bélier',
                    'taurus' => 'Taureau',
                    'gemini' => 'Gémeaux',
                    'cancer' => 'Cancer',
                    'leo' => 'Lion',
                    'virgo' => 'Vierge',
                    'libra' => 'Balance',
                    'scorpio' => 'Scorpion',
                    'sagittarius' => 'Sagittaire',
                    'capricorn' => 'Capricorne',
                    'aquarius' => 'Verseau',
                    'pisces' => 'Poissons',

                ),
            )
        ));

    }

    /**
     * set validations
     */
    public function setInputFilters(ServiceManager $sl){
        $filter = $this->getInputFilter();

        //gender
        $filter->add(array(
            'name' => 'gender',
            'required' => true,
            'filters'  => array(
                array('name' => 'StripTags'),
                array('name' => 'StringTrim'),
            ),
        ));

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
                    'name'    => 'Db\NoRecordExists',
                    'options' => array(
                        'table'     => 'credits_customers',
                        'field'     => 'email',
                        'adapter'   => $sl->get('Zend\Db\Adapter\Adapter'),
                        'message'   => 'Email already registered.',
                    ),
                ),
            ),
        ));
        //password
        $filter->add(array(
            'name' => 'password',
            'filters'  => array(
                array('name' => 'StringTrim'),
            ),
            'required' => true,
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
        $filter->add(array(
            'name' => 'retypepassword',
            'required' => true,
            'filters'  => array(
                array('name' => 'StringTrim'),
            ),
            'validators' => array(
                array(
                    'name' => 'Identical',
                    'options' => array(
                        'token' => 'password', // name of first password field
                        'messages' => array(\Zend\Validator\Identical::NOT_SAME => "Les deux jetons passés ne correspondent pas"),
                    ),
                ),
            ),
        ));

        //country
        $filter->add(array(
            'name' => 'country',
            'required' => true,
            'filters'  => array(
                array('name' => 'StripTags'),
                array('name' => 'StringTrim'),
            ),
        ));

        //zodiac
        $filter->add(array(
            'name' => 'zodiac',
            'required' => true,
            'filters'  => array(
                array('name' => 'StripTags'),
                array('name' => 'StringTrim'),
            ),
        ));

        $this->setInputFilter($filter);
    }
} 