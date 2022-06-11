<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 10/5/14
 * Time: 6:02 PM
 */

namespace Api\Manager;

use Api\Client\Soap\Core\SessionCriteria;
use Application\Util\Log;

class Session extends Base{

    /**
     * @param string $locale
     * @return string
     * @throws \SoapFault
     */
    public function create($locale = 'en_US')
    {
        try {
            $sessCriteria = new SessionCriteria($locale);
            return $this->getSearchService()->createSession($sessCriteria);
        } catch (\SoapFault $e) {
            //log error
            $log = new Log();
            $log->emerg($e, array(
                'Soap_Fault_in_session' => 'Soap Service failed.'
            ));
            //throw it back
            throw $e;
        }
    }

    /**
     * @param $sessionId
     * @return bool
     */
    public function refreshSession($sessionId)
    {
        $oSessionResponse = $this->getSearchService()->refreshSession($sessionId);

        //check if status is okay
        if($this->responseIsValid($oSessionResponse)){
            return true;
        }
        return false;
    }

} 