<?php
/**
 * Global Configuration Override
 *
 * You can use this file for overriding configuration values from modules, etc.
 * You would place values in here that are agnostic to the environment and not
 * sensitive to security.
 *
 * @NOTE: In practice, this file will typically be INCLUDED in your source
 * control, so do not include passwords or other sensitive information in this
 * file.
 */

return array(
    'db' => array(
        'driver' => 'Pdo',
        'dsn' => 'mysql:dbname=express_bb;host=localhost',
        'username'         => 'root',
        'password'         => 'qwe123',
        'driver_options' => array(
            PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES \'UTF8\''
        ),
    ),
    'service_manager' => array(
        'factories' => array(
            'Zend\Db\Adapter\Adapter' => 'Zend\Db\Adapter\AdapterServiceFactory',
            //initiate zend chache
            'ZendCacheStorageFactory' => function() {
                return \Zend\Cache\StorageFactory::factory(
                    array(
                        'adapter' => array(
                            'name' => 'filesystem',
                            'options' => array(
                                'dirLevel' => 2,
                                'cache_dir' => __DIR__.'/../../data/cache',
                                'ttl' => 7200,
                                'dirPermission' => 0755,
                                'filePermission' => 0666,
                                'namespaceSeparator' => '-data-'
                            ),
                        ),
                        'plugins' => array('serializer'),
                    )
                );
            },
            //data store
            'DataStore' => function() {
                return \Zend\Cache\StorageFactory::factory(
                    array(
                        'adapter' => array(
                            'name' => 'filesystem',
                            'options' => array(
                                'dirLevel' => 2,
                                'cache_dir' => __DIR__.'/../../data/datastore',
                                'ttl' => 86400, //set for a date
                                'dirPermission' => 0755,
                                'filePermission' => 0666,
                                'namespaceSeparator' => '-data-'
                            ),
                        ),
                        'plugins' => array('serializer'),
                    )
                );
            },
        ),
        'aliases' => array(
            'cache' => 'ZendCacheStorageFactory',
        ),
    ),
);
