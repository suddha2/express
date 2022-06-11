<?php


namespace Application;


class Util {

    /**
     * @param $number
     * @return string
     */
    public static function getColorByNumber($number) {
        $n = crc32($number);
        $n &= 0xffffffff;
        return("#".substr("000000".dechex($n),-6));
    }

    /**
     * Do a key sort for all the elements
     * @param $input
     * @return array
     */
    public static function ksortDeep($input)
    {
        if ( !is_object($input) || !is_array($input) ) {
            return $input;
        }

        foreach ( $input as $k=>$v ) {
            if ( is_object($v) || is_array($v) ) {
                if(is_object($input)){
                    $input->{$k} = self::ksortDeep($v);
                }else{
                    $input[$k] = self::ksortDeep($v);
                }
            }
        }

        if ( is_array($input) ) {
            ksort($input);
        }

        return $input;
    }

    /**
     * Sort ascending by passsed in key.
     * @param $objectArray
     * @param $key
     * @return mixed
     */
    public static function sortByKey($objectArray, $key)
    {
        //convert into an associative array
        $copiedArray = json_decode(json_encode($objectArray), true);

        usort($copiedArray, function ($a, $b) use ($key) {
            $sort = $a[$key] - $b[$key];
            return ($sort==0? 0 : ($sort>0)? 1 : -1);
        });

        return $copiedArray;
    }

    /**
     * Is the passed in an associate array
     * @param array $array
     * @return bool
     */
    public static function is_assoc(array $array) {
        return (bool) count(array_filter(array_keys($array), 'is_string'));
    }

}