<?php

class HmacUtils {
    
    private function __construct() {
    }

    public static function genarateHmac($secret, $data) {
        //$data = " {\"clientId\":10000000,\"merchantType\":\"PYCINTERNATIONAL\",\"startDate\":\"2016-01-01T00:00:00.000+1100\",\"endDate\":\"2016-01-31T00:00:00.000+1100\"}"
        $Hmac = hash_hmac('sha256', utf8_decode($data), utf8_decode($secret), FALSE);
        return $Hmac;
    }

}
