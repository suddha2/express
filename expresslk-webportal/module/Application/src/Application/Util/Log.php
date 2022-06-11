<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 3/17/15
 * Time: 9:40 AM
 */

namespace Application\Util;
use Zend\Log\Formatter\Simple;
use Zend\Log\Logger;
use Zend\Log\Writer\Stream;
use Zend\Log\Exception;
use Traversable;

/**
 * Class Log
 * @package Application\Util
 *
 */
class Log extends Logger{

    private $_basePath;

    public static function init()
    {
        //set native error priority for errors as critical
        Logger::$errorPriorityMap[E_ERROR] = Logger::EMERG;

        //register custom logger for all logs
        Logger::registerErrorHandler(new Log());
        //register exception logger
        Logger::registerExceptionHandler(new Log());
    }

    /**
     * constructor
     */
    public function __construct()
    {
        //call parent with writer
        parent::__construct();
    }

    /**
     * Add a message as a log entry
     *
     * @param  int $priority
     * @param  mixed $message
     * @param  array|Traversable $extras
     * @return Logger
     * @throws Exception\InvalidArgumentException if message can't be cast to string
     * @throws Exception\InvalidArgumentException if extra can't be iterated over
     * @throws Exception\RuntimeException if no log writer specified
     */
    public function log($priority, $message, $extras = null)
    {
        //set writer
        $this->_setGeneralWriter();

        //if the priority is emergency, activate emergency process
        if($priority==self::EMERG){
            $sPath = $this->_basePath . '/emergency';
            //create top folder if not exists
            if(!file_exists($sPath)){
                mkdir($sPath, 0777, true);
            }
            //set additional data
            $aData = array();
            if(isset($_SERVER['REQUEST_URI'])){
                $aData[] = "Generated URL : {$_SERVER['REQUEST_URI']}";
            }
            //save message if an exception occured
            if($message instanceof \Exception){
                $aData[] = "Exception message : {$message->getMessage()}";
                $aData[] = "Exception file & line : {$message->getFile()} - {$message->getLine()}";
            }

            //create emergency log file
            $writer = new Stream($sPath . '/emerg.log');
            $formatter = new Simple('<b>%timestamp%</b>
                '. implode('<br/>', $aData) .'<br/>
                <span style="color: red;"><pre>%message%</pre></span><br/>
                <hr/><br/>
                %extra%' . PHP_EOL);
            $writer->setFormatter($formatter);

            $oLog = new Logger();
            $oLog->addWriter($writer);
            $oLog->emerg($message, $extras);
        }

        return parent::log($priority, $message, $extras);

    }

    /**
     * Set up writer
     */
    private function _setGeneralWriter()
    {
        //set path
        $this->_basePath = "tmp";
        $sPath = $this->_basePath . '/logs-' . date('Y') . '/' . date('m');
        //create top folder if not exists
        if(!file_exists($sPath)){
            mkdir($sPath, 0777, true);
        }

        //create log file if not exists
        $writer = new Stream($sPath . '/' . 'error.log');
        $this->addWriter($writer);
    }

}