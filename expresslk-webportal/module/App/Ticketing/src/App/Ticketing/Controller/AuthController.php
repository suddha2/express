<?php

namespace App\Ticketing\Controller;

use Api\Client\Soap\Core\Bus;
use Api\Operation\Request\QueryCriteria;
use App\Base\AppException;
use App\Base\Controller\AbstractController;
use App\Ticketing\Model\Message;
use Zend\View\Model\JsonModel;
use Api\Client\Rest\Factory as RestFactory;

class AuthController extends AbstractController{

    public function loginAction()
    {
        $response = array();
        try {
            $params = $this->params()->fromJson();
            $username = $params['username'];
            $password = $params['password'];
            $bus = $params['bus'];

            //check for login credentials
            try {
                //logout
                $authService = $this->getServiceLocator()->get('AuthService')->clearIdentity();

                /** @var \App\Base\Auth $auth */
                $auth = $this->getServiceLocator()->get('App\Auth');
                $user = $auth->login($username, $password);
                //check if user is logged in
                if($user===false){
                    throw new \Exception('No user account');
                }
            } catch (\Exception $e) {
                throw new AppException(Message::ERROR_USER_NOACCOUNT);
            }

            //check for bus
            $busEntity = new RestFactory($this->getServiceLocator(), 'bus');
            //build criteria
            $criteria = new QueryCriteria();
            $criteria->addParam('plateNumber', $bus);
            //$criteria->addParam('status', 'true');
            $busDetails = $busEntity->getList($criteria);
            $busDetails = $busDetails->body;
            if(!empty($busDetails) && count($busDetails)>0){
                /** @var Bus $busData */
                $busData = $busDetails[0];
            }else{
                throw new \Exception('The bus you entered does not exist.');
            }

            //set response
            $this->addSuccess(array(
                'busId' => $busData->id,
                'busName' => $busData->name,
                'busPlate' => $busData->plateNumber,
                'token' => $password,
                'username' => $user->username,
                'userid' => $user->id,
            ));
        } catch (\Exception $e) {
            $this->addError($e);
        }

        return $this->getJsonResponse();
    }

    public function checkupdateAction()
    {
        $action = $this->params()->fromQuery('get');
        /**
         * @todo change the version as in the .apk file. "ExpressConductor_{}.apk"
         */
        $NEW_VERSION_CODE = 121;
        $NEW_VERSION_NAME = '1.2.1';

        $fileName = 'Express-Ticketing-v'. $NEW_VERSION_NAME .'.apk';
        //$filePlace = 'data/app_conductor/apk/'. $fileName;
        $filePlace = '/var/www/html/express-conductorapp-versions/'. $fileName; //put in root temporarily

        //if action is to download
        if($action=='d'){
            $fileSize = filesize($filePlace);

            if (file_exists($filePlace)) {
                //header('Content-Description: File Transfer');
                header('Content-Type: application/vnd.android.package-archive');
                header('Content-Disposition: attachment; filename='. $fileName);
                header('Content-Length: ' . $fileSize);
                //ob_clean();
                //flush();
                readfile($filePlace);
                exit;
            }
            exit;
            //ouptput
//            $fileContents = file_get_contents($filePlace);
//            $response = $this->getResponse();
//            $response->setContent($fileContents);
//            $headers = $response->getHeaders();
//            $headers->clearHeaders()
//                ->addHeaderLine('Content-Type', 'application/vnd.android.package-archive')
//                ->addHeaderLine('Content-Disposition', 'attachment; filename=' . $fileName . '')
//                ->addHeaderLine('Content-Length', strlen($fileContents));
//            return $response;

            //stream
//            $response = new \Zend\Http\Response\Stream();
//            $response->setStream(fopen($filePlace, 'r'));
//            $response->setStatusCode(200);
//
//            $headers = new \Zend\Http\Headers();
//            $headers->addHeaderLine('Content-Type', 'application/vnd.android.package-archive')
//                ->addHeaderLine('Content-Disposition', 'attachment; filename=' . $fileName . '')
//                ->addHeaderLine('Content-Length', filesize($filePlace));
//
//            $response->setHeaders($headers);
//            return $response;
        }else{
            //change this when a new version is to be released
            $response = array(
                'updateInfo' => array(
                    'appName' => 'Express Ticketing',
                    'appDescription' => 'Ticketing application for conductors',
                    'packageName' => 'lk.busbooking.mobile.expressticketingapp',
                    'versionCode' => $NEW_VERSION_CODE,
                    'versionName' => $NEW_VERSION_NAME,
                    'forceUpdate' => true, //if true,then when the user cancel the update,exit the application.
                    'autoUpdate' => true, //if true,the new app will be downloaded and install
                    //'apkUrl' => $this->getRequest()->getUriString() . '?get=d',
                    'apkUrl' => 'http://158.69.202.65/express-conductorapp-versions/'. $fileName,
                    'updateTips' => array(
                        'default' => 'Click confirm to update. This is mandatory.',
                        'en' => '',
                    ),
                )
            );

            //stop if file not exists
//            if (!file_exists($filePlace)) {
//                $response['updateInfo']['versionCode'] = 1;
//                $response['updateInfo']['versionName'] = '1';
//            }
            return new JsonModel($response);
        }

    }
}