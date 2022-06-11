/* 
 * Initialize angular modules here
 * 
 */
//define core app to share common components
var expressCoreApp = angular.module('expressCore', [
    'ui.router',
    'ui.bootstrap',
    //'ui.select',
    'ngSanitize',
    'ngAnimate'
]);

/**
 * Disable debug infor for Angular performance boost
 * @link https://code.angularjs.org/1.3.19/docs/guide/production
 */
expressCoreApp.config(['$compileProvider', function ($compileProvider) {
    $compileProvider.debugInfoEnabled(false);
}]);

//add momentjs timezone and set default to colombo
try{
    //borrowed from momentjs/timezone repo
    moment.tz.add("Asia/Colombo|MMT IST IHST IST LKT LKT|-5j.w -5u -60 -6u -6u -60|01231451|-2zOtj.w 1rFbN.w 1zzu 7Apu 23dz0 11zu n3cu|22e5");
    //set default timezone
    moment.tz.setDefault("Asia/Colombo");
}catch (e){
    console.log(e)
}