<?php


namespace Legacy\Extend;


abstract class ProviderBase
{
    protected abstract function getPhoneNumber();
    protected abstract function setPhoneNumber($phoneNumber);
}