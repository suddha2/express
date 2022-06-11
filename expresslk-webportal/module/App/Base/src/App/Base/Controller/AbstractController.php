<?php


namespace App\Base\Controller;

use App\Base\AppException;
use Application\Exception\SessionTimeoutException;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\JsonModel;

class AbstractController extends AbstractActionController{

    private $successData = null;

    private $errorData = null;

    public function addSuccess($data)
    {
        if(is_null($this->successData)){
            $this->successData = array();
        }
        //make it array if not
        $data = is_array($data)? $data : array($data);

        //append data
        $this->successData = array_merge_recursive($this->successData, $data);
    }

    public function addError($data)
    {
        if(is_null($this->errorData)){
            $this->errorData = array();
        }

        //check if this is app exception
        if($data instanceof AppException){
            $this->errorData['code'] = $data->getAppCode();
        }
        elseif($data instanceof SessionTimeoutException){
            $this->errorData['message'] = $data->getMessage();
            $this->errorData['type'] = 'SessionTimeout';
        }
        elseif($data instanceof \Exception){
            $this->errorData['message'] = $data->getMessage();
        }else{
            $this->errorData[] = $data;
        }
    }

    public function getJsonResponse()
    {
        $response = array();
        //check for success
        if (!is_null($this->successData)) {
            $response['success'] = $this->successData;
        }
        //check for error
        if (!is_null($this->errorData)) {
            $response['error'] = $this->errorData;
        }
        return new JsonModel($response);
    }
}