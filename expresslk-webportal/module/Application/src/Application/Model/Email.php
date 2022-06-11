<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 4/3/14
 * Time: 10:36 AM
 */

namespace Application\Model;

use Zend\Mail;
use Zend\ServiceManager\ServiceManager;
use Zend\Mime\Message as MimeMessage;
use Zend\Mime\Part as MimePart;
use Zend\View\Model\ViewModel;

class Email extends Mail\Message
{
    const TEMPLATE_BOOKING_SUCCESS = 'booking-success';

    /**
     * @var $_serviceLocator \Zend\ServiceManager\ServiceManager
     */
    protected $_serviceLocator;

    /**
     * get the DB layout and subject
     * @var array
     */
    private $_aLayout = array(
        'subject' => '',
        'body' => ''
    );

    /**
     * variable to store main template
     * @var string
     */
    private static $_sMainTemplate = null;

    private $_aDefParameters = array(
        '::SERVER_NAME::' => ''
    );

    private $_bodyParts = array();

    /**
     * @param ServiceManager $_serviceLocator
     */
    public function __construct(ServiceManager $_serviceLocator)
    {
        //set service locator
        $this->_serviceLocator = $_serviceLocator;

        //get config data
        $oConfig = $this->_serviceLocator->get('config');

        //set default parameters
        $this->_aDefParameters = array(
            '::SERVER_NAME::' => 'http://'. $oConfig['system']['serverName']
        );

        $mailDefaults = $oConfig['mail']['defaults'];
        parent::addFrom($mailDefaults['from']['email'], $mailDefaults['from']['name']);
        parent::addReplyTo($mailDefaults['replyto']['email'], $mailDefaults['replyto']['name']);
        parent::setEncoding('UTF-8');
    }

    /**
     * Load and set the DB layout to the email
     * @param $sTempleName
     * @return Email
     * @throws \Exception
     */
    public function setLayout($sTempleName)
    {
        if ($sTempleName != '') {
            $viewRender = $this->_serviceLocator->get('ViewRenderer');
            $oView = new ViewModel();
            $oView->setTerminal(true);
            $oView->setTemplate('application/email-template/'. $sTempleName);

            $this->_aLayout['body'] = $viewRender->render($oView);
        } else {
            throw new \Exception('Invalid email layout');
        }

        return $this;
    }

    /**
     * set the template of the Email
     * @param array $aParams
     * @param string $sBody
     * @return string
     */
    private function _setTemplate($aParams, $sBody)
    {
        //append default replace parameters
        $aParams = array_merge($aParams, $this->_aDefParameters);
        //replace parameters in template
        $sBody = str_replace(array_keys($aParams), array_values($aParams), $sBody);
        return $sBody;
    }

    /**
     * get html template
     * @return string
     */
    private function _getMainTemplate()
    {
        //include email template if not already included
        if (!self::$_sMainTemplate) {
            self::$_sMainTemplate = file_get_contents(__DIR__ . '/../../../view/template/email.phtml');
        }
        return self::$_sMainTemplate;
    }

    /**
     * Set Html body
     * @param $mParams array key value or just plain html string
     * @return Email
     */
    public function setBodyHtml($mParams)
    {
        //if an array passed as the parameter
        if (is_array($mParams)) {
            $sBody = $this->_setTemplate($mParams, $this->_aLayout['body']);
        } else { //else the body of the email is sParams
            $sBody = $mParams;
        }

        //set into template
        $template = str_replace(array('::BODY_TEMPLATE::'), array($sBody), $this->_getMainTemplate());
        $html = new MimePart($template);
        $html->type = "text/html";

        //save mime part in body array
        $this->_bodyParts[] = $html;

        return $this;
    }

    /**
     * set the plain text for the email body
     * @param $aParams
     * @return $this
     */
    public function setBodyText($aParams)
    {
        //if an array passed as the parameter
        if (is_array($aParams)) {
            $sBody = $this->_setTemplate($aParams, $this->_aLayout['body']);
        } else { //else the body of the email is sParams
            $sBody = $aParams;
        }

        $text = new MimePart($sBody);
        $text->type = "text/plain";
        //save mime part in body array
        $this->_bodyParts[] = $text;

        return $this;
    }

    /**
     * set the email subject, if local subject is null then template subject wiil set
     *
     * @param array $sSubject
     * @return Email
     */
    public function setSubject($sSubject = null)
    {
        if (is_array($sSubject) && !empty($sSubject)) {
            $sSubject = $this->_setTemplate($sSubject, $this->_aLayout['subject']);
        } else if (empty($sSubject)) {
            $sSubject = $this->_aLayout['subject'];
        }

        parent::setSubject($sSubject);

        return $this;
    }

    /**
     *
     * @param MimePart $part
     * @return $this
     */
    public function addPart(MimePart $part)
    {
        $this->_bodyParts[] = $part;
        return $this;
    }

    /**
     * Send email
     * @throws \Exception
     */
    public function send()
    {
        //check if body exists
        $body = parent::getBody();
        //if body is not set
        if (!empty($this->_bodyParts)) {
            $messageBody = new MimeMessage();
            $messageBody->setParts($this->_bodyParts);
            //set body
            $this->setBody($messageBody);
        } else {
            throw new \Exception('Email body not set');
        }

        //send email through transport
        $transport = $this->_serviceLocator->get('Mail\Transport\Smtp');
        $transport->send($this);
    }

}