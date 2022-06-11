<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 9/5/15
 * Time: 10:25 AM
 */

namespace Application\Navigation\Service;


use Zend\Navigation\Service\DefaultNavigationFactory;

class FrontMain extends DefaultNavigationFactory{

    protected function getName()
    {
        return 'frontMain';
    }
}