<?php
/**
 * Live www.busbooking.lk configurations
 * User: udantha
 * Date: 4/3/14
 * Time: 12:48 PM
 * Keep this only in qa
 */
return array(
    'db' => array(
        'driver' => 'Pdo',
        'dsn' => 'mysql:dbname=expresslk_bb_web;host=express-db.cm8ispqmkrpk.ap-southeast-1.rds.amazonaws.com',
        'username'         => '',
        'password'         => '',
        'driver_options' => array(
            PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES \'UTF8\''
        ),
    ),
    //configs for domains
    'domain' => array(
        'www.busbooking.lk' => array(
            'site'         => \Application\Domain::NAME_BUSBOOKING,
            'siteConfigName'   => 'busbooking.lk.php',
        ),
        'superline.express.lk' => array(
            'site'         => \Application\Domain::NAME_SUPERLINE,
            'siteConfigName'   => 'superline.lk.php',
        ),
        'surrexi.express.lk' => array(
            'site'         => \Application\Domain::NAME_SURREXI,
            'siteConfigName'   => 'surrexi.lk.php',
        ),
        'www.surrexi.lk' => array(
            'site'         => \Application\Domain::NAME_SURREXI,
            'siteConfigName'   => 'surrexi.lk.php',
        ),
        '52.220.116.44' => array(
            'site'         => \Application\Domain::NAME_MOBITEL,
            'siteConfigName'   => 'mobitel.cgw.php',
        ),
    ),
    'system' => array(
        'serverName' => 'www.busbooking.lk',
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
    'api' => array(
        'auth' => array(
            'username' => 'bbk',
            'password' => 'vS0bgsikVKSLnQ0JtcuHqie791wOVImF7SFAGpMGgn8T8vKWcZSjhGGzCtrM',
        ),
    ),
    //payment gateway overwrides
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

    'mobitel-ipg' => array(
        'tokenUrl'      => 'https://ipg.mobitel.lk/mcash/auth/tokens.html',
        'redirectUrl'   => "https://ipg.mobitel.lk/mcash/payment.html",
        'merchantId'    => 'NERV00-5999',
        'merchantMobile'=> '0712311297',
        'tokenPassword' => '375@B023Us#bookIng@38TUvx69',
        'initializeVecto' => '071cafabd6453219',
    ),

    'paypal-ec' => array(
        'returnUrl'     => '/app/payment/paypal',
        'username'      => 'payments_api1.express.lk',
        'password'      => 'L37AKFJLXR6CSJNF',
        'signature'     => 'AFcWxV21C7fd0v3bYYYRCpSSRl31AOrF3TsW2ofUrzrOXZjy9Ck4az01',
        'endpoint'      => 'https://api-3t.paypal.com/nvp',
        'redirectUrl'   => 'https://www.paypal.com/checkoutnow',
        'exchangeRate'  => 100,
     ),
);