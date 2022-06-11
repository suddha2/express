<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 4/3/14
 * Time: 12:48 PM
 * Keep this only in qa
 */
return array(
    'db' => array(
        'driver' => 'Pdo',
        'dsn' => 'mysql:dbname=expresslk_bb_web;host=localhost',
        'username'         => 'root',
        'password'         => '',
        'driver_options' => array(
            PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES \'UTF8\''
        ),
    ),
    //configs for domains
    'domain' => array(
        'depot.busbooking.lk' => array(
            'site'         => \Application\Domain::NAME_BUSBOOKING,
            'siteConfigName'   => 'depot.busbooking.lk.php',
        ),
        'superline.busbooking.lk' => array(
            'site'         => \Application\Domain::NAME_SUPERLINE,
            'siteConfigName'   => 'depot.superline.lk.php',
        ),
        'surrexi.busbooking.lk' => array(
            'site'         => \Application\Domain::NAME_SURREXI,
            'siteConfigName'   => 'surrexi.lk.php',
        ),
		 'garage.sltb.express.lk' => array(
            'site'         => \Application\Domain::NAME_SLTB,
            'siteConfigName'   => 'garage.sltb.lk.php',
        ),
		'garage.ebertsilva.express.lk' => array(
            'site'         => \Application\Domain::NAME_EBERTSILVA,
            'siteConfigName'   => 'garage.ebertsilva.lk.php',
        ),
		 'garage.ntc.express.lk' => array(
            'site'         => \Application\Domain::NAME_NTC,
            'siteConfigName'   => 'garage.ntc.lk.php',
        ),
    ),
    'system' => array(
        'serverName' => 'depot-busbooking.lk',
        'debug' => true,
    ),
    'wsdl'  => array(
        'search' => 'http://localhost:7070/ExpressLK-search/search?wsdl',
        'reports' => 'http://localhost:7070/ExpressLK-reports/reports?wsdl',
    ),
    'rest'  => array(
        'v1'    => array(
            'admin' => 'http://localhost:7070/ExpressLK-admin/v1/internal/admin'
        )
    ),

    'dialog-ipg' => array(
        'requestUrl'    => 'https://ipg.dialog.lk/ezCashIPGExtranet/servlet_sentinal',
        'merchantCode'  => 'SIGMAFEET_MERCHANT',
        'returnUrl'     => '/app/payment/dialogezcash',

        'publicKey'     => '-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCW8KV72IMdhEuuEks4FXTiLU2o
bIpTNIpqhjgiUhtjW4Si8cKLoT7RThyOvUadsgYWejLg2i0BVz+QC6F7pilEfaVS
L/UgGNeNd/m5o/VoX9+caAIyu/n8gBL5JX6asxhjH3FtvCRkT+AgtTY1Kpjb1Btp
1m3mtqHh6+fsIlpH/wIDAQAB
-----END PUBLIC KEY-----',

        'privateKey'    => '-----BEGIN PRIVATE KEY-----
MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIqSmBh03JbLHJzd
MFe5EIy0N1AcnGolAZOZ+tQykUlagIThB2dg+D/TYJFfXmEP/rclMra7qA306r3O
cmajgr7p/IQaGHrtxxW+4q0sB5cDxJny2HL7EokiYmO9xf4zeN61Me0jNxReKz+6
oOnzj4cL2Pntagv+4k43RsTrwPttAgMBAAECgYAJSM8l/FZ3R/6PyAYgKocgVkbK
eFBGkFotsIhsxUKUvpqTzJv5Yjbf1+LFerVRHYg8byKZubMF1F4R/44MsHHnDU8Q
g2sE5Dd/f++M28RAjXa+c0VcM7v1GIGcgA0TUMtsPfUvkY7a4RSyj5ABG0c+pxbM
VBamYMmkgMjRj0HuxQJBANOt+dhyoY0U0rFuaCnaK0H1WLMrdPmgiW4Opr5HgJ5T
nh/9Uf5vWmSafCwbAMJ7P/V8lnEr19idZlmqIfTFfE8CQQCnlhX52SLSxNF074ev
u7cZPnCliez56kirmVlwgZ4QWdZz5ADV7rRSynHNsEG8DGk7eO6KPItIuVDmOL8t
lPGDAkA1GMO+Bmr9j4aWGSKptN766Yb5UmJCJ4GxiJ1Mx3S8wi68yXciwl3DNHkH
OzLCldXiRwEZnCzFWh4NSb7AvjTDAkA8P5fs9+QuE/YdbRPB2OGNoMYMrVZVzqWL
k5j0+pRh2npftaObRbLsZL2fLfOj8G5QpDxV1Ehav8zWx6XrdDWTAkAPsge0XmSW
YYGGHze5yFeLVda0svyTGk1ERPNqvBBiexl8WTF4BPjFo/geDYFhxuGw1IPGjuSO
T6KVzskRuTf2
-----END PRIVATE KEY-----',
    ),
);