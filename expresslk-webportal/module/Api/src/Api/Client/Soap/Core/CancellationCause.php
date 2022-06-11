<?php

namespace Api\Client\Soap\Core;

class CancellationCause
{
    const __default = 'ClientRequested';
    const ClientRequested = 'ClientRequested';
    const NonPayment = 'NonPayment';
    const DataEntryError = 'DataEntryError';
    const Other = 'Other';


}
