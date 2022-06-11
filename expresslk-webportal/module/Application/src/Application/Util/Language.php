<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 8/8/14
 * Time: 9:44 AM
 */

namespace Application\Util;


use Zend\Mvc\MvcEvent;
use Zend\Session\Container;

class Language {

    const SI_LOCALE = 'si_LK';
    const EN_LOCALE = 'en_US';
    const TA_LOCALE = 'ta_LK';

    /**
     * Returns the current locale
     * @return mixed
     */
    public static function getCurrentLocale()
    {
        return (isset($_COOKIE['locale']) && !empty($_COOKIE['locale']))? $_COOKIE['locale'] : false;
    }

    public static function setCurrentLocale($locale)
    {
        //set to expire in one year
        setcookie('locale', $locale, (time()+31536000), '/');
    }

    /**
     * Get language code from locale constant or current locale
     * @param $locale
     * @return mixed
     */
    public static function getLocaleLang($locale)
    {
        if(!is_string($locale)){
            return false;
        }

        list($lang, $countryCode) = explode('_', $locale);
        return $lang;
    }

    /**
     * @param MvcEvent $e
     * @param null $preferredLang
     */
    public static function setLanguage($e, $preferredLang = null)
    {
        $translator = $e->getApplication()->getServiceManager()->get('translator');

        //set defaults
        $lang = Language::EN_LOCALE; // default language

        //set language from session if exists
        $savedLoc = self::getCurrentLocale();

        if(!empty($preferredLang)){
            $lang = $preferredLang;
        }
        elseif ($savedLoc!==false) {
            $lang = $savedLoc; // current language locale
        }
        else{
            //set language from the browser
            $bLang = self::getLanguageFromHeader();
            if($bLang!==false){
                $lang = $bLang;
            }
        }

        self::setCurrentLocale($lang);
        //set locale in translator
        $translator
            ->setLocale($lang)
            ->setFallbackLocale(Language::EN_LOCALE);
    }

    /**
     * @return bool|string
     */
    public static function getLanguageFromHeader()
    {
        //set language from the browser
        if(isset($_SERVER['HTTP_ACCEPT_LANGUAGE']) && !empty($_SERVER['HTTP_ACCEPT_LANGUAGE'])){
            // only get the first two characters
            $langCode = strtolower(substr($_SERVER['HTTP_ACCEPT_LANGUAGE'], 0, 2));
            //get locale based on browser language
            $lang = self::_getLocaleFromLangCode($langCode);

            return $lang;
        }
        return false;
    }

    /**
     * Get language code
     * @param $langCode
     * @return string
     */
    private static function _getLocaleFromLangCode($langCode)
    {
        switch($langCode){
            //sinhala
            case 'si' :
                return self::SI_LOCALE;
            //tamil
            case 'ta' :
                return self::TA_LOCALE;
            default :
                return self::EN_LOCALE;
        }
    }

} 