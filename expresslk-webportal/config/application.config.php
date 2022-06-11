<?php
/**
 * Dynamic module loading based on
 * @see https://framework.zend.com/manual/2.4/en/tutorials/config.advanced.html
 * Add
 * SetEnv "APP_ENV" "development"
 * in apache config to enable development mode.
 *
 * Enable Developer tools:
 * cp ./vendor/zendframework/zend-developer-tools/config/zenddevelopertools.local.php.dist ./config/autoload/zenddevelopertools.local.php
 */
$env = getenv('APP_ENV') ?: 'production';

// Use the $env value to determine which modules to load
$modules = array(
    'Data',
    'Legacy',
    'Application',
    'ZFTool',
    'Api',
    'Admin',
    'DOMPDFModule',
    'Superline',
	'Sltb',
	'Ebertsilva',
	'Ntc',
	'Cron',
    'App\Base',
    'App\Ticketing',
    'Web\Surrexi',
);

//if environment is dev, add dev tools
if ($env == 'development') {
    // $modules[] = 'ZfSnapEventDebugger';
    // $modules[] = 'ZendDeveloperTools';
}

return array(
    'modules' => $modules,

    'module_listener_options' => array(
        'module_paths' => array(
            './module',
            './vendor',
        ),
        'config_glob_paths' => array(
            'config/autoload/{{,*.}global,{,*.}server,{,*.}local}.php',
        ),
    ),
);
