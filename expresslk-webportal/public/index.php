<?php
/**
 * This makes our life easier when dealing with paths. Everything is relative
 * to the application root now.
 */
chdir(dirname(__DIR__));

// Decline static file requests back to the PHP built-in webserver
if (php_sapi_name() === 'cli-server' && is_file(__DIR__ . parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH))) {
    return false;
}

// Setup autoloading
require 'init_autoloader.php';

/**
 * register shut down function
 */
register_shutdown_function(function() {
    //get last error, see if it's a fatal error. If so, log the error.
    $error = error_get_last();
    if(is_array($error) && in_array($error['type'], array(
            E_ERROR, E_CORE_ERROR, E_COMPILE_ERROR, E_USER_ERROR, E_RECOVERABLE_ERROR))){
        //change directory
        chdir(dirname(__DIR__));

        //create log files for fatal errors
        $info = "File: ".$error['file']." | Line: ".$error['line']." | Type: ". $error['type'] ." | Message: ".$error['message'];
        $log = new \Application\Util\Log();
        $log->emerg($info);
    }

});

try {
    // Run the application!
    Zend\Mvc\Application::init(require 'config/application.config.php')->run();
} catch (\Exception $e) {
    echo $e->getMessage();
}
