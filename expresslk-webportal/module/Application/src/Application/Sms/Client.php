<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 12/30/14
 * Time: 9:52 AM
 */
namespace Application\Sms;

use Application\Util\Log;
use Zend\ServiceManager\ServiceManager;
use Application\Domain;
class Client {

    private $_endPoints = array();
    private $_smsMask;
    private $_sm;
    private $_smsConfig;

    public function __construct(ServiceManager $serviceManager)
    {
        $this->_sm = $serviceManager;
        //set sms config
        $config = $this->_sm->get('Config');
        $this->_smsConfig = $config['sms'];

        $this->_smsMask = \Application\Domain::getDomain(); //$this->_smsConfig['src'];
        //fillup endpoints
        $this->_endPoints = $this->_smsConfig['endPoints'];
    }

    /**
     * Send an SMS
     * @param $to
     * @param $template
     * @param $variables array Key value pairs of replacements
     * @throws \Exception
     */
    public function sendSMS($to, $template, $variables)
    {
        //validate and format to number
        $to = $this->_formatTo($to);
        //set values
        $text = $this->_formatMessage($template, $variables);

        //loop through each end point. Continue all the points if the sms is not sent
        $endpLen = count($this->_endPoints);
        $i=0;
        foreach ($this->_endPoints as $endPointKey=>$endPoint) {
            $i++;
            try{
                //send in current endpoint
                $this->_passSMS($endPointKey, $to, $text);
                //message sent, break from the loop
                break;
            }catch (\Exception $e){
                //if this is last of the loop, message could not be sent anyway. Throw exception back.
                if($endpLen==$i){
                    throw new \Exception($e->getMessage() . ' - Endpoint: ' . $endPointKey . $i);
                }
                (new Log())->crit($e);
                //message was not sent. Continue onto the next endpoint.
                continue;
            }
        }
    }

    /**
     * Mock sms sending and return template
     * @param $to
     * @param $template
     * @param $variables
     * @return mixed
     */
    public function mockSMS($to, $template, $variables)
    {
        //validate and format to number
        $to = $this->_formatTo($to);
        //set values
        return $this->_formatMessage($template, $variables);
    }

    /**
     * @param $to
     * @return string
     */
    private function _formatTo($to)
    {
        $phone = preg_replace("/[^0-9]/", "", trim($to));
        //check if there are 10 numbers
        if(strlen($phone)>10){
            throw new \Exception('Invalid Phone number');
        }elseif(strlen($phone)==9){
            //append 0 to front if the phone number is 9 digits
            $phone = '0'. $phone;
        }elseif(strlen($phone)<9){
            throw new \Exception('Invalid Phone number. Too short.');
        }
        //add 94 in place of front 0
        $phone = preg_replace('/^0/','94', $phone);
        //$phone = '94' . $phone;

        return $phone;
    }

    /**
     * @param $template
     * @param $variables
     * @return mixed
     */
    private function _formatMessage($template, $variables)
    {
        $removableWords = array('0115-116-117');

        $message = str_replace(array_keys($variables), array_values($variables), $template);

        //remove key words if length is too long
        if(strlen($message) > 120){
            $message = str_replace($removableWords, '', $message);
        }

        return $message;
    }

    /**
     * Connect to gateway and send sms
     * @param $endPointKey
     * @param $to
     * @param $text
     * @return bool
     * @throws \Exception
     */
    private function _passSMS($endPointKey, $to, $text)
    {
        $text = urlencode($text);
        //get sms endpoint data
        $smsPoint = $this->_endPoints[$endPointKey];
        $server     = $smsPoint['endPoint'];
        $userId     = $smsPoint['userID'];
        $password   = $smsPoint['password'];

        // create a new cURL resource
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);

        // set URL and other appropriate options
        curl_setopt($ch, CURLOPT_URL, "$server?username=".
            $userId ."&password=". $password.
            "&src=". $this->_smsMask . "&dst=".$to."&msg=".$text ."&dr=1");

        //curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
        //don't show anything
        //curl_setopt($ch, CURLOPT_NOBODY,1);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        //set timeout
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT ,0);
        curl_setopt($ch, CURLOPT_TIMEOUT, 30); //timeout in seconds

        // grab URL and pass it to the browser
        $response = curl_exec($ch);

        // close cURL resource, and free up system resources
        curl_close($ch);

		curl_setopt_array($curl, array(
		  //CURLOPT_URL => "http://sms.textware.lk:5000/sms/send_sms.php?username=express418&password=Expr48LK&src=BusBooking&dst=94773429310&msg=Test%2520SMS&dr=1",
		  CURLOPT_URL => $smsPoint."?username=".$userId."&password=".$password."&src=".$this->_smsMask."&dst=".$to."&msg=".$text."&dr=1",
		  CURLOPT_RETURNTRANSFER => true,
		  CURLOPT_ENCODING => "",
		  CURLOPT_MAXREDIRS => 10,
		  CURLOPT_TIMEOUT => 0,
		  CURLOPT_FOLLOWLOCATION => true,
		  CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
		  CURLOPT_CUSTOMREQUEST => "GET",
		));

		$response = curl_exec($curl);

		curl_close($curl);


        //check if message sent properly
        $resPortions = explode(":", $response);
        if (strtoupper(trim($resPortions[0])) != strtoupper("Operation success")){
            throw new \Exception('Sending sms failed. '. $response);
        }
        return true;
    }

}