<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 4/14/14
 * Time: 11:44 AM
 */

namespace Application\Controller\Plugin;

use Zend\Mvc\Controller\Plugin\Params as ZendParams;

class Params extends ZendParams
{
    /**
     * Get json output from body content
     * @return bool|mixed
     */
    public function fromJson()
    {
        $body = $this->getController()->getRequest()->getContent();
        if (!empty($body)) {
            $json = json_decode($body, true);
            if (!empty($json)) {
                return $json;
            }
        }

        return false;
    }

}