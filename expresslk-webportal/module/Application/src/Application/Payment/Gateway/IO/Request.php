<?php


namespace Application\Payment\Gateway\IO;


class Request
{
    const TYPE_FORMSUBMIT = 'submitForm';
    const TYPE_IFRAMEURL = 'iframeUrl';

    private $requestProcessType;
    private $requestPayload;

    /**
     * @return mixed
     */
    public function getRequestProcessType()
    {
        return $this->requestProcessType;
    }

    /**
     * @param mixed $requestProcessType
     * @return Request
     */
    public function setRequestProcessType($requestProcessType)
    {
        $this->requestProcessType = $requestProcessType;
        return $this;
    }

    /**
     * @return mixed
     */
    public function getRequestPayload()
    {
        return $this->requestPayload;
    }

    /**
     * @param mixed $requestPayload
     * @return Request
     */
    public function setRequestPayload($requestPayload)
    {
        $this->requestPayload = $requestPayload;
        return $this;
    }


}