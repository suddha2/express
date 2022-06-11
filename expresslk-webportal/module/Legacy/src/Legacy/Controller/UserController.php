<?php

namespace Legacy\Controller;

use Api\Client\Soap\Core\User;
use Api\Manager\Base;
use Application\Domain;
use Application\Exception\HttpStatusException;
use Application\Exception\SessionTimeoutException;
use Zend\Debug\Debug;
use Zend\View\Model\ViewModel;

class UserController extends EntityController
{
    public function __construct()
    {
        parent::__construct('userLight');
        $this->controller = 'user';
    }

    protected function getFilters()
    {
        return array(
            array('name' => 'firstName*', 'label' => 'First name', 'placeholder' => 'Filter by first name'),
            array('name' => 'lastName*', 'label' => 'Last name', 'placeholder' => 'Filter by last name'),
            array('name' => 'username~', 'label' => 'username', 'placeholder' => 'Filter by username'),
        );
    }

    protected function getHeaders()
    {
        return array(
            'username' => 'Username',
            'firstName' => 'First name',
            'lastName' => 'Last name',
        );
    }

    protected function loadFromPost(&$model)
    {
        $model->firstName = $this->params()->fromPost('firstName');
        $model->lastName = $this->params()->fromPost('lastName');
        $model->email = $this->params()->fromPost('email');
        $model->mobileTelephone = $this->params()->fromPost('mobileTelephone');
        $model->status = $this->params()->fromPost('status');
        $model->userGroups = $this->params()->fromPost('userGroups');
    }

    protected function verifyPassword()
    {
        $password1 = $this->params()->fromPost('password1');
        $password2 = $this->params()->fromPost('password2');
        if (empty($password1)) {
            return "Password cannot be empty!";
        }
        if (strlen($password1) < 6) {
            return "Minimum length of the password is 6 characters!";
        }
        if ($password1 != $password2) {
            return "Passwords do not match!";
        }
        return true;
    }

    protected function getUserDomain()
    {
        $sSuffix = '';
        switch (Domain::getDomain()){
            case Domain::NAME_MOBITEL:
                $sSuffix = '@mb365';
        }
        return $sSuffix;
    }

    /**
     * @param $sUsername
     * @return string
     */
    protected function getUsernameWithPrefix($sUsername)
    {
        $sSuffix = $this->getUserDomain();
        //check if the prefix already exists. If not add the prefix
        if(strstr($sUsername, $sSuffix)===false){
            return $sUsername . $sSuffix;
        }
        return $sUsername;
    }

    protected function getDataForEditOrCreate()
    {
        $ug = new CrudController('userGroup');
        $userGroups = $ug->getEntityList($this->getServiceLocator(), array('pageRows' => '-1'));
        return array('userGroups' =>  $userGroups);
    }

    public function createAction()
    {
        try {
            /** @var User $model */
            $model = Base::getEntityObject('User');
            $data = $this->getDataForEditOrCreate();
            $error = null;

            if (!empty($this->params()->fromPost('update'))) {
                $this->loadFromPost($model);
                $model->username = $this->getUsernameWithPrefix($this->params()->fromPost('username'));
                $result = $this->verifyPassword();
                if ($result === true) {
                    $password = $this->params()->fromPost('password1');
                    $response = $this->crud->createEntity($this->getServiceLocator(), $model, array('password' => $password));
                    if ($response->code == 200) {
                        return $this->redirect()->toRoute('legacy/default', array('controller' => $this->controller, 'action' => 'view', 'id' => $response->body->id));
                    } else {
                        if (is_string($response->body)) {
                            $error = $response->body;
                        }
                    }
                } else {
                    $error = $result;
                }
            }
        } catch (\Exception $e) {
            $error = 'An error occured on the operation. Please contact admin support if problem persists.';
        }

        return new ViewModel(
            array_merge($data, array(
                'model' => $model,
                'error' => $error,
                'controller' => $this->controller,
                'usernameSuffix' => $this->getUserDomain(),
            )
        ));
    }

    public function resetpasswordAction()
    {
        $id = $this->params()->fromRoute('id');
        try {
            /** @var User $model */
            $model = $this->crud->getEntity($this->getServiceLocator(), $id);
            $error = null;

            if (!empty($this->params()->fromPost('update'))) {
                $result = $this->verifyPassword();
                if ($result === true) {
                    $password = $this->params()->fromPost('password1');
                    $oldpassword = $this->params()->fromPost('oldpassword');
                    try {
                        /** @var $auth \Api\Manager\Authentication */
                        $auth = $this->getServiceLocator()->get('Api\Manager\Authentication');
                        $response = $auth->changePasswordWithouotOldPassword($model->username, $password);
                        if ($response) {
                            return $this->redirect()->toRoute('legacy/default', array('controller' => $this->controller, 'action' => 'view', 'id' => $id));
                        } else {
                            $error = 'Error in operation.';
                        }
                    } catch (SessionTimeoutException $e) {
                        $error = 'Existing password doesnt match. Please try again.';
                    } catch (HttpStatusException $statusException) {
                        switch ($statusException->getCode()){
                            case 400:
                                //bad request
                                $error = 'New password is not valid';
                                break;
                            case 401:
                                //bad request
                                $error = 'Existing password doesnt match. Please try again.';
                                break;
                            default:
                                $error = 'Error in operation. Code: '. $statusException->getCode();
                        }
                    }
                } else {
                    $error = $result;
                }
            }
        } catch (\Exception $e) {
            $error = $e->getMessage();
        }

        return new ViewModel(array(
            'model' => $model,
            'error' => $error,
            'controller' => $this->controller,
            'usernameSuffix' => $this->getUserDomain(),
        ));
    }
}
