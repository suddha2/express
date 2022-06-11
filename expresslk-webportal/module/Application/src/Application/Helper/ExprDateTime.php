<?php


namespace Application\Helper;


class ExprDateTime extends \DateTime{

    /**
     * @param $dateString
     * @return ExprDateTime
     */
    public static function getDateFromString($dateString)
    {
        $dt = new self();
        $dt->setTimestamp(strtotime($dateString));
        return $dt;
    }

    /**
     * Get date from express services
     * @param $dateMixed
     * @return ExprDateTime
     * @throws \Exception
     */
    public static function getDateFromServices($dateMixed)
    {
        if(is_string($dateMixed)){
            //get from string
            return self::getDateFromString($dateMixed);
        }elseif(is_numeric($dateMixed)){
            //convert to seconds
            $seconds = floor($dateMixed/1000);
            $dt = new self();
            $dt->setTimestamp($seconds);
            return $dt;
        }else{
            throw new \Exception('Malformatted date');
        }
    }

    /**
     * Get timestamp in miliseconds
     * @return int
     */
    public function getTimestampMiliSeconds()
    {
        return $this->getTimestamp() * 1000;
    }

    public function formatForJs()
    {
        return $this->format(DATE_ISO8601);
    }

    /**
     * // Converts a unix timestamp to iCal format (UTC) - if no timezone is
     * // specified then it presumes the uStamp is already in UTC format.
     * // tzone must be in decimal such as 1hr 45mins would be 1.75, behind
     * // times should be represented as negative decimals 10hours behind
     * // would be -10
     * @param int $uStamp
     * @param float $tzone
     * @return bool|string
     */
    public static function unixToiCal($uStamp = 0, $tzone = 0.0)
    {

        $uStampUTC = $uStamp + ($tzone * 3600);
        $stamp  = date("Ymd\THis", $uStampUTC);

        return $stamp;

    }
}