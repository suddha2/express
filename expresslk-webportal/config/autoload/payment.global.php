<?php
/**
 * All the configurations related to payments go here
 */
return array(
    /**
     * Dialog EZ cash payment gateway
     */
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

    /**
     * Mobitel mCash payment gateway settings
     */
    'mobitel-ipg' => array(
        'tokenUrl'      => 'https://www.mcash.lk/ipg/auth/tokens.html',
        'redirectUrl'   => "https://www.mcash.lk/ipg/payment.html",
        'merchantId'    => 'NERV00-5999',
        'merchantMobile'=> '0712311297',
        'tokenPassword' => 'Busbooking#lk@09383838#',
        'initializeVecto' => '071cafabd6453219',
    ),

    /**
     * Sampath cc payment gateway
     */
    'sampath-ipg' => array(
        //old version (Enstage) gateway settings
        'v1' => array(
            'requestUrl'     => 'https://www.paystage.com/AccosaPG/verify.jsp',
            'pgInstanceId'  => '91941126',
            'merchantId'    => '57935411',
            'hashKey'       => '84803F07C37DE530',
        ),
        //Version 2 - PayCorp
        'v2' => array(
            'clientId'      => '14000291',
            'authToken'     => 'e96bc98b-ed0b-46ad-a29a-49cdde85a929',
            'HMACSecret'    => 'nzY40rZcTf2MJzX4',
            // 'endPoint'      => 'https://sampath.paycorp.com.au/rest/service/proxy',
            'endPoint'      => 'https://sampath.paycorp.lk/rest/service/proxy',
            'returnUrl'     => '/app/payment/sampathipgpaycorp',
        ),
		//Version 2 - PayCorp Test
        'v2-Test' => array(
            'clientId'      => '14002037',
            'authToken'     => 'e96bc98b-ed0b-46ad-a29a-49cdde85a929',
            'HMACSecret'    => 'nzY40rZcTf2MJzX4',
            'endPoint'      => 'https://sampath.paycorp.lk/rest/service/proxy',
			'returnUrl'     => '/app/payment/sampathipgpaycorp',
			
        ),
		//Version 2 - PayCorp Account for SLTB White label site
        'v2-SLTB' => array(
            'clientId'      => '14002094',
            'authToken'     => 'e96bc98b-ed0b-46ad-a29a-49cdde85a929',
            'HMACSecret'    => 'nzY40rZcTf2MJzX4',
            'endPoint'      => 'https://sampath.paycorp.lk/rest/service/proxy',
			'returnUrl'     => '/app/payment/sampathipgpaycorp',
        ),
		//Version 2 - PayCorp Account for NDB Card promotions via Sampath IPG
        'v2-NDB-PROMO' => array(
            'clientId'      => '14002590',
            'authToken'     => 'e96bc98b-ed0b-46ad-a29a-49cdde85a929',
            'HMACSecret'    => 'nzY40rZcTf2MJzX4',
            'endPoint'      => 'https://sampath.paycorp.lk/rest/service/proxy',
			'returnUrl'     => '/app/payment/sampathipgpaycorp',
        )
    ),

    /**
     * PayPal Express Checkout
     */
    'paypal-ec' => array(
        'returnUrl'     => '/app/payment/paypal',
        'username'      => 'payments-facilitator_api1.express.lk',
        'password'      => 'YM5EZUPDNA5U9UK9',
        'signature'     => 'AFcWxV21C7fd0v3bYYYRCpSSRl31AnAtkht2W.buGtfhcEEMzO-sE-xp',
        'endpoint'      => 'https://api-3t.sandbox.paypal.com/nvp',
        'redirectUrl'   => 'https://www.sandbox.paypal.com/checkoutnow',
        'exchangeRate'  => 100,
     ),

    /**
     * HNB ipg
     */
    'hnb-ipg' => array(
        'sentry' => array(
            'endPoint'      => 'https://www.hnbpg.hnb.lk/SENTRY/PaymentGateway/Application/ReDirectLink.aspx',
            'password'      => 'g1CT5q2g',
            'merchantNo'    => '13925900',
            'acquirerId'    => '415738',
            'currency'      => '144',
            'version'       => '1.0.0',
            'domainName'    => 'www.busbooking.lk', //hnb only supports https. Only allow https enabled production domains here.
            'returnUrl'     => '/app/payment/hnbipg',
        )
    ),
	'payhere'=>array(
		'merchant_id' => '1212948',
		'return_url'  => '/app/payment/payhereview',
		'cancel_url'  => '/app/payment/payhereview',
		'notify_url'  => '/app/payment/payhere',
		'endPoint'	  => 'https://sandbox.payhere.lk/pay/checkout', 
		'merchant_secret'  => 'Exprex418Sandbox',
	),
	'people-ipg' => array(
		'Version'=>'1.0.0',
		'MerID'=>'1000000000041',
		'AcqID'=>'512940',
		'Password'=>'p4(L64yZ',
		'MerRespURL'=>'/app/payment/peopleipg',
		'PurchaseCurrency'=>'144',
		'PurchaseCurrencyExponent'=>'2',
		'SignatureMethod'=>'SHA1',
		'endPoint'=>'https://pgtd.peoplesbank.lk/OrderProcessingEngine/RedirectLink.aspx',
	),
);